package io.philo.shop.messaging;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
public class KafkaListenerErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler(
            KafkaOperations<Object, Object> kafkaOperations,
            @Value("${app.kafka.topic.order-canceled-dlt}") String orderCanceledDltTopic
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaOperations,
                (record, ex) -> new TopicPartition(orderCanceledDltTopic, record.partition())
        );

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(2);
        backOff.setInitialInterval(1000L); // 1st retry
        backOff.setMultiplier(2.0);        // exponential
        backOff.setMaxInterval(2000L);     // 2nd retry

        // 2회 재시도(1초 -> 2초) 후 DLT 전송 (총 처리 시도 3회).
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
