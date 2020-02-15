package com.example.kafka.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class Producer {
    @Autowired
    private KafkaTemplate<String, String> template;

    @Value("${app.topic.name}")
    private String topic;

    public ListenableFuture<SendResult<String, String>> send(String message) {
        return template.send(topic, message);
    }
}
