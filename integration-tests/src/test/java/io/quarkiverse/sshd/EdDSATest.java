package io.quarkiverse.sshd;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = EdDSAServerTestResource.class, restrictToAnnotatedClass = true)
public class EdDSATest {

    @Test
    void shouldOpenSessionWithEdDSAHostKey() {
        given()
                .when().get("/sshd/eddsa/session")
                .then()
                .statusCode(200)
                .body(containsString("Ed"));
    }
}
