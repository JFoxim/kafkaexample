package ru.kafkaexample.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties
public class KafkaProperties {
    @Value("${spring.kafka.topic.name}")
    private String topicName;
    @Value("${spring.kafka.replication.factor}")
    private int replicationFactor;
    @Value("${spring.kafka.partition.number}")
    private int partitionNumber;
    @Value("${spring.kafka.producer.acks}")
    private String producerAcks;
    @Value("${spring.kafka.producer.retries}")
    private String producerRetries;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${user.json.topic.name}")
    private String userJsonTopicName;
    @Value("${custom.json.topic.name}")
    private String customJsonTopicName;
}
