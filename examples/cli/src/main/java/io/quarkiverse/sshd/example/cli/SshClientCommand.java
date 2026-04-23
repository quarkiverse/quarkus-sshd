package io.quarkiverse.sshd.example.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.util.io.input.NoCloseInputStream;
import org.apache.sshd.common.util.io.output.NoCloseOutputStream;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * SSH client command. Connects to an SSH server and runs a command or opens an interactive shell.
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * # Interactive shell
 * java -jar ssh-cli.jar client alice@localhost -p 2222 -w alice
 *
 * # Execute a command
 * java -jar ssh-cli.jar client alice@localhost -p 2222 -w alice -- echo hello
 *
 * # Use a private key
 * java -jar ssh-cli.jar client alice@localhost -p 2222 -i ~/.ssh/id_rsa -- ls -la
 * </pre>
 */
@Command(name = "client", description = "Connect to an SSH server", mixinStandardHelpOptions = true)
public class SshClientCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "[user@]hostname")
    String target;

    @Parameters(index = "1..*", description = "Remote command to execute (optional; if omitted, opens an interactive shell)")
    List<String> command;

    @Option(names = { "-p",
            "--port" }, description = "Remote port (default: ${DEFAULT-VALUE})", defaultValue = "22")
    int port;

    @Option(names = { "-l", "--login" }, description = "Login username (overrides user@host)")
    String login;

    @Option(names = { "-w", "--password" }, description = "Password for authentication")
    String password;

    @Option(names = { "-i", "--identity" }, description = "Path to private key file")
    String identityFile;

    @Option(names = {
            "--no-strict-host-key-checking" }, description = "Disable host-key verification (insecure; for testing only)", defaultValue = "false")
    boolean noStrictHostKeyChecking;

    @Override
    public Integer call() throws Exception {
        String username = login;
        String hostname = target;

        // Support user@host shorthand
        int atSignIndex = target.indexOf('@');
        if (atSignIndex > 0) {
            if (username == null) {
                username = target.substring(0, atSignIndex);
            }
            hostname = target.substring(atSignIndex + 1);
        }

        if (username == null) {
            username = System.getProperty("user.name");
        }

        try (SshClient client = SshClient.setUpDefaultClient()) {
            if (noStrictHostKeyChecking) {
                client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
            }

            if (identityFile != null) {
                client.setKeyIdentityProvider(new FileKeyPairProvider(Paths.get(identityFile)));
            }

            client.start();

            System.out.printf("Connecting to %s@%s:%d%n", username, hostname, port);

            try (ClientSession session = client.connect(username, hostname, port)
                    .verify(10, TimeUnit.SECONDS)
                    .getSession()) {

                if (password != null) {
                    session.addPasswordIdentity(password);
                }

                session.auth().verify(30, TimeUnit.SECONDS);

                if (command == null || command.isEmpty()) {
                    return runShell(session);
                } else {
                    return runCommand(session, String.join(" ", command));
                }
            }
        }
    }

    private int runShell(ClientSession session) throws IOException {
        try (ChannelShell channel = session.createShellChannel()) {
            channel.setIn(new NoCloseInputStream(System.in));
            try (OutputStream out = new NoCloseOutputStream(System.out);
                    OutputStream err = new NoCloseOutputStream(System.err)) {
                channel.setOut(out);
                channel.setErr(err);
                channel.open().verify(10, TimeUnit.SECONDS);
                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
            }
        }
        return 0;
    }

    private int runCommand(ClientSession session, String cmd) throws IOException {
        try (ClientChannel channel = session.createExecChannel(cmd)) {
            try (OutputStream out = new NoCloseOutputStream(System.out);
                    OutputStream err = new NoCloseOutputStream(System.err)) {
                channel.setOut(out);
                channel.setErr(err);
                channel.open().verify(10, TimeUnit.SECONDS);
                Collection<ClientChannelEvent> events = channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
                Integer exitStatus = channel.getExitStatus();
                if (events.contains(ClientChannelEvent.EXIT_STATUS) && exitStatus != null) {
                    return exitStatus;
                }
            }
        }
        return 0;
    }
}
