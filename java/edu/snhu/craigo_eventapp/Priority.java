package edu.snhu.craigo_eventapp;

/**
 * This enum also can convert from integers in a defined way, which is handy throughout
 * the codebase.
 */

public enum Priority {
    LOW(0),
    MED(1),
    HIGH(2);

    private Integer mValue;
    Priority(Integer value) {
        this.mValue = value;
    }
    public Integer value() {
        return mValue;
    }

    public static Priority fromInt(Integer i) {
        for (Priority priority : Priority.values()) {
            if (priority.value() == i) return priority;
        }
        return null;
    }



}
