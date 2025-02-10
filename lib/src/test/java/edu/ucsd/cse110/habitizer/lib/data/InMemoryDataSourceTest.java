package edu.ucsd.cse110.habitizer.lib.data;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class InMemoryDataSourceTest {
    @Test
    public void testInitialState() {
        InMemoryDataSource dataSource = new InMemoryDataSource();

        assertTrue("initial routines list should be empty", dataSource.getRoutines().isEmpty());
    }

    @Test
    public void testPutAndGetRoutine() {
        InMemoryDataSource dataSource = new InMemoryDataSource();

        List<Task> tasks = List.of(
                new Task(1, "Wake Up", 0),
                new Task(2, "Brush Teeth", 1)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks);

        dataSource.putRoutine(routine);
        Routine retrieved = dataSource.getRoutine(42);

        assertEquals("retrieved routine should match saved routine", routine, retrieved);
        assertEquals("routines list should contain one routine", 1, dataSource.getRoutines().size());
    }

    @Test
    public void testGetRoutineSubject() {
        InMemoryDataSource dataSource = new InMemoryDataSource();

        List<Task> tasks = List.of(
                new Task(1, "Wake Up", 0)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks);

        dataSource.putRoutine(routine);
        Subject<Routine> subject = dataSource.getRoutineSubject(42);

        assertNotNull("routine subject should not be null", subject);
        assertEquals("subject value should match saved routine", routine, subject.getValue());
    }

    @Test
    public void testGetAllRoutinesSubject() {
        InMemoryDataSource dataSource = new InMemoryDataSource();

        List<Task> tasks1 = List.of(
                new Task(1, "Wake Up", 0)
        );
        List<Task> tasks2 = List.of(
                new Task(2, "Sleep", 0)
        );

        Routine routine1 = new Routine(1, "Morning Routine", tasks1);
        Routine routine2 = new Routine(2, "Evening Routine", tasks2);

        dataSource.putRoutine(routine1);
        dataSource.putRoutine(routine2);

        Subject<List<Routine>> allRoutines = dataSource.getAllRoutinesSubject();
        List<Routine> routinesList = allRoutines.getValue();

        assertEquals("should have two routines", 2, routinesList.size());
        assertTrue("should contain first routine", routinesList.contains(routine1));
        assertTrue("should contain second routine", routinesList.contains(routine2));
    }

    @Test
    public void testUpdateExistingRoutine() {
        InMemoryDataSource dataSource = new InMemoryDataSource();

        List<Task> originalTasks = List.of(
                new Task(1, "Wake Up", 0)
        );
        List<Task> updatedTasks = List.of(
                new Task(1, "Wake Up", 0),
                new Task(2, "Exercise", 1)
        );

        Routine original = new Routine(42, "Morning Routine", originalTasks);
        dataSource.putRoutine(original);

        Routine updated = new Routine(42, "Updated Routine", updatedTasks);
        dataSource.putRoutine(updated);

        Routine retrieved = dataSource.getRoutine(42);
        assertEquals("routine should be updated", updated, retrieved);
        assertEquals("routines list should still have one routine", 1, dataSource.getRoutines().size());
    }

    @Test
    public void testFromDefault() {
        InMemoryDataSource dataSource = InMemoryDataSource.fromDefault();
        List<Routine> routines = dataSource.getRoutines();

        assertEquals("should have one default routine", 1, routines.size());

        Routine defaultRoutine = routines.get(0);
        assertEquals("default routine should have id 0", (Integer) 0, defaultRoutine.id());
        assertEquals("default routine should be named Morning", "Morning", defaultRoutine.name());
        assertEquals("default routine should have 6 tasks", 7, defaultRoutine.tasks().size());
    }

    @Test
    public void testSubjectNotification() {
        InMemoryDataSource dataSource = new InMemoryDataSource();

        List<Task> tasks = List.of(
                new Task(1, "Wake Up", 0)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks);

        dataSource.putRoutine(routine);
        Subject<Routine> subject = dataSource.getRoutineSubject(42);

        final boolean[] wasNotified = {false};
        subject.observe(r -> wasNotified[0] = true);

        List<Task> newTasks = List.of(
                new Task(1, "Wake Up", 0),
                new Task(2, "Exercise", 1)
        );
        Routine updated = new Routine(42, "Updated Routine", newTasks);
        dataSource.putRoutine(updated);

        assertTrue("observer should be notified of changes", wasNotified[0]);
    }
}