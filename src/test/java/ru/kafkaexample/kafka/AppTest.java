package ru.kafkaexample.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;
import ru.kafkaexample.service.UserService;

import java.time.Duration;

@Testcontainers
@SpringBootTest
public class AppTest {

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Autowired
    private UserKafkaProducer userKafkaProducer;

    @Autowired
    private UserKafkaConsumer userKafkaConsumer;

    @MockBean
    private UserService userService;

    @Test
    public void testNotificationSending() {
        String topicName = "testcontainer-test";
        String bootstrapServers = kafka.getBootstrapServers();

        User user = new User(2L, "JW", "John", "Wick", "DJ", "M");
        userKafkaProducer.writeToKafka(user);

//        UserKafkaConsumer<String, User> consumer = new KafkaConsumer<>(ImmutableMap.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                bootstrapServers, ConsumerConfig.GROUP_ID_CONFIG,
//                "collector-test", ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
//                "earliest"), new StringDeserializer(), new User());
//
//        consumer.subscribe(Collections.singletonList(topicName));
//
//        producer.produce();
//
//        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
//            ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(100));
//
//            if (records.isEmpty()) {
//                return false;
//            }
//
//            for (ConsumerRecord<String, Notification> record : records) {
//                LocationNotification consumedLocationNotification = (LocationNotification) record.value();
//                assertNotNull(consumedLocationNotification);
//                assertNotNull(consumedLocationNotification.getLatitude());
//                assertNotNull(consumedLocationNotification.getLongitude());
//            }
//
//            return true;
//        });
//
//        consumer.unsubscribe();
    }

}
