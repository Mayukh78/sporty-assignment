package com.example.sporty.sporty.service;

import com.example.sporty.sporty.config.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventDataProducer {
    private final Logger logger = LoggerFactory.getLogger(KafkaEventDataProducer.class);

    @Autowired
    private ApplicationConfiguration appConfig;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String key, String message) {
        // .send() is asynchronous by default
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(appConfig.getEventPublishingTopicName(), key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // SUCCESS: Message reached Kafka
                logger.info("Sent message=[{}] with offset=[{}]",
                        message, result.getRecordMetadata().offset());
            } else {
                // FAILURE: All retries exhausted
                logger.error("Unable to send message=[{}] due to : {}",
                        message, ex.getMessage());

                handlePermanentFailure(appConfig.getEventPublishingTopicName(), key, message, ex);
            }
        });
    }

    private void handlePermanentFailure(String topic, String key, String message, Throwable ex) {
        logger.error("Couldn't publish event {} to topic {}", message, topic);
        // Example: myRepository.save(new FailedMessage(topic, key, message, ex.getMessage()));
    }
}
