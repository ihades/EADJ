package org.books.persistence;

import org.books.persistence.testdata.AbstractTestBase;
import org.books.persistence.dao.LoginDao;
import org.books.persistence.entity.Login;
import static org.junit.Assert.*;
import org.junit.Test;

public class LoginDaoTest extends AbstractTestBase {

    @Test
    public void testLogIn() {
        Login result = new LoginDao(getEm()).find("egestas.ligula.Nullam@duiSuspendisseac.co.uk");
        assertNotNull(result);
        assertTrue(result.isPasswordValid("Kellie"));
    }

}
