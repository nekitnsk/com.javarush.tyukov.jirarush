package com.javarush.jira.bugtracking.task;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskTagController {
    private final TaskService taskService;

    @PostMapping("/{taskId}/tags")
    public ResponseEntity<Void> addTag(@PathVariable Long taskId, @RequestBody String tag) {
        taskService.addTagToTask(taskId, tag);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/tags/{tag}")
    public ResponseEntity<Void> removeTag(@PathVariable Long taskId, @PathVariable String tag) {
        taskService.removeTagFromTask(taskId, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{taskId}/tags")
    public ResponseEntity<Set<String>> getTags(@PathVariable Long taskId) {

        return ResponseEntity.ok(taskService.getTaskTags(taskId));
    }
}
