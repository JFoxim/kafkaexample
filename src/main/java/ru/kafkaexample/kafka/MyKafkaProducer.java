package ru.kafkaexample.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyKafkaProducer {

    private final KafkaProperties kafkaProperties;
    private final Producer<String, User> producer;

    public Future<RecordMetadata> send(String key, User value) {
        ProducerRecord<String, User> record = new ProducerRecord<>(kafkaProperties.getCustomJsonTopicName(), key, value);
        return producer.send(record);
    }

    public void sendWithCallBack(String key, User value) {
        ProducerRecord<String, User> record = new ProducerRecord<>(kafkaProperties.getCustomJsonTopicName(), key, value);
         producer.send(record, (recordMetadata, exeption) -> {
            if (exeption == null) {
                log.info("Message send delivered to kafka with key = {}, topic = {}, partition = {}, offset = {}, timestamp = {}",
                        key,
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp());
            } else{
                log.error("Unable to deliver to kafka message {}. key = {}",
                        value,
                        key,
                        exeption);
                throw new RuntimeException(exeption);
            }
        });
    }
}
