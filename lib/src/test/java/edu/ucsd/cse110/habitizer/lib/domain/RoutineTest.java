package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public class RoutineTest {
    @Test
    public void testConstructorAndGetters() {
        List<Task> tasks = List.of(
                new Task(0, "Wake up", 0),
                new Task(1, "Brush teeth", 1)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks);

        assertEquals("routine id should match", (Integer) 42, routine.id());
        assertEquals("routine name should match", "Morning Routine", routine.name());
        assertEquals("routine tasks should match", tasks, routine.tasks());
    }

    @Test
    public void testWithId() {
        List<Task> tasks = List.of(
                new Task(0, "Wake up", 0)
        );
        Routine original = new Routine(null, "Unnamed", tasks);

        Routine updated = original.withId(99);
        assertNotEquals("updated and original should not be the same object", original, updated);
        assertEquals("new id should be 99", (Integer) 99, updated.id());
        assertEquals("name should remain unchanged", "Unnamed", updated.name());
        assertEquals("tasks should remain unchanged", tasks, updated.tasks());

        // Original should still have null ID
        assertNull("original routine id should still be null", original.id());
    }

    @Test
    public void testEqualsAndHashCode() {
        List<Task> tasks1 = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Brush teeth", 1)
        );
        List<Task> tasks2 = List.of(
                new Task(3, "Shower", 0)
        );

        Routine r1 = new Routine(10, "Routine A", tasks1);
        Routine r2 = new Routine(10, "Routine A", tasks1);
        Routine r3 = new Routine(11, "Routine B", tasks2);

        // r1 and r2 have same fields => should be equal
        assertEquals("routines with same fields should be equal", r1, r2);
        assertEquals("equal objects should have same hashCode", r1.hashCode(), r2.hashCode());

        // r3 is different => should not be equal
        assertNotEquals("different routines should not be equal", r1, r3);
        assertNotEquals("different routines have different hashCode", r1.hashCode(), r3.hashCode());
    }
}
