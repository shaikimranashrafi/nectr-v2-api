package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import java.util.Collections;

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
import com.connectedworldservices.nectr.v2.api.rest.service.NotFoundException;
import com.connectedworldservices.nectr.v2.api.rest.service.TestScenarioService;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class ReadTestScenarioTests extends AbstractRestAssuredTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    FongoConnector mongoConnector;

    @Autowired
    MockTemplate mongoTemplate;

    @Autowired
    TestScenarioService testScenarioService;

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
    public void should_return_test_scenario_when_test_id_exists() throws Exception {
        testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("id", equalTo("TTTT-TEST-0001"))
        .body("environmentId", equalTo("TEST"))
        .body("journeyName", equalTo("new-connection"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body(not(hasKey("metadata")))
        .body("integration", not(empty()))
        .body("integration.data", hasSize(2))
        .body(not(hasKey("application")))
        .when()
        .get("/tests/TTTT-TEST-0001");
    }



    @Test
    public void should_return_test_scenario_when_test_id_exists_and_application_host_is_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);
        testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("id", equalTo("TTTT-TEST-0001"))
        .body("environmentId", equalTo("TEST"))
        .body("journeyName", equalTo("new-connection"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body(not(hasKey("metadata")))
        .body("integration", not(empty()))
        .body("integration.data", hasSize(2))
        .body("application", not(empty()))
        .body("application.data", not(empty()))
        .when()
        .get("/tests/TTTT-TEST-0001");
    }

    @Test
    public void should_return_http_404_when_test_id_is_unknown() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(404)
        .body("exception", equalTo(NotFoundException.class.getCanonicalName()))
        .when()
        .get("/tests/TTTT-TEST-0001");
    }
    //@formatter:on
}
