package ru.kafkaexample.serialazer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.kafkaexample.kafka.KafkaProperties;
import ru.kafkaexample.kafka.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
public class CustomConfig {
    private final KafkaProperties kafkaProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Bean
    public ProducerFactory<String, User> customProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducerRetries());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducerAcks());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, User> customKafkaTemplate() {
        return new KafkaTemplate<>(customProducerFactory());
    }

    private ConsumerFactory<String, ru.kafkaexample.other.User> createCustomKafkaConsumer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "my_client");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my_group");
        //configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.kafkaexample.kafka");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomDeserializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducerRetries());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducerAcks());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(configProps,
                new StringDeserializer(),
                new CustomDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ru.kafkaexample.other.User> customKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ru.kafkaexample.other.User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createCustomKafkaConsumer());
        return factory;
    }

    @Bean
    public NewTopic createCustomTopic() {
        return new NewTopic(
                kafkaProperties.getCustomJsonTopicName(),
                kafkaProperties.getPartitionNumber(),
                (short)kafkaProperties.getReplicationFactor());
    }

}
