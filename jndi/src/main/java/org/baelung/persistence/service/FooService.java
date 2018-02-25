package org.baelung.persistence.service;

import java.util.List;

import javax.transaction.Transactional;

import org.baelung.persistence.dao.FooDao;
import org.baelung.persistence.model.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FooService {
    @Autowired
    private FooDao dao;

    public List<Foo> findAll() {
        return dao.findAll();
    }
}
