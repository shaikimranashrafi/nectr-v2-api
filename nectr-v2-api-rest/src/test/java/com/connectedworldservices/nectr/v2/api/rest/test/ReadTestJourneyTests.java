package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
import com.connectedworldservices.nectr.v2.api.rest.model.IntegrationData;
import com.connectedworldservices.nectr.v2.api.rest.model.Sequence;
import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.service.NeCTRv2Exception;
import com.connectedworldservices.nectr.v2.api.rest.service.NotFoundException;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class ReadTestJourneyTests extends AbstractRestAssuredTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    FongoConnector mongoConnector;

    @Autowired
    MockTemplate mongoTemplate;

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

        //clear mock flags
        mongoConnector.clearThrowUnknownHostExceptionOnIntegrationConnection();

    }

    //@formatter:off
    @Test
    public void should_return_test_journey_when_journey_id_exists() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("environmentId", equalTo("TEST"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body("data", hasSize(2))
        .when()
        .get("/journeys/TEST/705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_http_404_when_journey_id_is_unknown() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(404)
        .body("exception", equalTo(NotFoundException.class.getCanonicalName()))
        .when()
        .get("/journeys/TEST/unknown");
    }

    @Test
    public void should_return_http_500_when_environment_id_is_unknown() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .get("/journeys/UNKNOWN/705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_http_500_when_it_cant_connect_to_integration_host() throws Exception {
        mongoConnector.setThrowUnknownHostExceptionOnIntegrationConnection(true);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .get("/journeys/TEST/705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }
    //@formatter:on
}
