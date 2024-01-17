package ru.kafkaexample.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaController {
    private final UserKafkaProducer userKafkaProducer;
    private final MyKafkaProducer myKafkaProducer;


    @GetMapping("/kafka/send")
    public String sendMessageKafka() throws InterruptedException {
        User user = new User(1L, "Ivan", "Иван", "Петров", "Петрович", "М");
        userKafkaProducer.writeToKafka(user);
        return "OK";
    }

    @GetMapping("/kafka/send-json")
    public String sendJsonKafka() throws InterruptedException {
        User user = new User(1L, "Ivan", "Иван", "Петров", "Петрович", "М");
        userKafkaProducer.writeJsonToKafka(user);
        return "OK";
    }

    @GetMapping("/kafka/send-custom")
    public String sendCustomKafka() throws InterruptedException {
        User user = new User(1L, "Ivan", "Иван", "Петров", "Петрович", "М");
        userKafkaProducer.writeCustomToKafka(user);
        return "Custom OK";
    }

    @GetMapping("/kafka/send-kafka-producer")
    public String sendKafkaProducer() throws InterruptedException {
        User user = new User(1L, "Ivan", "Иван", "Петров", "Петрович", "М");
        myKafkaProducer.sendWithCallBack(user.getId().toString(), user);
        return "OK sent to my kafka producer";
    }

}
