package com.vasilebreban.trainticketing.optional;

import java.util.Comparator;
import java.util.List;

public class TaskPrioritizerDemo {

    public static void main(String[] args) {
        TaskPriorityCalculator calculator = new TaskPriorityCalculator();

        List<Task> tasks = List.of(
                Task.builder()
                        .title("Finish Java trainee assignment")
                        .urgency(5)
                        .importance(5)
                        .estimatedHours(4)
                        .deadlineDays(1)
                        .build(),
                Task.builder()
                        .title("Refactor old project")
                        .urgency(2)
                        .importance(3)
                        .estimatedHours(6)
                        .deadlineDays(10)
                        .build(),
                Task.builder()
                        .title("Prepare interview answers")
                        .urgency(4)
                        .importance(5)
                        .estimatedHours(2)
                        .deadlineDays(3)
                        .build(),
                Task.builder()
                        .title("Clean local files")
                        .urgency(1)
                        .importance(1)
                        .estimatedHours(1)
                        .deadlineDays(30)
                        .build()
        );

        List<Task> prioritizedTasks = tasks.stream()
                .sorted(Comparator.comparingInt(calculator::calculateScore).reversed())
                .toList();

        System.out.println("Prioritized tasks:");

        for (Task task : prioritizedTasks) {
            int score = calculator.calculateScore(task);

            System.out.println(
                    task.getTitle()
                            + " | score: " + score
                            + " | urgency: " + task.getUrgency()
                            + " | importance: " + task.getImportance()
                            + " | estimatedHours: " + task.getEstimatedHours()
                            + " | deadlineDays: " + task.getDeadlineDays()
            );
        }
    }
}