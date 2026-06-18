package io.quarkiverse.sshd;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.simple.SimpleClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/sshd")
@ApplicationScoped
public class SshdResource {

    @ConfigProperty(name = "quarkiverse.sshd.host")
    String host;

    @ConfigProperty(name = "quarkiverse.sshd.port")
    int port;

    @GET
    @Path("/session")
    public Response openSession() throws IOException {
        try (SimpleClient client = SshClient.setUpDefaultSimpleClient()) {
            try (ClientSession session = client.sessionLogin(host, port, "anonymous", "anonymous")) {
                return Response.ok("connected").build();
            }
        }
    }

    @GET
    @Path("/eddsa/session")
    public Response openEdDsaSession() throws IOException {
        try (SimpleClient client = SshClient.setUpDefaultSimpleClient()) {
            try (ClientSession session = client.sessionLogin(host, port, "anonymous", "anonymous")) {
                return Response.ok(session.getServerKey().getAlgorithm()).build();
            }
        }
    }
}
