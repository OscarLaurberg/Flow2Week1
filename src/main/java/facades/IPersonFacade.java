/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import Exceptions.PersonNotFoundException;
import dto.PersonDTO;
import dto.PersonListDTO;

/**
 *
 * @author oscar
 */
public interface IPersonFacade {
    
     public PersonDTO getPerson(int id) throws PersonNotFoundException;   
     public PersonListDTO getAllPersons();
     public PersonDTO addPerson(String fName, String lName, String phone);
     public PersonDTO deletePerson(int id) throws PersonNotFoundException;
     public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException;
     
     
}
