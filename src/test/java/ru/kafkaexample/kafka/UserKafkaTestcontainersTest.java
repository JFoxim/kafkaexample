package ru.kafkaexample.kafka;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.kafkaexample.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Testcontainers
class UserKafkaTestcontainersTest {
    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
    private UserService userService = mock(UserService.class);

    @Test
    void testProduceAndConsumeKafkaMessage() {
        KafkaProperties kafkaProperties = getKafkaProperties();
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
        UserKafkaProducer userKafkaProducer = new UserKafkaProducer(kafkaConfig,
                kafkaConfig.userKafkaTemplate(), userService);

        //UserKafkaConsumer userKafkaConsumer = new UserKafkaConsumer(kafkaConfig);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User user = new User(2L, "JW", "John", "Wick", "DJ", "M");

        userKafkaProducer.writeToKafka(user);

        verify(userService, timeout(5000)).save(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals(2L, captor.getValue().getId());
        assertEquals("John", captor.getValue().getFirstName());
        assertEquals("Wick", captor.getValue().getLastName());
    }

    @NotNull
    private static KafkaProperties getKafkaProperties() {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.setBootstrapServers(kafkaContainer.getBootstrapServers());
        kafkaProperties.setTopicName("my-topic");
        kafkaProperties.setPartitionNumber(1);
        kafkaProperties.setReplicationFactor(1);
        kafkaProperties.setProducerAcks("1");
        kafkaProperties.setProducerRetries("3");
        return kafkaProperties;
    }
}
