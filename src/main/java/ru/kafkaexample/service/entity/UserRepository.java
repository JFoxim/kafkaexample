package ru.kafkaexample.service.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    List<User> getByFirstNameIgnoreCaseOrderByFirstNameAscLastNameAsc(String firstName);
}