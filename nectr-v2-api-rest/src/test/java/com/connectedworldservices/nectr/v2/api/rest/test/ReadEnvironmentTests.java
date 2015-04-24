package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;

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
public class ReadEnvironmentTests extends AbstractRestAssuredTest {

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
        mongoTemplate.remove(Queries.searchById("TEST-ENV"), Environment.COLLECTION);

        mongoConnector.setApplicationHostEnabled(false);
    }

    //@formatter:off
    @Test
    public void should_return_default_environment_with_application_host_when_application_host_is_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("id", equalTo("TEST"))
        .body("integrationHost.url", equalTo("integration:27017"))
        .body("integrationHost.dbName", equalTo("integration"))
        .body("integrationHost.collection", equalTo("integration"))
        .body("integrationHost.connectionsPerHost", equalTo(8))
        .body("integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(4))
        .body("integrationHost.connectTimeout", equalTo(1000))
        .body("integrationHost.maxWaitTime", equalTo(1500))
        .body("integrationHost.autoConnectRetry", equalTo(true))
        .body("integrationHost.socketKeepAlive", equalTo(true))
        .body("integrationHost.socketTimeout", equalTo(1500))
        .body("integrationHost.slaveOk", equalTo(true))
        .body("applicationHost.url", equalTo("application:27017"))
        .body("applicationHost.dbName", equalTo("application"))
        .body("applicationHost", not(hasProperty("collection")))
        .body("applicationHost.connectionsPerHost", equalTo(8))
        .body("applicationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(4))
        .body("applicationHost.connectTimeout", equalTo(1000))
        .body("applicationHost.maxWaitTime", equalTo(1500))
        .body("applicationHost.autoConnectRetry", equalTo(true))
        .body("applicationHost.socketKeepAlive", equalTo(true))
        .body("applicationHost.socketTimeout", equalTo(1500))
        .body("applicationHost.slaveOk", equalTo(true))
        .when()
        .get("/environments/TEST");
    }

    @Test
    public void should_return_default_environment_with_integration_host_only_when_application_host_is_disabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(false);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("id", equalTo("TEST"))
        .body("integrationHost.url", equalTo("integration:27017"))
        .body("integrationHost.dbName", equalTo("integration"))
        .body("integrationHost.collection", equalTo("integration"))
        .body("integrationHost.connectionsPerHost", equalTo(8))
        .body("integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(4))
        .body("integrationHost.connectTimeout", equalTo(1000))
        .body("integrationHost.maxWaitTime", equalTo(1500))
        .body("integrationHost.autoConnectRetry", equalTo(true))
        .body("integrationHost.socketKeepAlive", equalTo(true))
        .body("integrationHost.socketTimeout", equalTo(1500))
        .body("integrationHost.slaveOk", equalTo(true))
        .body("$", not(hasKey("applicationHost")))
        .when()
        .get("/environments/TEST");
    }

    @Test
    public void should_return_multiple_environments_with_integration_host_only_when_application_host_is_disabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(false);

        environmentService.createEnvironment(new Environment("TEST-ENV", new Host("integration:27017", "integration", "integration")));

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("[0].id", equalTo("TEST"))
        .body("[0].integrationHost.url", equalTo("integration:27017"))
        .body("[0].integrationHost.dbName", equalTo("integration"))
        .body("[0].integrationHost.collection", equalTo("integration"))
        .body("[0].integrationHost.connectionsPerHost", equalTo(8))
        .body("[0].integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(4))
        .body("[0].integrationHost.connectTimeout", equalTo(1000))
        .body("[0].integrationHost.maxWaitTime", equalTo(1500))
        .body("[0].integrationHost.autoConnectRetry", equalTo(true))
        .body("[0].integrationHost.socketKeepAlive", equalTo(true))
        .body("[0].integrationHost.socketTimeout", equalTo(1500))
        .body("[0].integrationHost.slaveOk", equalTo(true))
        .body("[0]", not(hasKey("applicationHost")))
        .body("[1].id", equalTo("TEST-ENV"))
        .body("[1].integrationHost.url", equalTo("integration:27017"))
        .body("[1].integrationHost.dbName", equalTo("integration"))
        .body("[1].integrationHost.collection", equalTo("integration"))
        .body("[1].integrationHost.connectionsPerHost", equalTo(10))
        .body("[1].integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(5))
        .body("[1].integrationHost.connectTimeout", equalTo(1500))
        .body("[1].integrationHost.maxWaitTime", equalTo(1000))
        .body("[1].integrationHost.autoConnectRetry", equalTo(true))
        .body("[1].integrationHost.socketKeepAlive", equalTo(true))
        .body("[1].integrationHost.socketTimeout", equalTo(1500))
        .body("[1].integrationHost.slaveOk", equalTo(true))
        .body("[1]", not(hasKey("applicationHost")))
        .when()
        .get("/environments");
    }

    @Test
    public void should_return_multiple_environments_with_application_host_when_application_host_is_enabled() throws Exception {
        mongoConnector.setApplicationHostEnabled(true);

        environmentService.createEnvironment(new Environment("TEST-ENV", new Host("integration:27017", "integration", "integration"), new Host("application:27017", "application", null)));

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("[0].id", equalTo("TEST"))
        .body("[0].integrationHost.url", equalTo("integration:27017"))
        .body("[0].integrationHost.dbName", equalTo("integration"))
        .body("[0].integrationHost.collection", equalTo("integration"))
        .body("[0].integrationHost.connectionsPerHost", equalTo(8))
        .body("[0].integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(4))
        .body("[0].integrationHost.connectTimeout", equalTo(1000))
        .body("[0].integrationHost.maxWaitTime", equalTo(1500))
        .body("[0].integrationHost.autoConnectRetry", equalTo(true))
        .body("[0].integrationHost.socketKeepAlive", equalTo(true))
        .body("[0].integrationHost.socketTimeout", equalTo(1500))
        .body("[0].integrationHost.slaveOk", equalTo(true))
        .body("[0].applicationHost.url", equalTo("application:27017"))
        .body("[0].applicationHost.dbName", equalTo("application"))
        .body("[0].applicationHost", not(hasProperty("collection")))
        .body("[0].applicationHost.connectionsPerHost", equalTo(8))
        .body("[0].applicationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(4))
        .body("[0].applicationHost.connectTimeout", equalTo(1000))
        .body("[0].applicationHost.maxWaitTime", equalTo(1500))
        .body("[0].applicationHost.autoConnectRetry", equalTo(true))
        .body("[0].applicationHost.socketKeepAlive", equalTo(true))
        .body("[0].applicationHost.socketTimeout", equalTo(1500))
        .body("[0].applicationHost.slaveOk", equalTo(true))
        .body("[1].integrationHost.url", equalTo("integration:27017"))
        .body("[1].integrationHost.dbName", equalTo("integration"))
        .body("[1].integrationHost.collection", equalTo("integration"))
        .body("[1].integrationHost.connectionsPerHost", equalTo(10))
        .body("[1].integrationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(5))
        .body("[1].integrationHost.connectTimeout", equalTo(1500))
        .body("[1].integrationHost.maxWaitTime", equalTo(1000))
        .body("[1].integrationHost.autoConnectRetry", equalTo(true))
        .body("[1].integrationHost.socketKeepAlive", equalTo(true))
        .body("[1].integrationHost.socketTimeout", equalTo(1500))
        .body("[1].integrationHost.slaveOk", equalTo(true))
        .body("[1].applicationHost.url", equalTo("application:27017"))
        .body("[1].applicationHost.dbName", equalTo("application"))
        .body("[1].applicationHost", not(hasProperty("collection")))
        .body("[1].applicationHost.connectionsPerHost", equalTo(10))
        .body("[1].applicationHost.threadsAllowedToBlockForConnectionMultiplier", equalTo(5))
        .body("[1].applicationHost.connectTimeout", equalTo(1500))
        .body("[1].applicationHost.maxWaitTime", equalTo(1000))
        .body("[1].applicationHost.autoConnectRetry", equalTo(true))
        .body("[1].applicationHost.socketKeepAlive", equalTo(true))
        .body("[1].applicationHost.socketTimeout", equalTo(1500))
        .body("[1].applicationHost.slaveOk", equalTo(true))
        .when()
        .get("/environments");
    }

    @Test
    public void should_return_all_environment_ids() throws Exception {
        mongoConnector.setApplicationHostEnabled(false);

        environmentService.createEnvironment(new Environment("TEST-ENV", new Host("integration:27017", "integration", "integration")));

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(200)
        .body("[0]", equalTo("TEST"))
        .body("[1]", equalTo("TEST-ENV"))
        .when()
        .get("/environments?fields=id");
    }

    @Test
    public void should_return_http_404_when_environment_id_is_unknown() throws Exception {
        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(404)
        .body("exception", equalTo(NotFoundException.class.getCanonicalName()))
        .when()
        .get("/environments/UNKNOWN");
    }
    //@formatter:on
}
