package org.baelung.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.baelung.persistence.model.Foo;
import org.springframework.stereotype.Repository;

@Repository
public class FooDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Foo> findAll() {
        return entityManager.createQuery("from " + Foo.class.getName()).getResultList();
    }
}
