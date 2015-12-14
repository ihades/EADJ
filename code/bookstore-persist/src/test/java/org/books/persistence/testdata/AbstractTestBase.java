package org.books.persistence.testdata;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractTestBase {

    private static final String PERSISTENCE_UNIT = "test-bookstore";

    private static EntityManager em;
    private static TestDataFactory tdf;

    @BeforeClass
    public static void setUpClass() {
        em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT).createEntityManager();
        tdf = new TestDataFactory(em);
        tdf.deleteTestData();
        tdf.setUp();
    }

    @AfterClass
    public static void tearDownClass() {
        tdf.deleteTestData();
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    protected static EntityManager getEm() {
        return em;
    }
}
