package facades;

import exceptions.PersonNotFoundException;
import dto.PersonDTO;
import utils.EMF_Creator;
import entities.Person;
import exceptions.MissingInputException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Settings;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person pers1, pers2;

    public PersonFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = PersonFacade.getFacadeExample(emf);
    }

    /*   **** HINT **** 
        A better way to handle configuration values, compared to the UNUSED example above, is to store those values
        ONE COMMON place accessible from anywhere.
        The file config.properties and the corresponding helper class utils.Settings is added just to do that. 
        See below for how to use these files. This is our RECOMENDED strategy
     */
    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = PersonFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            pers1 = new Person("Some txt", "More text", "12321");
            pers2 = new Person("aaa", "bbb", "12321");
            em.persist(pers1);
            em.persist(pers2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testAFacadeMethod() {
        assertEquals(2, facade.getPersonCount(), "Expects two rows in the database");
    }

    @Test
    public void testGetPerson() throws PersonNotFoundException {
        Long testID = pers1.getId();
        String expected = ("Some txt");
        String result = facade.getPerson(testID.intValue()).getfName();
        assertEquals(expected, result);
    }
    
    @Test
    public void testGetPersons(){
        int expected = 2;
        int result = facade.getAllPersons().getAll().size();
        assertEquals(expected, result);
    }
    
    @Test
    public void testAddPerson()throws MissingInputException{
        int expected = facade.getAllPersons().getAll().size()+1;
        facade.addPerson("Test1", "Test2", "Test3");
        int result = facade.getAllPersons().getAll().size();
        assertEquals(expected, result);
    }
    
    @Test
    public void testDeletePerson() throws PersonNotFoundException{
       int expected = facade.getAllPersons().getAll().size()-1;
       facade.deletePerson(pers1.getId().intValue());
       int result = facade.getAllPersons().getAll().size();
        assertEquals(expected, result);
    }

    
}
