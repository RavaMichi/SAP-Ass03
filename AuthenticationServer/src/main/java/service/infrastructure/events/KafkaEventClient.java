package service.infrastructure.events;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface KafkaEventClient {

    @Topic("user-topic")
    void sendUserEvent(@KafkaKey String key, String value);
}
