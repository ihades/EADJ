package org.books.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.books.persistence.entity.IDObject;

public class GenericDao<T extends IDObject> {

    @PersistenceContext
    private EntityManager mgr;
    private final Class<T> type;

    /**
     * For Test-purposes only, do not Use!
     *
     * @param type
     * @param mgr
     */
    protected GenericDao(Class<T> type, EntityManager mgr) {
        this.type = type;
        this.mgr = mgr;
    }

    protected GenericDao(Class<T> type) {
        this.type = type;
    }

    /**
     * API Compatibility, like {@link #getById(java.lang.Long) }.
     *
     * @param type
     * @param id
     * @return
     * @throws ClassCastException if Wrong DAO is Used.
     */
    @Deprecated
    public T find(Class<T> type, Long id) {
        if (type != this.type) {
            throw new ClassCastException("Wrong DAO Used for Type " + type.getCanonicalName()
                    + ". This Dao is for " + this.type.getCanonicalName());
        }
        return getById(id);
    }

    /**
     * finds a Entity by it's Technical ID (Primary Key)
     *
     * @param id the Primary Key of this Entity
     * @return the Entity Data
     */
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

    protected EntityManager getEM() {
        return mgr;
    }

}
