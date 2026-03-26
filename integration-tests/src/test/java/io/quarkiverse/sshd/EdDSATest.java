package io.quarkiverse.sshd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.simple.SimpleClient;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = EdDSAServerTestResource.class, restrictToAnnotatedClass = true)
public class EdDSATest {

    @Test
    void shouldOpenSessionWithEdDSAHostKey() throws IOException {
        String host = ConfigProvider.getConfig().getValue("quarkiverse.sshd.eddsa.host", String.class);
        int port = ConfigProvider.getConfig().getValue("quarkiverse.sshd.eddsa.port", Integer.class);
        try (SimpleClient client = SshClient.setUpDefaultSimpleClient()) {
            try (ClientSession clientSession = client.sessionLogin(host, port, "anonymous", "anonymous")) {
                assertThat(clientSession).isNotNull();
                assertThat(clientSession.getServerKey().getAlgorithm()).isIn("EdDSA", "Ed25519");
            }
        }
    }
}
