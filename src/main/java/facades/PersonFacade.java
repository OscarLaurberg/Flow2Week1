package facades;

import exceptions.PersonNotFoundException;
import dto.PersonDTO;
import dto.PersonListDTO;
import entities.Person;
import exceptions.MissingInputException;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //TODO Remove/Change this before use
    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long renameMeCount = (long) em.createQuery("SELECT COUNT(r) FROM Person r").getSingleResult();
            return renameMeCount;
        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO getPerson(int id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        try {
            Person person = em.find(Person.class, (long) id);
            PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
        } catch (NullPointerException e) {
            throw new PersonNotFoundException("No person with provided id found");
        } finally {
            em.close();
        }
    }

    @Override
    public PersonListDTO getAllPersons() {
        EntityManager em = getEntityManager();
        TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> allPersons = q.getResultList();
        return new PersonListDTO(allPersons);

    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone) throws MissingInputException {
        Person person = new Person(fName, lName, phone);

        EntityManager em = emf.createEntityManager();
        try {
            if (person.getFirstName() == null || person.getLastName() == null) {
                throw new MissingInputException("First Name and/or Last Name is missing");
            }
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            return new PersonDTO(person);

        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO deletePerson(int id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        try{
        Person person = em.find(Person.class, (long) id);
            PersonDTO persDTO = new PersonDTO(person);
            em.getTransaction().begin();
            em.remove(person);
            em.getTransaction().commit();
            return persDTO;
        }catch (Exception e){
            throw new PersonNotFoundException("Could not delete, provided id does not exist");
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, p.getId());
        try {
            em.getTransaction().begin();
            if (p.getfName() != null) {
                person.setFirstName(p.getfName());
            }
            if (p.getlName() != null) {
                person.setLastName(p.getlName());
            }
            if (p.getPhone() != null) {
                person.setPhone(p.getPhone());
            }
            person.setLastEdited(new Date());
            em.getTransaction().commit();
            return new PersonDTO(person);
        }catch (Exception e){
            throw new PersonNotFoundException(("Could not edit, provided id does not exist"));
        } finally {
            em.close();
        }

    }

}
