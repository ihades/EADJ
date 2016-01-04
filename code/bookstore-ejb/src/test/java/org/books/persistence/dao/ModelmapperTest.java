/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.persistence.dao;

import org.books.ejb.dto.BookDTO;
import org.books.persistence.entity.Book;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.modelmapper.ModelMapper;

/**
 *
 * @author cb
 */
public class ModelmapperTest {

    @Test
    public void testGetByIsbn() {
        Book book = new Book();
        assertNotNull(book);
        book.setTitle("The Java Language Specification (3rd Edition)");
        assertEquals("The Java Language Specification (3rd Edition)", book.getTitle());

        ModelMapper modelMapper = new ModelMapper();
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);

        assertEquals(bookDTO.getTitle(), book.getTitle());
    }
}
