package ru.kafkaexample.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;
import ru.kafkaexample.service.UserService;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Testcontainers
public class AppTest {

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    private UserService userService = mock(UserService.class);

    @Test
    public void testNotificationSending() {
        String topicName = "testcontainer-test";
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.setBootstrapServers(kafka.getBootstrapServers());
        kafkaProperties.setTopicName(topicName);
        kafkaProperties.setPartitionNumber(1);
        kafkaProperties.setReplicationFactor(1);
        kafkaProperties.setProducerAcks("1");
        kafkaProperties.setProducerRetries("3");
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
        UserKafkaProducer userKafkaProducer = new UserKafkaProducer(kafkaConfig,
                kafkaConfig.userKafkaTemplate(), userService);

        UserKafkaConsumer userKafkaConsumer = new UserKafkaConsumer(kafkaConfig);

        KafkaConsumer<String, User> consumer = new KafkaConsumer<>(ImmutableMap.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG,
                "my-topic-group",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"),
                new StringDeserializer(),
                new JsonDeserializer<>(User.class));

        consumer.subscribe(Collections.singletonList(topicName));

        User user = new User(2L, "JW", "John", "Wick", "DJ", "M");
        userKafkaProducer.writeToKafka(user);

        verify(userService).save(any(User.class));

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(100));

            if (records.isEmpty()) {
                return false;
            }

            for (ConsumerRecord<String, User> record : records) {
                User consumedUser = (User) record.value();
                assertNotNull(consumedUser);
                assertNotNull(consumedUser.getId());
                assertNotNull(consumedUser.getFirstName());
                assertEquals(2L, consumedUser.getId());
                assertEquals("JW", consumedUser.getLogin());
            }

            return true;
        });

        consumer.unsubscribe();
    }

}
