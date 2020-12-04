package edu.ucsb.mapache.repositories;

import java.util.List;
import java.util.Optional;

import edu.ucsb.mapache.entities.Student;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {
  public List<Student> findByEmail(String email);
}