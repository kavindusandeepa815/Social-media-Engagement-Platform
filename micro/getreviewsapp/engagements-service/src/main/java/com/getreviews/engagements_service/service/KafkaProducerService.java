package com.getreviews.engagements_service.service;

import com.getreviews.core_service.dto.EngagementCompleteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, EngagementCompleteDTO> kafkaTemplate;
    private static final String TOPIC = "engagement-completed";

    public void sendEngagementCompletedEvent(EngagementCompleteDTO event) {
        kafkaTemplate.send(TOPIC, event.getTaskId().toString(), event);
    }

//    private final NewTopic newTopic;
//    private final KafkaTemplate<String, EngagementCompleteDTO> kafkaTemplate;
//
//    public void sendEngagementCompletedEvent(EngagementCompleteDTO event) {
//
//        Message<EngagementCompleteDTO> message = MessageBuilder
//                .withPayload(event)
//                .setHeader(KafkaHeaders.TOPIC, newTopic.name())
//                .build();
//
//        kafkaTemplate.send(message);
//    }
}
