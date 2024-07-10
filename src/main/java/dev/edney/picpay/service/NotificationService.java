package dev.edney.picpay.service;

import dev.edney.picpay.client.NotificationClient;
import dev.edney.picpay.entity.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationClient notificationClient;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void sendNotification(Transfer transfer) {
        try {
            logger.info("Sending notification");

            var response = notificationClient.sendNotification(transfer);
            if(response.getStatusCode().isError()) {
                logger.error("Error sending notification, status code is not ok");
            }
        } catch (Exception e) {
            logger.error("Error while sending notification", e);
        }
    }
}
