package ru.kafkaexample.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
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


    @KafkaListener(
            topics = "user-json-topic",
            concurrency ="3",
            containerFactory = "userJsonKafkaListenerContainerFactory") //"${spring.kafka.consumer.level.concurrency:3}")
    public void logKafkaMessages(@Payload ConsumerRecord<String, Object> record,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                                 @Header(KafkaHeaders.OFFSET) Long offset) {
       User user = (User)record.value();

        //for (org.apache.kafka.common.header.Header header : record.headers()) {
            log.info("Значение: {}", record.headers());
        //}
        logger.info("Received a message object {}, from {} topic, " +
                   "{} partition, and {} offset", user.getLogin(), topic, partition, offset);

    }

    @KafkaListener(
            topics = "baeldung",
            groupId = "my_app",
            concurrency ="3") //"${spring.kafka.consumer.level.concurrency:3}")
    public void logConsoleMessages(@Payload ConsumerRecord<String, String> record,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                                 @Header(KafkaHeaders.OFFSET) Long offset) {

        log.info("Значение: {}", record.value());
        log.info("Заголовки: {}", record.headers());
        logger.info("Received a message from {} topic, " +
                "{} partition, and {} offset", topic, partition, offset);
    }


    @KafkaListener(
            topics = "custom-json-topic",
            groupId = "my_group",
            concurrency ="3",
            containerFactory = "customKafkaListenerContainerFactory")
    public void logCustomMessages(@Payload ru.kafkaexample.other.User user,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                                   @Header(KafkaHeaders.OFFSET) Long offset) {

        //log.info("Значение: {}", record.value());
        //log.info("Заголовки: {}", record.headers());
        //ru.kafkaexample.other.User user = (ru.kafkaexample.other.User)record.value();

        logger.info("Received a message {} from {} topic, " +
                "{} partition, and {} offset", user, topic, partition, offset);
    }

}
