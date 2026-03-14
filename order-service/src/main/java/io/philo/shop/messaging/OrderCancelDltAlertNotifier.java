package io.philo.shop.messaging;

import org.springframework.stereotype.Component;

import io.philo.shop.OrderCanceledEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderCancelDltAlertNotifier {

    public void notifyReprocessFailure(OrderCanceledEvent event, String dltTopic, int partition, long offset, Exception exception) {
        // TODO: Slack/Webhook/PagerDuty 등 외부 알림 채널 연동 지점
        log.error(
                "DLT 재처리에 실패했습니다. orderId={}, itemId={}, topic={}, partition={}, offset={}, reason={}, error={}",
                event.orderId(), event.itemId(), dltTopic, partition, offset, event.reason(), exception.getMessage(), exception
        );
    }
}
