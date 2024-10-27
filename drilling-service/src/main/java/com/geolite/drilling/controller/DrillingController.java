package com.geolite.drilling.controller;

import com.geolite.drilling.command.DeleteDrillingCommand;
import com.geolite.drilling.command.StartDrillingCommand;
import com.geolite.drilling.command.UpdateDrillingCommand;
import com.geolite.drilling.command.DrillingCommandHandler;
import com.geolite.drilling.model.Drilling;
import com.geolite.drilling.model.DrillingReadModel;
import com.geolite.drilling.query.DrillingQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drillings")
public class DrillingController {

    private final DrillingCommandHandler commandHandler;
    private final DrillingQueryHandler queryHandler;

    @Autowired
    public DrillingController(DrillingCommandHandler commandHandler, DrillingQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    public ResponseEntity<?> createDrilling(@RequestBody StartDrillingCommand command) {
        try {
            Drilling drilling = commandHandler.handleStartDrilling(command);
            return ResponseEntity.ok(drilling);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{holeId}")
    public ResponseEntity<?> updateDrilling(@PathVariable UUID holeId, @RequestBody UpdateDrillingCommand command) {
        try {
            command.setHoleId(holeId);
            commandHandler.handleUpdateDrilling(command);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{holeId}")
    public ResponseEntity<?> deleteDrilling(@PathVariable UUID holeId) {
        try {
            commandHandler.handleDeleteDrilling(new DeleteDrillingCommand(holeId));
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{holeId}")
    public ResponseEntity<?> getDrilling(@PathVariable UUID holeId) {
        try {
            DrillingReadModel drilling = queryHandler.getDrillingById(holeId);
            return ResponseEntity.ok(drilling);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllDrillings() {
        List<DrillingReadModel> drillings = queryHandler.getAllDrillings();
        return ResponseEntity.ok(drillings);
    }
}
