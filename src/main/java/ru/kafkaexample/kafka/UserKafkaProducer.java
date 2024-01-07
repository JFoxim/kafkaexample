package ru.kafkaexample.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kafkaexample.service.UserService;


@Component
@Slf4j
@RequiredArgsConstructor
public class UserKafkaProducer {
    private final KafkaConfig kafkaConfig;
    private final KafkaTemplate<String, User> userKafkaTemplate;
    private final UserService userService;

    public void writeToKafka(User user) {
        log.info("Отправка сообщения в kafka {}", user);
        userKafkaTemplate.send(kafkaConfig.getTopicName(), user.getId().toString(), user);
        log.info("Сообщение отправлено: {}", user);

        userService.save(user);
        log.info("Сообщение сохранено: {}", user);
    }

}
