package io.quarkiverse.sshd;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.hostbased.AcceptAllHostBasedAuthenticator;
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.UnknownCommandFactory;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class SshdServerTestResource implements QuarkusTestResourceLifecycleManager {

    private SshServer sshd;

    @Override
    public Map<String, String> start() {
        try {
            sshd = SshServer.setUpDefaultServer();
            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Files.createTempFile("host", "key")));
            sshd.setHostBasedAuthenticator(AcceptAllHostBasedAuthenticator.INSTANCE);
            sshd.setPasswordAuthenticator(AcceptAllPasswordAuthenticator.INSTANCE);
            sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
            sshd.setCommandFactory(UnknownCommandFactory.INSTANCE);
            sshd.setIoServiceFactoryFactory(new Nio2ServiceFactoryFactory());
            sshd.setHost("localhost");
            sshd.start();
            return Map.of(
                    "quarkiverse.sshd.host", sshd.getHost(),
                    "quarkiverse.sshd.port", Integer.toString(sshd.getPort()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void stop() {
        try {
            sshd.stop(true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
