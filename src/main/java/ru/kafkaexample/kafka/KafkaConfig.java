package ru.kafkaexample.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public org.apache.kafka.clients.producer.KafkaProducer<String, User> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducerRetries());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducerAcks());
        return new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "20971520");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, User> userProducerFactory() {
        log.info("ProducerAcks: {}", kafkaProperties.getProducerAcks());
        log.info("ProducerRetries: {}", kafkaProperties.getProducerRetries());

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducerRetries());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducerAcks());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, User> userKafkaTemplate() {
        return new KafkaTemplate<>(userProducerFactory());
    }


    @Bean
    @Order(-1)
    public NewTopic createNewTopic() {
        log.info("Наименование топика {}", kafkaProperties.getTopicName());
        log.info("PartitionNumber {}", kafkaProperties.getPartitionNumber());
        log.info("ReplicationFactor {}", kafkaProperties.getReplicationFactor());

        Map<String, String> map = new HashMap<>();
        map.put("min.insync.replicas", "2");

        return new NewTopic(
                kafkaProperties.getTopicName(),
                kafkaProperties.getPartitionNumber(),
                (short)kafkaProperties.getReplicationFactor())
                .configs(map);
    }

    @Bean
    @Order(-1)
    public NewTopic createPropertiesNewTopic() {
        Map<String, String> map = new HashMap<>();
        map.put("min.insync.replicas", "2");

        return new NewTopic(
                "new_topic_1",
               3,
                (short)1)
                .configs(map);
    }

    @Bean
    public NewTopic createUserJsonTopic() {
        return new NewTopic(
                kafkaProperties.getUserJsonTopicName(),
                kafkaProperties.getPartitionNumber(),
                (short)kafkaProperties.getReplicationFactor());
    }

    public String getTopicName() {
        return  kafkaProperties.getTopicName();
    }

    public ConsumerFactory<String, User> userConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-topic-group");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(User.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, User> userKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> userJsonProducerFactory() {
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(true);


        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        //configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducerRetries());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducerAcks());
        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                jsonSerializer);
    }

    @Bean
    public KafkaTemplate<String, Object> userJsonKafkaTemplate() {
        return new KafkaTemplate<>(userJsonProducerFactory());
    }

    public String getTopicJsonName() {
        return  kafkaProperties.getUserJsonTopicName();
    }



    public ConsumerFactory<String, Object> userJsonConsumerFactory() {
       JsonDeserializer<Object> payloadJsonDeserializer = new JsonDeserializer<>();
       payloadJsonDeserializer.addTrustedPackages("ru.kafkaexample.kafka");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "topic-json-group");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.kafkaexample.kafka");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                payloadJsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> userJsonKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userJsonConsumerFactory());
        return factory;
    }



}
