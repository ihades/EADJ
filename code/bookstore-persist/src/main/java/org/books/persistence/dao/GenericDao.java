package org.books.persistence.dao;

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
        mgr.flush();
    }

    public T update(T entity) {
        return mgr.merge(entity);
    }

    public void delete(T entity) {
        mgr.remove(entity);
    }

    protected T find(String namedQuery, String parameterName, String businessKey) {
        return this.mgr.createNamedQuery(namedQuery, type)
                .setParameter(parameterName, businessKey)
                .getSingleResult();
    }

    protected EntityManager getEM() {
        return mgr;
    }

}
