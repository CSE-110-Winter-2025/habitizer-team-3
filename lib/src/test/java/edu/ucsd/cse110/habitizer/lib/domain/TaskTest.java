package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskTest {
    @Test
    public void testConstructorAndGetters() {
        // Create a Task with ID=5, name="Wake Up", sortOrder=0
        Task task = new Task(5, "Wake Up", 0,null);

        // Check each field
        assertEquals("task id should be 5", (Integer) 5, task.id());
        assertEquals("task name should be Wake Up", "Wake Up", task.name());
        assertEquals("task sortOrder should be 0", 0, task.sortOrder());
        assertEquals("taskTime should be -1 when passed null", -1, (int)task.taskTime());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Two identical tasks
        Task t1 = new Task(1, "Make Coffee", 0, null);
        Task t2 = new Task(1, "Make Coffee", 0,null);

        // A different task
        Task t3 = new Task(2, "Brush Teeth", 1,null);

        // t1 and t2 are equal
        assertEquals("tasks with same fields should be equal", t1, t2);
        assertEquals("equal tasks should have same hashCode", t1.hashCode(), t2.hashCode());

        // t3 is different
        assertNotEquals("different tasks should not be equal", t1, t3);
        assertNotEquals("different tasks have different hashCode", t1.hashCode(), t3.hashCode());
    }
}
