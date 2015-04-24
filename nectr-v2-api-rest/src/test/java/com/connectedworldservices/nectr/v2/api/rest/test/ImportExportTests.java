package com.connectedworldservices.nectr.v2.api.rest.test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.zip.ZipInputStream;

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
import com.connectedworldservices.nectr.v2.api.rest.mock.MockObjectMapper;
import com.connectedworldservices.nectr.v2.api.rest.mock.MockTemplate;
import com.connectedworldservices.nectr.v2.api.rest.model.ApplicationData;
import com.connectedworldservices.nectr.v2.api.rest.model.IntegrationData;
import com.connectedworldservices.nectr.v2.api.rest.model.Sequence;
import com.connectedworldservices.nectr.v2.api.rest.model.TestScenario;
import com.connectedworldservices.nectr.v2.api.rest.repository.Queries;
import com.connectedworldservices.nectr.v2.api.rest.service.NeCTRv2Exception;
import com.connectedworldservices.nectr.v2.api.rest.service.NotFoundException;
import com.connectedworldservices.nectr.v2.api.rest.service.TestScenarioService;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

@DirtiesContext
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestNeCTRv2.class)
public class ImportExportTests extends AbstractRestAssuredTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    FongoConnector mongoConnector;

    @Autowired
    MockTemplate mongoTemplate;

    @Autowired
    MockObjectMapper objectMapper;

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

        //clear mocked flags
        objectMapper.clearThrowExceptionOnWrite();
        objectMapper.clearThrowExceptionOnRead();

        //majority of tests don't have an application host enabled
        mongoConnector.setApplicationHostEnabled(false);
    }

    //@formatter:off
    @Test
    public void should_return_zip_on_export_test_scenario() throws Exception {
        testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());

        Response response = given()
            .contentType(ContentType.JSON)
        .expect()
            .statusCode(200)
            .header("Content-Disposition", "attachment; filename=\"TTTT-TEST-0001.zip\"")
        .when()
            .get("/tests/TTTT-TEST-0001/export");

        assertTrue(new ZipInputStream(response.asInputStream()).getNextEntry() != null);
    }

    @Test
    public void should_return_http_500_on_import_when_zip_has_one_invalid_test() throws Exception {
        given()
            .multiPart("file", "0003.zip", getClass().getResourceAsStream("/0003.zip"))
        .expect()
            .statusCode(500)
            .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
            .post("/tests/import");

        assertFalse(mongoTemplate.exists(Queries.searchById("CAGL-LOCAL-0039"), TestScenario.COLLECTION));
    }

    @Test
    public void should_return_http_500_on_import_when_its_not_a_zip() throws Exception {
        given()
            .multiPart("file", "TTTT-TEST-0001.json", getClass().getResourceAsStream("/TTTT-TEST-0001.json"))
        .expect()
            .statusCode(500)
            .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
            .post("/tests/import");

        assertFalse(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0001"), TestScenario.COLLECTION));
    }

    @Test
    public void should_return_http_500_on_import_when_zip_has_two_valid_and_one_invalid_test() throws Exception {
        given()
            .multiPart("file", "0001_0002_0003.zip", getClass().getResourceAsStream("/0001_0002_0003.zip"))
        .expect()
            .statusCode(500)
            .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
            .post("/tests/import");

        assertFalse(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0001"), TestScenario.COLLECTION));
        assertFalse(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0002"), TestScenario.COLLECTION));
        assertFalse(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0003"), TestScenario.COLLECTION));
    }

    @Test
    public void should_return_http_500_on_import_when_deserialization_fails() throws Exception {
        objectMapper.setThrowExceptionOnRead(true, TestScenario.class);

        given()
            .multiPart("file", "0001.zip", getClass().getResourceAsStream("/0001.zip"))
        .expect()
            .statusCode(500)
            .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
            .post("/tests/import");

        assertFalse(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0001"), TestScenario.COLLECTION));
    }

    @Test
    public void should_return_http_500_on_import_when_test_id_already_exists() throws Exception {
        testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());

        given()
            .multiPart("file", "0001.zip", getClass().getResourceAsStream("/0001.zip"))
        .expect()
            .statusCode(500)
            .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
            .post("/tests/import");

        assertTrue(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0001"), TestScenario.COLLECTION));
    }

    @Test
    public void should_import_one_test_scenario_when_zip_is_valid() throws Exception {
        given()
            .multiPart("file", "0001.zip", getClass().getResourceAsStream("/0001.zip"))
        .expect()
            .statusCode(201)
        .when()
            .post("/tests/import");

        assertTrue(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0001"), TestScenario.COLLECTION));
    }

    @Test
    public void should_import_two_test_scenarios_when_zip_is_valid() throws Exception {
        given()
            .multiPart("file", "0001_0002.zip", getClass().getResourceAsStream("/0001_0002.zip"))
        .expect()
            .statusCode(201)
        .when()
            .post("/tests/import");

        assertTrue(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0001"), TestScenario.COLLECTION));
        assertTrue(mongoTemplate.exists(Queries.searchById("TTTT-TEST-0002"), TestScenario.COLLECTION));
    }

    @Test
    public void should_return_http_500_on_export_when_test_serialization_fails() throws Exception {
        testScenarioService.createTestScenario("TEST", "SPRINT", "TT", "TT", "new-connection", "705e98d5-7d16-43b4-9a78-fddc767cbb25", Collections.emptyMap());
        objectMapper.setThrowExceptionOnWrite(true, TestScenario.class);

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(500)
        .body("exception", equalTo(NeCTRv2Exception.class.getCanonicalName()))
        .when()
        .get("/tests/TTTT-TEST-0001/export");
    }

    @Test
    public void should_return_http_404_on_export_when_test_id_is_unknown() throws Exception {

        given()
        .contentType(ContentType.JSON)
        .expect()
        .statusCode(404)
        .body("exception", equalTo(NotFoundException.class.getCanonicalName()))
        .when()
        .get("/tests/TTTT-TEST-0001/export");
    }
    //@formatter:on
}
