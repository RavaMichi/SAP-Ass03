package service.infrastructure.events;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface KafkaEventClient {

    @Topic("bike-topic")
    void sendBikeEvent(@KafkaKey String key, String value);
    @Topic("station-topic")
    void sendStationEvent(@KafkaKey String key, String value);
}
