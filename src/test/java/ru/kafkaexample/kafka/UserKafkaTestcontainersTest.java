package ru.kafkaexample.kafka;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.kafkaexample.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


@Testcontainers
@SpringBootTest
class UserKafkaTestcontainersTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Autowired
    private UserKafkaProducer userKafkaProducer;

    @Autowired
    private UserKafkaConsumer userKafkaConsumer;

    @MockBean
    private UserService userService;


    @Test
    void testProduceAndConsumeKafkaMessage() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User user = new User(2L, "JW", "John", "Wick", "DJ", "M");

        userKafkaProducer.writeToKafka(user);

        verify(userService, timeout(5000)).save(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals(2L, captor.getValue().getId());
        assertEquals("John", captor.getValue().getFirstName());
        assertEquals("Wick", captor.getValue().getLastName());
    }
}
