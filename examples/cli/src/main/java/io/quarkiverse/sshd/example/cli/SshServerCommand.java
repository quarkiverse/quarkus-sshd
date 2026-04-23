package io.quarkiverse.sshd.example.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.shell.ProcessShellCommandFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * SSH server command. Starts an SSH server on a configurable port.
 *
 * <p>
 * Password authentication: username must equal password (for demo purposes only).
 * Public-key authentication: all keys accepted.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * java -jar ssh-cli.jar server -p 2222
 * </pre>
 *
 * Then connect with:
 *
 * <pre>
 * java -jar ssh-cli.jar client alice@localhost -p 2222 -w alice
 * </pre>
 */
@Command(name = "server", description = "Start an SSH server", mixinStandardHelpOptions = true)
public class SshServerCommand implements Callable<Integer> {

    @Option(names = { "-p",
            "--port" }, description = "Port to listen on (default: ${DEFAULT-VALUE})", defaultValue = "2222")
    int port;

    @Option(names = { "-b",
            "--bind" }, description = "Address to bind to (default: ${DEFAULT-VALUE})", defaultValue = "localhost")
    String bindAddress;

    @Option(names = {
            "--host-key" }, description = "Path to host key file; generated if absent (default: ${DEFAULT-VALUE})", defaultValue = "hostkey.ser")
    String hostKey;

    @Override
    public Integer call() throws Exception {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setHost(bindAddress);

        Path keyPath = Paths.get(hostKey);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(keyPath));

        // For demo purposes: password auth where username == password.
        // Replace with a real authenticator in production!
        sshd.setPasswordAuthenticator((username, password, session) -> username.equals(password));

        // Accept all public keys for demo; use a real verifier in production
        sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);

        // Use the system shell for interactive sessions and process execution
        sshd.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
        sshd.setCommandFactory(new ProcessShellCommandFactory());

        System.out.printf("Starting SSH server on %s:%d%n", bindAddress, port);
        System.out.printf("Host key: %s%n", keyPath.toAbsolutePath());
        System.out.println("Auth: password (username == password) or any public key");
        System.out.println("Press Ctrl+C to stop.");

        sshd.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping SSH server...");
            try {
                sshd.stop(true);
            } catch (IOException e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }));

        // Block indefinitely until interrupted
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return 0;
    }
}
