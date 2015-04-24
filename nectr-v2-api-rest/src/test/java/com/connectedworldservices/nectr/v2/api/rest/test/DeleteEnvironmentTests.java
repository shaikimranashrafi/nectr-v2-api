package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.connectedworldservices.nectr.v2.api.rest.TestNeCTRv2;
import com.connectedworldservices.nectr.v2.api.rest.mock.FongoConnector;
import com.connectedworldservices.nectr.v2.api.rest.mock.MockTemplate;
import com.connectedworldservices.nectr.v2.api.rest.model.ApplicationData;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment;
import com.connectedworldservices.nectr.v2.api.rest.model.IntegrationData;
import com.connectedworldservices.nectr.v2.api.rest.model.Sequence;
import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.model.Environment.Host;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;
import com.connectedworldservices.nectr.v2.api.rest.service.EnvironmentService;
import com.connectedworldservices.nectr.v2.api.rest.service.NotFoundException;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class DeleteEnvironmentTests extends AbstractRestAssuredTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    FongoConnector mongoConnector;

    @Autowired
    MockTemplate mongoTemplate;

    @Autowired
    EnvironmentService environmentService;

    @Before
    public void setup() throws Exception {
        //set dynamic port in rest assured
        RestAssured.port = port;

        mongoConnector.loadIntegrationData("sample-integration-data.json", "integration", true);
        mongoConnector.loadApplicationData("sample-application-data.json", "new-connection", true);
    }

    @After
    public void tearDown() throws Exception {
        //clear nectr db state
        mongoTemplate.dropCollection(Sequence.COLLECTION);
        mongoTemplate.dropCollection(TestScenario.COLLECTION);
        mongoTemplate.dropCollection(ApplicationData.COLLECTION);
        mongoTemplate.dropCollection(IntegrationData.COLLECTION);

        //majority of tests don't have an application host enabled
        mongoConnector.setApplicationHostEnabled(false);
    }

    //@formatter:off
    @Test
    public void should_delete_environment_when_id_exists() throws Exception {
        environmentService.createEnvironment(new Environment("TEST-ENV", new Host("integration:27017", "integration", "integration")));

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(204)
        .when()
        .delete("/environments/TEST-ENV");

        assertFalse(mongoTemplate.exists(Queries.searchById("TEST-ENV"), Environment.class));
    }

    @Test
    public void should_return_http_404_when_environment_id_is_unknown() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(404)
        .body("exception", equalTo(NotFoundException.class.getCanonicalName()))
        .when()
        .delete("/environments/TEST-ENV");
    }

    @Test
    public void should_return_http_500_when_environment_id_is_default_environment() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .delete("/environments/TEST");
    }
    //@formatter:on
}
