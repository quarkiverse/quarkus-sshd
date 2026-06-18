package io.quarkiverse.sshd;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = SshdServerTestResource.class, restrictToAnnotatedClass = true)
public class SmokeTest {

    @Test
    void shouldOpenSession() {
        given()
                .when().get("/sshd/session")
                .then()
                .statusCode(200)
                .body(equalTo("connected"));
    }
}
