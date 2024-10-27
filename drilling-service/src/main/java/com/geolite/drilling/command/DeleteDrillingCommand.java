package com.geolite.drilling.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteDrillingCommand {
    private UUID holeId;
}
