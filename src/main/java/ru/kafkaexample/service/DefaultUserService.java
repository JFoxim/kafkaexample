package ru.kafkaexample.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.kafkaexample.kafka.User;
import ru.kafkaexample.service.entity.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserService.class);
    private final UserRepository userRepository;

    @Override
    public void save(User user) {
        logger.info("Saving user with id = {}", user.getId());
        userRepository.save(new ru.kafkaexample.service.entity.User(
                user.getId(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getGender()));
    }

    @Override
    public List<ru.kafkaexample.service.entity.User> getUsers(String firstName) {
        return userRepository.getByFirstNameIgnoreCaseOrderByFirstNameAscLastNameAsc(firstName);
    }
}
