/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import exceptions.PersonNotFoundException;
import dto.PersonDTO;
import dto.PersonListDTO;
import exceptions.MissingInputException;

/**
 *
 * @author oscar
 */
public interface IPersonFacade {
    
     public PersonDTO getPerson(int id) throws PersonNotFoundException;   
     public PersonListDTO getAllPersons();
     public PersonDTO addPerson(String fName, String lName, String phone, String street, String city, String zip) throws MissingInputException;
     public PersonDTO deletePerson(int id) throws PersonNotFoundException;
     public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException;
     
     
}
