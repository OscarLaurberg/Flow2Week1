package rest;

import Exceptions.PersonNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import dto.PersonListDTO;
import entities.Person;
import utils.EMF_Creator;
import facades.PersonFacade;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Todo Remove or change relevant parts before ACTUAL use
@Path("Person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
    
    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    
    private static final PersonFacade FACADE =  PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        long count = FACADE.getPersonCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":"+count+"}";  //Done manually so no need for a DTO
    }
    
    @Path("add")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addPerson(String json){
        PersonDTO persDTO = GSON.fromJson(json, PersonDTO.class);
        PersonDTO persistedPersDTO = FACADE.addPerson(persDTO.getfName(), persDTO.getlName(), persDTO.getPhone());
        return GSON.toJson(persistedPersDTO);
    }
 
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonFromId(@PathParam("id") int id) throws PersonNotFoundException{
        PersonDTO persDTO = FACADE.getPerson(id);
        return GSON.toJson(persDTO);
    }
    
    @DELETE
    @Path("delete/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") int id) throws PersonNotFoundException{
        PersonDTO persDTO = FACADE.deletePerson(id);
        return GSON.toJson(persDTO);
    }
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPersons(){
        PersonListDTO allPersonDTOs = FACADE.getAllPersons();
        return GSON.toJson(allPersonDTOs);
        
    }
    
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editPerson (String json, @PathParam("id") Long id) throws PersonNotFoundException {
        PersonDTO personDTO = GSON.fromJson(json, PersonDTO.class);
        personDTO.setId(id);
        return Response
                .ok(GSON.toJson(FACADE.editPerson(personDTO)))
                .build();
    }
}
