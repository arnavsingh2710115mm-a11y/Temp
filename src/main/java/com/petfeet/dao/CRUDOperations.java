package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;

import java.util.List;

/**
 * Generic CRUD contract implemented by the DAO classes.
 * @param <T>  entity type
 * @param <ID> primary key type
 */
public interface CRUDOperations<T, ID> {
    ID create(T entity) throws DatabaseException;
    T findById(ID id) throws DatabaseException;
    List<T> findAll() throws DatabaseException;
    boolean update(T entity) throws DatabaseException;
    boolean delete(ID id) throws DatabaseException;
}
