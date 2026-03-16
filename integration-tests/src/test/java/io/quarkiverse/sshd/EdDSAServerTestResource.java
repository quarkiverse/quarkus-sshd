package io.quarkiverse.sshd;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.hostbased.AcceptAllHostBasedAuthenticator;
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.shell.UnknownCommandFactory;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class EdDSAServerTestResource implements QuarkusTestResourceLifecycleManager {

    private SshServer sshd;

    @Override
    public Map<String, String> start() {
        try {
            Path keyPath = Paths.get("target/key_ed25519.pem");
            try (InputStream stream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("edDSA/key_ed25519.pem")) {
                if (stream == null) {
                    throw new RuntimeException("Failed to load edDSA/key_ed25519.pem");
                }
                Files.write(keyPath, stream.readAllBytes());
            }

            sshd = SshServer.setUpDefaultServer();
            sshd.setKeyPairProvider(new FileKeyPairProvider(keyPath));
            sshd.setHostBasedAuthenticator(AcceptAllHostBasedAuthenticator.INSTANCE);
            sshd.setPasswordAuthenticator(AcceptAllPasswordAuthenticator.INSTANCE);
            sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
            sshd.setCommandFactory(UnknownCommandFactory.INSTANCE);
            sshd.setIoServiceFactoryFactory(new Nio2ServiceFactoryFactory());
            sshd.setHost("localhost");
            sshd.start();
            return Map.of(
                    "quarkiverse.sshd.eddsa.host", sshd.getHost(),
                    "quarkiverse.sshd.eddsa.port", Integer.toString(sshd.getPort()));
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
