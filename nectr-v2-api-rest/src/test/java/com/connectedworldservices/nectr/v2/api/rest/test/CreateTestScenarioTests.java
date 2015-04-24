package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
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
import com.connectedworldservices.nectr.v2.api.rest.service.NeCTRv2Exception;
import com.connectedworldservices.nectr.v2.api.rest.service.TestScenarioService;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class CreateTestScenarioTests extends AbstractRestAssuredTest {

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

        //all tests have mocked integration data
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

        //clear mock flags
        mongoTemplate.clearThrowExceptionOnSave();

        mongoConnector.clearThrowUnknownHostExceptionOnIntegrationConnection();
        mongoConnector.clearThrowUnknownHostExceptionOnApplicationConnection();

        //majority of tests don't have an application host enabled
        mongoConnector.setApplicationHostEnabled(false);
    }

    //@formatter:off
    @Test
    public void should_return_test_scenario_info_without_metadata() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TTTT-TEST-0001"))
        .body("environmentId", equalTo("TEST"))
        .body("carrier", equalTo("SPRINT"))
        .body("country", equalTo("TT"))
        .body("clientId", equalTo("TT"))
        .body("journeyName", equalTo("new-connection"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body(not(hasKey("metadata")))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_test_scenario_info_without_without_journey_name() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TTTT-TEST-0001"))
        .body("environmentId", equalTo("TEST"))
        .body("carrier", equalTo("SPRINT"))
        .body("country", equalTo("TT"))
        .body("clientId", equalTo("TT"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body(not(hasKey("metadata")))
        .when()
        .post("/tests");
    }
    @Test
    public void should_return_test_scenario_info_when_application_host_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TTTT-TEST-0001"))
        .body("environmentId", equalTo("TEST"))
        .body("carrier", equalTo("SPRINT"))
        .body("country", equalTo("TT"))
        .body("clientId", equalTo("TT"))
        .body("journeyName", equalTo("new-connection"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body(not(hasKey("metadata")))
        .when()
        .post("/tests");
    }
    @Test
    public void should_return_test_scenario_info_with_metadata() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\",\"metadata\": {\"portIn\":false,\"custom\":\"business\"}}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TTTT-TEST-0001"))
        .body("environmentId", equalTo("TEST"))
        .body("carrier", equalTo("SPRINT"))
        .body("country", equalTo("TT"))
        .body("clientId", equalTo("TT"))
        .body("journeyName", equalTo("new-connection"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body("metadata", hasEntry("portIn", false))
        .body("metadata", hasEntry("custom", "business"))
        .when()
        .post("/tests");
    }

    @Test
    public void should_increment_test_scenario_id() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TTTT-TEST-0001"))
        .body("environmentId", equalTo("TEST"))
        .body("carrier", equalTo("SPRINT"))
        .body("country", equalTo("TT"))
        .body("clientId", equalTo("TT"))
        .body("journeyName", equalTo("new-connection"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body(not(hasKey("metadata")))
        .when()
        .post("/tests");

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TTTT-TEST-0002"))
        .body("environmentId", equalTo("TEST"))
        .body("carrier", equalTo("SPRINT"))
        .body("country", equalTo("TT"))
        .body("clientId", equalTo("TT"))
        .body("journeyName", equalTo("new-connection"))
        .body("journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body(not(hasKey("metadata")))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_environment_id_is_missing() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_environment_id_is_unknown() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"UNKNOWN\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_journey_name_is_missing_and_applicatioh_host_is_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_journey_id_is_missing() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_carrier_is_missing() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_client_id_is_missing() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_country_is_missing() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_journey_is_not_found() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"some-journey\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb21\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_application_data_not_found() throws Exception {
        //for this test we enable the application host without any data so it can fail
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_test_id_is_duplicate() throws Exception {
        testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());

        //reset sequence so we can generate the same test id
        mongoTemplate.dropCollection(Sequence.COLLECTION);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_persistence_fails_on_save_test_scenario() throws Exception {
        mongoTemplate.setThrowExceptionOnSave(true, TestScenario.class);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_persistence_fails_with_enabled_application_host() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);
        mongoTemplate.setThrowExceptionOnSave(true, TestScenario.class);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_application_data_doesnt_exist_and_application_host_is_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb27\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_connector_fails_to_connect_to_integration_host() throws Exception {
        mongoConnector.setThrowUnknownHostExceptionOnIntegrationConnection(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }

    @Test
    public void should_return_http_500_when_connector_fails_to_connect_to_application_host() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);
        mongoConnector.setThrowUnknownHostExceptionOnApplicationConnection(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"environmentId\":\"TEST\",\"carrier\":\"SPRINT\",\"country\":\"TT\",\"clientId\":\"TT\",\"journeyName\":\"new-connection\",\"journeyId\":\"705e98d5-7d16-43b4-9a78-fddc767cbb25\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .post("/tests");
    }
    //@formatter:on
}
