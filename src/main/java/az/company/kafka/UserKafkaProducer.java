package az.company.kafka;

import az.company.event.UserCreatedEvent;
import az.company.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserKafkaProducer {

    private static final String USER_CREATED_TOPIC = "user.created";
    private static final String USER_UPDATED_TOPIC = "user.updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreatedEvent(UserCreatedEvent event) {
        kafkaTemplate.send(USER_CREATED_TOPIC, event.getId().toString(), event);
    }

    public void publishUserUpdatedEvent(UserUpdatedEvent event) {
        kafkaTemplate.send(USER_UPDATED_TOPIC, event.getId().toString(), event);
    }
}
