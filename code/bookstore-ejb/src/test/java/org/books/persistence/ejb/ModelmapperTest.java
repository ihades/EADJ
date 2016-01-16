/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import java.math.BigDecimal;
import org.books.ejb.dto.AddressDTO;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.dto.CreditCardDTO;
import org.books.ejb.dto.CustomerDTO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.modelmapper.ModelMapper;
import org.testng.annotations.Test;

/**
 *
 * @author cb
 */
public class ModelmapperTest {

    @Test
    public void testGetByIsbn() {
        BookDTO book = new BookDTO(
                "123456789",
                "Oracle for Dummies",
                "irgendwer",
                "orelly",
                2012,
                BookDTO.Binding.Unknown,
                120,
                new BigDecimal(25.7));
        assertNotNull(book);
        assertEquals("Oracle for Dummies", book.getTitle());

        ModelMapper modelMapper = new ModelMapper();
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);

        assertEquals(bookDTO.getIsbn(), book.getIsbn());
        assertEquals(bookDTO.getTitle(), book.getTitle());
        assertEquals(bookDTO.getAuthors(), book.getAuthors());
        assertEquals(bookDTO.getPublisher(), book.getPublisher());
        assertEquals(bookDTO.getPublicationYear(), book.getPublicationYear());
        assertEquals(bookDTO.getNumberOfPages(), book.getNumberOfPages());
        assertEquals(bookDTO.getPrice(), book.getPrice());
    }
    
    @Test
    public void testCustomer() {
        AddressDTO addressDTO = new AddressDTO("Wasserschloss 42",
            "Bern",
            "3011",
            "Switzerland");
        
        CreditCardDTO validCreditCardDTO = new CreditCardDTO(CreditCardDTO.Type.MasterCard,
            "5555555555554444",
            2,
            2017);
        
        CustomerDTO customerDTO = new CustomerDTO(Util.numbGen() + "@gmail.com",
            "Erwin",
            "Bazong",
            null,
            addressDTO,
            validCreditCardDTO);
        
        ModelMapper mm = new ModelMapper();
        CustomerDTO mappedCustomer = mm.map(customerDTO, CustomerDTO.class);
        assertEquals(mappedCustomer.getAddress().getCity(), addressDTO.getCity());
        assertEquals(mappedCustomer.getAddress().getCountry(), addressDTO.getCountry());
        assertEquals(mappedCustomer.getAddress().getPostalCode(), addressDTO.getPostalCode());
        assertEquals(mappedCustomer.getAddress().getStreet(), addressDTO.getStreet());
        
        assertEquals(mappedCustomer.getCreditCard().getNumber(), validCreditCardDTO.getNumber());
        assertEquals(mappedCustomer.getCreditCard().getExpirationMonth(), validCreditCardDTO.getExpirationMonth());
        assertEquals(mappedCustomer.getCreditCard().getExpirationYear(), validCreditCardDTO.getExpirationYear());
        assertEquals(mappedCustomer.getCreditCard().getType(), validCreditCardDTO.getType());
        
        assertEquals(mappedCustomer.getEmail(), customerDTO.getEmail());
        assertEquals(mappedCustomer.getFirstName(), customerDTO.getFirstName());
        assertEquals(mappedCustomer.getLastName(), customerDTO.getLastName());
        assertEquals(mappedCustomer.getNumber(), customerDTO.getNumber());
    }
    
    
}
