package io.philo.shop.messaging;

import org.apache.kafka.common.TopicPartition;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import io.philo.shop.exception.OrderNotFoundForCancelException;

@Configuration
public class KafkaListenerErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler(
            KafkaOperations<Object, Object> kafkaOperations,
            @Value("${app.kafka.topic.order-canceled-dlt}") String orderCanceledDltTopic
    ) {
		var recoverer = createDeadLetterPublishingRecoverer(kafkaOperations, orderCanceledDltTopic);
        var backOff = createBackOff();
        var errorHandler = createDefaultErrorHandler(recoverer, backOff);
        return errorHandler;
    }

    private static @NonNull DeadLetterPublishingRecoverer createDeadLetterPublishingRecoverer(KafkaOperations<Object, Object> kafkaOperations,
        String orderCanceledDltTopic) {
        return new DeadLetterPublishingRecoverer(
            kafkaOperations,
            (record, ex) -> new TopicPartition(orderCanceledDltTopic, record.partition())
        );
    }

    private static @NonNull ExponentialBackOffWithMaxRetries createBackOff() {
        var backOff = new ExponentialBackOffWithMaxRetries(2);
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(2000L);
        return backOff;
    }

    private static @NonNull DefaultErrorHandler createDefaultErrorHandler(DeadLetterPublishingRecoverer recoverer,
        ExponentialBackOffWithMaxRetries backOff) {
        var errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(OrderNotFoundForCancelException.class);
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
