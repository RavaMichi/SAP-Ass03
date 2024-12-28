package service.infrastructure.events;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface KafkaEventClient {

    @Topic("user-topic")
    void sendUserEvent(@KafkaKey String key, String value);
    @Topic("bike-topic")
    void sendBikeEvent(@KafkaKey String key, String value);
    @Topic("ride-topic")
    void sendRideEvent(@KafkaKey String key, String value);
}
