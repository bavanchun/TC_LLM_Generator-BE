package com.group05.TC_LLM_Generator.infrastructure.config;

import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized WebSocket event broadcaster.
 * <p>
 * Listens for {@link EntityChangedEvent} from any service in the application
 * and automatically broadcasts the corresponding STOMP message to the correct topic.
 * </p>
 * <p>
 * <b>To add a new entity type:</b> simply add a new case to {@link #resolveTopic(EntityChangedEvent)}.
 * No other changes are needed — the service just needs to publish an {@code EntityChangedEvent}.
 * </p>
 *
 * <h3>Topic Convention:</h3>
 * <ul>
 *   <li>Top-level: {@code /topic/{entities}} (e.g., {@code /topic/workspaces})</li>
 *   <li>Nested: {@code /topic/{parent}/{parentId}/{entities}} (e.g., {@code /topic/workspaces/{id}/projects})</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handles all entity change events and broadcasts them via WebSocket.
     */
    @EventListener
    public void handleEntityChanged(EntityChangedEvent event) {
        String topic = resolveTopic(event);

        // Build a generic message envelope
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("entityType", event.getEntityType().name());
        message.put("action", event.getAction().name());
        message.put("entityId", event.getEntityId());

        if (event.getParentId() != null) {
            message.put("parentId", event.getParentId());
        }

        if (event.getPayload() != null) {
            message.put("payload", event.getPayload());
        }

        message.put("performedBy", event.getPerformedBy());

        // Cast to Object to resolve ambiguous method call convertAndSend(String, Object) vs convertAndSend(Object, Map)
        messagingTemplate.convertAndSend(topic, (Object) message);

        log.debug("[WebSocket] Broadcast {}.{} to topic '{}' (entityId={})",
                event.getEntityType(), event.getAction(), topic, event.getEntityId());
    }

    /**
     * Resolves the STOMP topic based on entity type and parent context.
     * <p>
     * Add new entity types here as the application grows.
     * </p>
     */
    private String resolveTopic(EntityChangedEvent event) {
        return switch (event.getEntityType()) {
            case WORKSPACE        -> "/topic/workspaces";
            case WORKSPACE_MEMBER -> "/topic/workspaces/" + event.getParentId() + "/members";
            case PROJECT          -> "/topic/workspaces/" + event.getParentId() + "/projects";
            case STORY            -> "/topic/projects/" + event.getParentId() + "/stories";
            case TEST_CASE        -> "/topic/projects/" + event.getParentId() + "/test-cases";
            case TEST_PLAN        -> "/topic/projects/" + event.getParentId() + "/test-plans";
            case TEST_SUITE       -> "/topic/projects/" + event.getParentId() + "/test-suites";
        };
    }
}
