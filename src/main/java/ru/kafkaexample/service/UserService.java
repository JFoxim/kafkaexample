package ru.kafkaexample.service;

import ru.kafkaexample.kafka.User;

import java.util.List;

public interface UserService {

    void save(User user);

    List<ru.kafkaexample.service.entity.User> getUsers(String firstName);
}
