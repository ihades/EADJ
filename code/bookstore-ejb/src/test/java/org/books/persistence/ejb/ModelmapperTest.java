/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.ejb;

import java.math.BigDecimal;
import org.books.ejb.dto.BookDTO;
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
}
