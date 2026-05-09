package com.vasilebreban.trainticketing.optional;

public class TaskPriorityCalculator {

    public int calculateScore(Task task) {
        int importanceScore = task.getImportance() * 3;
        int urgencyScore = task.getUrgency() * 2;
        int deadlinePressure = calculateDeadlinePressure(task.getDeadlineDays());
        int effortPenalty = task.getEstimatedHours();

        return importanceScore + urgencyScore + deadlinePressure - effortPenalty;
    }

    private int calculateDeadlinePressure(int deadlineDays) {
        if (deadlineDays <= 1) {
            return 10;
        }

        if (deadlineDays <= 3) {
            return 6;
        }

        if (deadlineDays <= 7) {
            return 3;
        }

        return 0;
    }
}