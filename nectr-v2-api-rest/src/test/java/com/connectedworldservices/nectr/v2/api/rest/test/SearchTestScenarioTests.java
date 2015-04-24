package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

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
import com.connectedworldservices.nectr.v2.api.rest.model.dto.TestScenarioInfo;
import com.connectedworldservices.nectr.v2.api.rest.service.NeCTRv2Exception;
import com.connectedworldservices.nectr.v2.api.rest.service.TestScenarioService;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class SearchTestScenarioTests extends AbstractRestAssuredTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    FongoConnector mongoConnector;

    @Autowired
    MockTemplate mongoTemplate;

    @Autowired
    TestScenarioService testScenarioService;

    //@formatter:off

    @Before
    public void setup() throws Exception {
        //set dynamic port in rest assured
        RestAssured.port = port;

        mongoConnector.loadIntegrationData("sample-integration-data.json", "integration", true);
        mongoConnector.loadApplicationData("sample-application-data.json", "new-connection", true);

        mongoConnector.setApplicationHostEnabled(true);

        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("description", "this is a test scenario");
        metadata.put("channel", "store");
        metadata.put("deposit", "no");
        metadata.put("portIn", "no");
        metadata.put("boolean", false);
        metadata.put("integer", 1);

        testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", metadata);

        metadata = new HashMap<String, Object>();
        metadata.put("description", "this is another test scenario");
        metadata.put("channel", "store");
        metadata.put("deposit", "yes");
        metadata.put("portIn", "yes");

        testScenarioService.createTestScenario("TEST", "BELL", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb26", metadata);

    }

    @After
    public void tearDown() throws Exception {
        //clear nectr db state
        mongoTemplate.clearThrowExceptionOnFind();
        mongoTemplate.dropCollection(Sequence.COLLECTION);
        mongoTemplate.dropCollection(TestScenario.COLLECTION);
        mongoTemplate.dropCollection(ApplicationData.COLLECTION);
        mongoTemplate.dropCollection(IntegrationData.COLLECTION);
    }

    @Test
    public void should_return_empty_when_criteria_is_empty() throws Exception {

        given()
            .contentType(ContentType.JSON)
       .expect()
           .statusCode(200)
           .body("$", hasSize(0))
       .when()
           .get("/tests/search");
    }

    @Test
    public void should_return_empty_when_criteria_is_empty_but_query_parameter_present() throws Exception {

        given()
            .contentType(ContentType.JSON)
       .expect()
           .statusCode(200)
           .body("$", hasSize(0))
       .when()
           .get("/tests/search?q=");
    }

    @Test
    public void should_return_empty_when_criteria_value_is_empty() throws Exception {

        given()
            .contentType(ContentType.JSON)
       .expect()
           .statusCode(200)
           .body("$", hasSize(0))
       .when()
           .get("/tests/search?=id:");
    }

    @Test
    public void should_return_empty_when_test_scenario_does_not_exist() throws Exception {

        given()
            .contentType(ContentType.JSON)
       .expect()
           .statusCode(200)
           .body("$", hasSize(0))
       .when()
           .get("/tests/search?q=id:INVALID-TEST-0001");
    }

    @Test
    public void should_return_empty_when_mongodb_throws_exception() throws Exception {

        mongoTemplate.setThrowExceptionOnFind(true, TestScenarioInfo.class);

        given()
            .contentType(ContentType.JSON)
       .expect()
           .statusCode(200)
           .body("$", hasSize(0))
       .when()
           .get("/tests/search?q=id:INVALID-TEST-0001");
    }

    @Test
    public void should_return_one_test_scenario_when_criteria_is_one_equals_predicate() throws UnknownHostException, NeCTRv2Exception {

        given()
            .contentType(ContentType.JSON)
        .expect().statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0001"))
            .body("[0].environmentId", equalTo("TEST"))
            .body("[0].journeyName", equalTo("new-connection"))
            .body("[0].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
            .body("[0].carrier", equalTo("SPRINT"))
            .body("[0].country", equalTo("TT"))
            .body("[0].clientId", equalTo("TT"))
            .body("[0].metadata", hasEntry("channel", "store"))
            .body("[0].metadata", hasEntry("description", "this is a test scenario"))
            .body("[0].metadata", hasEntry("deposit", "no"))
            .body("[0].metadata", hasEntry("portIn", "no"))
        .when()
            .get("/tests/search?q=id:TTTT-TEST-0001");
    }

    @Test
    public void should_return_one_test_scenario_when_criteria_is_one_equals_predicate_and_separator_is_used() throws UnknownHostException, NeCTRv2Exception {

        given()
            .contentType(ContentType.JSON)
        .expect().statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0001"))
        .when()
            .get("/tests/search?q=id:TTTT-TEST-0001,");
    }

    @Test
    public void should_return_two_test_scenarios_when_criteria_is_one_equals_predicate_from_metadata() throws UnknownHostException, NeCTRv2Exception {
        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0001"))
            .body("[0].environmentId", equalTo("TEST"))
            .body("[0].journeyName", equalTo("new-connection"))
            .body("[0].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
            .body("[0].carrier", equalTo("SPRINT"))
            .body("[0].country", equalTo("TT"))
            .body("[0].clientId", equalTo("TT"))
            .body("[0].metadata", hasEntry("channel", "store"))
            .body("[0].metadata", hasEntry("description", "this is a test scenario"))
            .body("[0].metadata", hasEntry("deposit", "no"))
            .body("[0].metadata", hasEntry("portIn", "no"))

            .body("[1].id", equalTo("TTTT-TEST-0002"))
            .body("[1].environmentId", equalTo("TEST"))
            .body("[1].journeyName", equalTo("new-connection"))
            .body("[1].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb26"))
            .body("[1].carrier", equalTo("BELL"))
            .body("[1].country", equalTo("TT"))
            .body("[1].clientId", equalTo("TT"))
            .body("[1].metadata", hasEntry("channel", "store"))
            .body("[1].metadata", hasEntry("description", "this is another test scenario"))
            .body("[1].metadata", hasEntry("deposit", "yes"))
            .body("[1].metadata", hasEntry("portIn", "yes"))
        .when()
            .get("/tests/search?q=metadata.channel:store");
    }

    @Test
    public void should_return_one_test_scenario_when_criteria_is_a_boolean() throws UnknownHostException, NeCTRv2Exception {
        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0001"))
        .when()
            .get("/tests/search?q=metadata.boolean:false");
    }

    @Test
    public void should_return_one_test_scenario_when_criteria_is_an_integer() throws UnknownHostException, NeCTRv2Exception {
        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0001"))
        .when()
            .get("/tests/search?q=metadata.integer:1");
    }

    @Test
    public void should_return_one_test_scenarios_when_criteria_is_two_equals_predicates() throws UnknownHostException, NeCTRv2Exception {

        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0002"))
            .body("[0].environmentId", equalTo("TEST"))
            .body("[0].journeyName", equalTo("new-connection"))
            .body("[0].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb26"))
            .body("[0].carrier", equalTo("BELL"))
            .body("[0].country", equalTo("TT"))
            .body("[0].clientId", equalTo("TT"))
            .body("[0].metadata", hasEntry("channel", "store"))
            .body("[0].metadata", hasEntry("description", "this is another test scenario"))
            .body("[0].metadata", hasEntry("deposit", "yes"))
            .body("[0].metadata", hasEntry("portIn", "yes"))
        .when()
            .get("/tests/search?q=journeyName:new-connection,carrier:BELL");
    }

    @Test
    public void should_return_two_test_scenarios_when_criteria_is_greater_than_and_less_than_predicates() throws UnknownHostException, NeCTRv2Exception {

        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0001"))
            .body("[0].environmentId", equalTo("TEST"))
            .body("[0].journeyName", equalTo("new-connection"))
            .body("[0].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
            .body("[0].carrier", equalTo("SPRINT"))
            .body("[0].country", equalTo("TT"))
            .body("[0].clientId", equalTo("TT"))
            .body("[0].metadata", hasEntry("channel", "store"))
            .body("[0].metadata", hasEntry("description", "this is a test scenario"))
            .body("[0].metadata", hasEntry("deposit", "no"))
            .body("[0].metadata", hasEntry("portIn", "no"))

            .body("[1].id", equalTo("TTTT-TEST-0002"))
            .body("[1].environmentId", equalTo("TEST"))
            .body("[1].journeyName", equalTo("new-connection"))
            .body("[1].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb26"))
            .body("[1].carrier", equalTo("BELL"))
            .body("[1].country", equalTo("TT"))
            .body("[1].clientId", equalTo("TT"))
            .body("[1].metadata", hasEntry("channel", "store"))
            .body("[1].metadata", hasEntry("description", "this is another test scenario"))
            .body("[1].metadata", hasEntry("deposit", "yes"))
            .body("[1].metadata", hasEntry("portIn", "yes"))
        .when()
            .get("/tests/search?q=created>01/01/12,created<01/01/16");
    }

    @Test
    public void should_return_empty_when_criteria_is_greater_than_predicate() throws UnknownHostException, NeCTRv2Exception {

        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("$", hasSize(0))
        .when()
            .get("/tests/search?q=created>01/01/17");
    }

    @Test
    public void should_return_two_test_scenarios_when_criteria_is_less_than_predicate() throws UnknownHostException, NeCTRv2Exception {

        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("[0].id", equalTo("TTTT-TEST-0001"))
            .body("[0].environmentId", equalTo("TEST"))
            .body("[0].journeyName", equalTo("new-connection"))
            .body("[0].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
            .body("[0].carrier", equalTo("SPRINT"))
            .body("[0].country", equalTo("TT"))
            .body("[0].clientId", equalTo("TT"))
            .body("[0].metadata", hasEntry("channel", "store"))
            .body("[0].metadata", hasEntry("description", "this is a test scenario"))
            .body("[0].metadata", hasEntry("deposit", "no"))
            .body("[0].metadata", hasEntry("portIn", "no"))

            .body("[1].id", equalTo("TTTT-TEST-0002"))
            .body("[1].environmentId", equalTo("TEST"))
            .body("[1].journeyName", equalTo("new-connection"))
            .body("[1].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb26"))
            .body("[1].carrier", equalTo("BELL"))
            .body("[1].country", equalTo("TT"))
            .body("[1].clientId", equalTo("TT"))
            .body("[1].metadata", hasEntry("channel", "store"))
            .body("[1].metadata", hasEntry("description", "this is another test scenario"))
            .body("[1].metadata", hasEntry("deposit", "yes"))
            .body("[1].metadata", hasEntry("portIn", "yes"))
        .when()
            .get("/tests/search?q=created<01/01/17");
    }

    @Test
    public void should_return_two_test_scenarios_when_criteria_is_greater_than_predicate() throws UnknownHostException, NeCTRv2Exception {

        given()
        .contentType(ContentType.JSON)
    .expect()
        .statusCode(200)
        .body("[0].id", equalTo("TTTT-TEST-0001"))
        .body("[0].environmentId", equalTo("TEST"))
        .body("[0].journeyName", equalTo("new-connection"))
        .body("[0].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body("[0].carrier", equalTo("SPRINT"))
        .body("[0].country", equalTo("TT"))
        .body("[0].clientId", equalTo("TT"))
        .body("[0].metadata", hasEntry("channel", "store"))
        .body("[0].metadata", hasEntry("description", "this is a test scenario"))
        .body("[0].metadata", hasEntry("deposit", "no"))
        .body("[0].metadata", hasEntry("portIn", "no"))

        .body("[1].id", equalTo("TTTT-TEST-0002"))
        .body("[1].environmentId", equalTo("TEST"))
        .body("[1].journeyName", equalTo("new-connection"))
        .body("[1].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb26"))
        .body("[1].carrier", equalTo("BELL"))
        .body("[1].country", equalTo("TT"))
        .body("[1].clientId", equalTo("TT"))
        .body("[1].metadata", hasEntry("channel", "store"))
        .body("[1].metadata", hasEntry("description", "this is another test scenario"))
        .body("[1].metadata", hasEntry("deposit", "yes"))
        .body("[1].metadata", hasEntry("portIn", "yes"))
    .when()
        .get("/tests/search?q=created>01/01/15");
    }

    @Test
    public void should_return_empty_when_unknown_operator_is_used() throws UnknownHostException, NeCTRv2Exception {

        given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .body("$", hasSize(0))
        .when()
            .get("/tests/search?q=created|01/01/17");
    }


  //@formatter:on
}
