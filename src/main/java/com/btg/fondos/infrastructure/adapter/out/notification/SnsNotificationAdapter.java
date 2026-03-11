package com.btg.fondos.infrastructure.adapter.out.notification;

import com.btg.fondos.domain.model.Client;
import com.btg.fondos.domain.model.NotificationType;
import com.btg.fondos.domain.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Component
@ConditionalOnProperty(name = "aws.sns.enabled", havingValue = "true")
public class SnsNotificationAdapter implements NotificationPort {

    private final SnsClient snsClient;
    private final String topicArn;

    public SnsNotificationAdapter(SnsClient snsClient,
                                   @Value("${aws.sns.topic-arn}") String topicArn) {
        this.snsClient = snsClient;
        this.topicArn = topicArn;
    }

    @Override
    public void sendNotification(Client client, String subject, String message) {
        if (client.getNotificationPreference() == NotificationType.SMS) {
            sendSms(client.getPhone(), message);
        } else {
            sendToTopic(subject, message);
        }
    }

    private void sendSms(String phoneNumber, String message) {
        PublishRequest request = PublishRequest.builder()
                .phoneNumber(phoneNumber)
                .message(message)
                .build();
        PublishResponse response = snsClient.publish(request);
        log.info("SMS enviado. MessageId: {}", response.messageId());
    }

    private void sendToTopic(String subject, String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .subject(subject)
                .message(message)
                .build();
        PublishResponse response = snsClient.publish(request);
        log.info("Notificación enviada al topic SNS. MessageId: {}", response.messageId());
    }
}
