package org.books.persistence.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import org.books.persistence.entity.Login;

@Stateless
@LocalBean
public class LoginDao extends GenericDao<Login> {

    public LoginDao(EntityManager mgr) {
        super(Login.class, mgr);
    }

    public LoginDao() {
        super(Login.class);
    }

    public Login find(String userName) {
        return this.getEM().createNamedQuery("Login.findByUserName", Login.class)
                .setParameter(Login.LOGIN_FIND_BY_USER_NAME, userName)
                .getSingleResult();
    }

}
