package com.geolite.scenarios.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * This event is published when a project is deleted.
 *
 * @author Obakeng Maikano
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDeletedEvent extends Event {
    private UUID projectId;
}