package ru.kafkaexample.kafka;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyProducerTest {

    @Test
    void sendMessage() {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.setBootstrapServers("localhost:9092");
        kafkaProperties.setCustomJsonTopicName("test-topic");
        kafkaProperties.setProducerRetries("3");
        kafkaProperties.setPartitionNumber(3);
        kafkaProperties.setProducerAcks("all");
        kafkaProperties.setReplicationFactor(10);

        User user = new User();
        user.setId(1L);
        user.setFirstName("Иван");
        user.setGender("М");
        user.setLogin("Ivan");
        user.setPatronymic("Иванович");
        user.setLastName("Иванов");

        MockProducer<String, User> mockProducer = new MockProducer<>(true,
                new StringSerializer(), new JsonSerializer<>());

        MyKafkaProducer myKafkaProducer = new MyKafkaProducer(kafkaProperties, mockProducer);
        myKafkaProducer.sendWithCallBack(user.getId().toString(), user);

        assertEquals(1, mockProducer.history().size());
    }
}
