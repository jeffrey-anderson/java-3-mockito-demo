package edu.cscc.degrees.data;

import java.util.List;
import java.util.Optional;

public interface CrudRepository <T, ID> {
  Optional<T> findById(ID primaryKey);
  List<T> findAll();
  T create(T entity);
  void update(T entity);
  void delete(ID primaryKey);
}