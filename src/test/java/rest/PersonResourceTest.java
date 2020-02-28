package rest;

import dto.PersonDTO;
import entities.Person;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person r1, r2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        r1 = new Person("Some txt", "More text", "21312");
        r2 = new Person("aaa", "bbb", "12321");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(r1);
            em.persist(r2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/Person").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/Person/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello World"));
    }

    @Test
    public void testCount() throws Exception {
        given()
                .contentType("application/json")
                .get("/Person/count").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(2));
    }

    @Test
    public void testGetAllPersons() throws Exception {
        given()
                .contentType("application/json")
                .get("/Person/all").then()
                .assertThat().body("all", hasSize(2));
    }

    @Test
    public void testGetPersonByid() {
        Long id = r1.getId();
        given().when()
                .get("/Person/{id}", id)
                .then()
                .assertThat()
                .body("fName", equalTo("Some txt"));
    }
    
    @Test
    public void testGetPersonWithNonExistingId() {
        long id = 100;
        given().when()
                .get("/Person/{id}",id)
                .then()
                .statusCode(404)
                .body("code", equalTo(404),
                        "message", equalTo("No person with provided id found"));
    }

    @Test
    public void testDeletePerson() {
        Long id = r1.getId();
        given()
                .when()
                .delete("/Person/delete/{id}", id)
                .then()
                .statusCode(200)
                .body("fName", is("Some txt"));
    }

    @Test
    public void testDeletePersonWithNonExistingId() {
        int id = 100;
        when()
                .delete("Person/delete/{id}", id)
                .then()
                .statusCode(404)
                .body("code", equalTo(404),
                        "message", equalTo("Could not delete, provided id does not exist"));
    }

    @Test
    public void testEditPerson() {
        Long id = r1.getId();
        Map<String, String> content = new HashMap<>();
        content.put("fName", "updated");

        given()
                .contentType(ContentType.JSON)
                .with()
                .body(content)
                .when()
                .put("Person/{id}", id)
                .then()
                .body("fName", is("updated"));
    }

    @Test
    public void testEditPersonWithNonExistingId() {
        PersonDTO person = new PersonDTO("Niels", "Joe Joe", "4444");
        int id = 100;
        given()
                .contentType("application/json")
                .body(person)
                .when()
                .put("Person/{id}",id)
                .then()
                .statusCode(404)
                .body("code", equalTo(404),
                        "message", equalTo("Could not edit, provided id does not exist"));
    }


    
    @Test
    public void testAddPerson() {
        String jsonStr = "{\"fName\":\"testFName\",\"lName\":\"testLName\",\"phone\":\"123232\"}";

        Map<String, String> content = new HashMap<>();
        content.put("fName", "testFName");
        content.put("lName", "testLName");
        content.put("phone", "70121416");
        given()
                .contentType(ContentType.JSON)
                .with()
                .body(new PersonDTO("testFName", "testLName", "123321"))
                .when()
                .post("/Person/add")
                .then()
                .body("fName", equalTo("testFName"), "lName", equalTo("testLName"));
    }
    
    @Test
    public void testAddPersonWithMissingInfo(){
        PersonDTO person = new PersonDTO("Jewns", "", "36954310");
        given()
                .contentType("application/json")
                .body(person)
                .when()
                .post("Person/add")
                .then()
                .body("code", equalTo(400),
                        "message", equalTo("First Name and/or Last Name is missing"));
    
    }

}
