package agent.abike;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface ABikeKafkaClient {

    @Topic("bike-topic")
    void sendBikeUpdate(@KafkaKey String key, String value);
}
