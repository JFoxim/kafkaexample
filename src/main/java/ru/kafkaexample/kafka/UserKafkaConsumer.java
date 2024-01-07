package ru.kafkaexample.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserKafkaConsumer.class);
    private final KafkaConfig kafkaConfig;


    @KafkaListener(
            topics = "my-topic", // "${spring.kafka.topic.name}",
            concurrency ="3",
            containerFactory = "userKafkaListenerContainerFactory") //"${spring.kafka.consumer.level.concurrency:3}")
    public void logKafkaMessages(@Payload User user,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                                 @Header(KafkaHeaders.OFFSET) Long offset) {
        logger.info("Received a message contains a user information with id {}, login {}, from {} topic, " +
                "{} partition, and {} offset", user.getId(), user.getLogin(), topic, partition, offset);

    }
}
