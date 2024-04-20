package io.quarkiverse.sshd;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.apache.sshd.common.util.security.SecurityUtils;

@Path("/sshd")
public class SshdResource {

    @GET
    public boolean get() throws Exception {
        System.out.println(SecurityUtils.getRegisteredProviders());
        return SecurityUtils.isEDDSACurveSupported();
    }
}
