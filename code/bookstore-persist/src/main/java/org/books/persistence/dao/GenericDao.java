package org.books.persistence.dao;

import org.books.persistence.exception.NotExistException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.books.persistence.entity.IdentifiableObject;

public class GenericDao<T extends IdentifiableObject> {

    @PersistenceContext
    private EntityManager mgr;
    private final Class<T> type;

    protected GenericDao(Class<T> type, EntityManager mgr) {
        this.type = type;
        this.mgr = mgr;
    }

    protected GenericDao(Class<T> type) {
        this.type = type;
    }

    public T getById(Long id) {
        return mgr.find(type, id);
    }

    public void create(T entity) {
        mgr.persist(entity);
    }

    public T update(T entity) throws NotExistException {
        ensureExistence(entity);
        return mgr.merge(entity);
    }

    public void delete(T entity) {
        mgr.remove(entity);
    }

    protected T find(String namedQuerry, String parameterName, String businessKey) {
        return this.mgr.createNamedQuery(namedQuerry, type)
                .setParameter(parameterName, businessKey)
                .getSingleResult();
    }

    protected EntityManager getEM() {
        return mgr;
    }

    private void ensureExistence(T entity) throws NotExistException {
        if ((entity.getId() == null) || (mgr.find(type, entity.getId()) == null)) {
            throw new NotExistException();
        }
    }
}
