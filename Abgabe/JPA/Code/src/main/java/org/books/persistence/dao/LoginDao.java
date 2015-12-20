package org.books.persistence.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.books.persistence.entity.Login;

@Stateless
@LocalBean
public class LoginDao extends GenericDao<Login> {

    protected LoginDao(EntityManager mgr) {
        super(Login.class, mgr);
    }

    public LoginDao() {
        super(Login.class);
    }

    /**
     * Searches for a Login with the given Username (E-Mail)
     *
     * @param userName the Username (E-Mail)
     * @return the Login or null if nothin is found.
     */
    public Login find(String userName) {
        try {
            return this.getEM().createNamedQuery("Login.findByUserName", Login.class)
                    .setParameter(Login.LOGIN_FIND_BY_USER_NAME, userName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
