package com.group05.TC_LLM_Generator.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Generic domain event fired when any entity is created, updated, or deleted.
 * <p>
 * The centralized {@code WebSocketEventListener} picks this up and broadcasts
 * the appropriate STOMP message. Controllers and Services never need to know
 * about WebSocket — they just publish this event.
 * </p>
 *
 * <h3>Usage in a Service:</h3>
 * <pre>{@code
 * eventPublisher.publishEvent(new EntityChangedEvent(
 *     this,
 *     EntityType.WORKSPACE,
 *     Action.CREATED,
 *     workspace.getWorkspaceId().toString(),
 *     null,                // parentId (null for top-level entities)
 *     workspaceResponse,   // payload DTO (null for DELETE)
 *     userId.toString()
 * ));
 * }</pre>
 */
@Getter
public class EntityChangedEvent extends ApplicationEvent {

    /**
     * The type of CRUD action performed.
     */
    public enum Action {
        CREATED, UPDATED, DELETED
    }

    /**
     * All entity types that support real-time broadcasting.
     * Add new entities here as the application grows.
     */
    public enum EntityType {
        WORKSPACE,
        PROJECT,
        STORY,
        TEST_PLAN,
        TEST_SUITE
    }

    private final EntityType entityType;
    private final Action action;
    private final String entityId;
    private final String parentId;
    private final Object payload;
    private final String performedBy;

    /**
     * @param source     the object that published this event (typically {@code this} in the service)
     * @param entityType which entity was affected
     * @param action     CREATED, UPDATED, or DELETED
     * @param entityId   the ID of the affected entity
     * @param parentId   the parent entity ID (e.g., workspaceId for a project), nullable
     * @param payload    the response DTO to broadcast (null for DELETE events)
     * @param performedBy the user ID who performed the action
     */
    public EntityChangedEvent(Object source,
                               EntityType entityType,
                               Action action,
                               String entityId,
                               String parentId,
                               Object payload,
                               String performedBy) {
        super(source);
        this.entityType = entityType;
        this.action = action;
        this.entityId = entityId;
        this.parentId = parentId;
        this.payload = payload;
        this.performedBy = performedBy;
    }
}
