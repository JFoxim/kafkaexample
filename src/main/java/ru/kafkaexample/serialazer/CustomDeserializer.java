package ru.kafkaexample.serialazer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.kafkaexample.kafka.User;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomDeserializer implements Deserializer<ru.kafkaexample.other.User> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public ru.kafkaexample.other.User deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            System.out.println("Deserializing...");
            return objectMapper.readValue(new String(data, "UTF-8"), ru.kafkaexample.other.User.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to MessageDto");
        }
    }

    @Override
    public void close() {
    }
}
