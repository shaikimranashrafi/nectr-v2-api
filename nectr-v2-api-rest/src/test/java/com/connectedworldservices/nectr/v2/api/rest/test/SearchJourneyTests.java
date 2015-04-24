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
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class SearchJourneyTests extends AbstractRestAssuredTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    FongoConnector mongoConnector;

    @Before
    public void setup() throws Exception {
        //set dynamic port in rest assured
        RestAssured.port = port;

        mongoConnector.loadIntegrationData("sample-integration-data.json", "integration", true);
    }

    @After
    public void tearDown() {

        //clear mock flags
        mongoConnector.clearThrowUnknownHostExceptionOnIntegrationConnection();
        mongoConnector.clearThrowExceptionOnCreateMongoTemplate();
        mongoConnector.clearDbExists();
        mongoConnector.clearCollectionExists();
    }

    //@formatter:off
    @Test
    public void should_return_empty_when_criteria_is_empty() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search");
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
    public void should_return_empty_when_criteria_has_empty_environment_value() throws Exception {

        given()
            .contentType(ContentType.JSON)
       .expect()
           .statusCode(200)
           .body("$", hasSize(0))
       .when()
           .get("/tests/search?=environmentId:");
    }

    @Test
    public void should_return_empty_when_criteria_only_has_environment() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search?q=environmentId:TEST");
    }

    @Test
    public void should_return_empty_when_criteria_has_empty_environment() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search?q=journeyId:705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_empty_when_criteria_has_unknown_environment() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search?q=environmentId:UNKNOWN,journeyId:705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_empty_when_cant_connect_to_host() throws Exception {
        mongoConnector.setThrowUnknownHostExceptionOnIntegrationConnection(true);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search?q=environmentId:TEST,journeyId:705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_empty_when_db_in_environment_doesnt_exist() throws Exception {
        mongoConnector.setDbExists(false);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search?q=environmentId:TEST,journeyId:705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_empty_when_collection_in_environment_doesnt_exist() throws Exception {
        mongoConnector.setCollectionExists(false);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search?q=environmentId:TEST,journeyId:705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }
    @Test
    public void should_return_empty_when_cant_create_mongo_template() throws Exception {
        mongoConnector.setThrowExceptionOnCreateMongoTemplate(true);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(0))
        .when()
        .get("/journeys/search?q=environmentId:TEST,journeyId:705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_one_journey_when_criteria_matches_one_journey() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(1))
        .body("[0].environmentId", equalTo("TEST"))
        .body("[0].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .body("[0].carrier", equalTo("TMOBILE"))
        .body("[0].country", equalTo("UK"))
        .body("[0].clientId", equalTo("GL"))
        .when()
        .get("/journeys/search?q=environmentId:TEST,journeyId:705e98d5-7d16-43b4-9a78-fddc767cbb25");
    }

    @Test
    public void should_return_two_journeys_when_criteria_matches_two_journeys() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("$", hasSize(2))
        .body("[0].journeyId", equalTo("705e98d5"))
        .body("[1].journeyId", equalTo("705e98d5-7d16-43b4-9a78-fddc767cbb25"))
        .when()
        .get("/journeys/search?q=environmentId:TEST,carrier:TMOBILE");
    }
    //@formatter:on
}
