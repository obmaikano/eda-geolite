package com.geolite.drilling.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DrillingDeletedEvent extends Event {
    private UUID holeId;
}
