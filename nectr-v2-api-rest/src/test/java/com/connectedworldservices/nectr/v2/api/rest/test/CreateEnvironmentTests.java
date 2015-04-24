package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

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
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;
import com.connectedworldservices.nectr.v2.api.rest.service.EnvironmentService;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class CreateEnvironmentTests extends AbstractRestAssuredTest {

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
        mongoTemplate.remove(Queries.searchById("TEST-ENV"), Environment.COLLECTION);

        //majority of tests don't have an application host enabled
        mongoConnector.setApplicationHostEnabled(false);
    }

    //@formatter:off
    @Test
    public void should_return_environment_withtout_application_host_when_application_host_is_disabled() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"}}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TEST-ENV"))
        .body("integrationHost.url", equalTo("integration:27017"))
        .body("integrationHost.dbName", equalTo("integration"))
        .body("integrationHost.collection", equalTo("integration"))
        .body("integrationHost.connectionsPerHost", equalTo(10))
        .body("integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(5))
        .body("integrationHost.connectTimeout", equalTo(1500))
        .body("integrationHost.maxWaitTime", equalTo(1000))
        .body("integrationHost.autoConnectRetry", equalTo(true))
        .body("integrationHost.socketKeepAlive", equalTo(true))
        .body("integrationHost.socketTimeout", equalTo(1500))
        .body("integrationHost.slaveOk", equalTo(true))
        .when()
        .post("/environments");

        assertTrue(mongoTemplate.exists(Queries.searchById("TEST-ENV"), Environment.COLLECTION));
    }

    @Test
    public void should_return_environment_with_application_host_when_application_host_is_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"},\"applicationHost\":{\"url\":\"application:27017\",\"dbName\":\"application\"}}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TEST-ENV"))
        .body("integrationHost.url", equalTo("integration:27017"))
        .body("integrationHost.dbName", equalTo("integration"))
        .body("integrationHost.collection", equalTo("integration"))
        .body("integrationHost.connectionsPerHost", equalTo(10))
        .body("integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(5))
        .body("integrationHost.connectTimeout", equalTo(1500))
        .body("integrationHost.maxWaitTime", equalTo(1000))
        .body("integrationHost.autoConnectRetry", equalTo(true))
        .body("integrationHost.socketKeepAlive", equalTo(true))
        .body("integrationHost.socketTimeout", equalTo(1500))
        .body("integrationHost.slaveOk", equalTo(true))
        .body("applicationHost.url", equalTo("application:27017"))
        .body("applicationHost.dbName", equalTo("application"))
        .body("applicationHost", not(hasProperty("collection")))
        .body("applicationHost.connectionsPerHost", equalTo(10))
        .body("applicationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(5))
        .body("applicationHost.connectTimeout", equalTo(1500))
        .body("applicationHost.maxWaitTime", equalTo(1000))
        .body("applicationHost.autoConnectRetry", equalTo(true))
        .body("applicationHost.socketKeepAlive", equalTo(true))
        .body("applicationHost.socketTimeout", equalTo(1500))
        .body("applicationHost.slaveOk", equalTo(true))
        .when()
        .post("/environments");

        assertTrue(mongoTemplate.exists(Queries.searchById("TEST-ENV"), Environment.COLLECTION));
    }

    @Test
    public void should_return_http_500_when_environment_has_same_id_as_default() throws Exception {

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_environment_id_exists() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"}}")
        .expect()
        .statusCode(201)
        .body("id", equalTo("TEST-ENV"))
        .when()
        .post("/environments");

        assertTrue(mongoTemplate.exists(Queries.searchById("TEST-ENV"), Environment.COLLECTION));

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");

    }

    @Test
    public void should_return_http_500_when_payload_is_empty() throws Exception {

        given()
        .contentType(ContentType.JSON)
        .body("{}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_integration_host_is_not_present() throws Exception {

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\"}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_integration_host_url_is_not_present() throws Exception {

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"dbName\":\"integration\",\"collection\":\"integration\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_integration_host_dbname_is_not_present() throws Exception {

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"collection\":\"integration\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_integration_host_collection_is_not_present() throws Exception {

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_application_host_is_not_present_and_application_host_is_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_application_host_url_is_not_present() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"},\"applicationHost\":{\"dbName\":\"application\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    @Test
    public void should_return_http_500_when_application_host_dbname_is_not_present() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .body("{\"id\":\"TEST-ENV\",\"integrationHost\":{\"url\":\"integration:27017\",\"dbName\":\"integration\",\"collection\":\"integration\"},\"applicationHost\":{\"url\":\"application:27017\"}}")
        .expect()
        .statusCode(500)
        .body("exception", equalTo(IllegalArgumentException.class.getCanonicalName()))
        .when()
        .post("/environments");
    }

    //@formatter:on
}
