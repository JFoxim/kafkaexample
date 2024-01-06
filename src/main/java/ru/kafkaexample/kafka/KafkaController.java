package ru.kafkaexample.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaController {
    private final UserKafkaProducer userKafkaProducer;


    @GetMapping("/kafka/send")
    public String sendMessageKafka() throws InterruptedException {
        User user = new User(1L, "Ivan", "Иван", "Петров", "Петрович", "М");
        userKafkaProducer.writeToKafka(user);
        return "OK";
    }

}
