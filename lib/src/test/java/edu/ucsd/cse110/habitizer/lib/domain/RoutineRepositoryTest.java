package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class RoutineRepositoryTest {
    @Test
    public void testInitialState() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        assertEquals("initial count should be zero", (Integer) 0, repository.count());
    }

    @Test
    public void testSaveAndFind() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Brush teeth", 1)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks);

        repository.save(routine);
        Subject<Routine> found = repository.find(42);

        assertEquals("saved routine should be retrievable", routine, found.getValue());
        assertEquals("count should be 1 after saving", (Integer) 1, repository.count());
    }

    @Test
    public void testFindAll() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        List<Task> morningTasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Brush teeth", 1)
        );
        List<Task> eveningTasks = List.of(
                new Task(3, "Shower", 0),
                new Task(4, "Sleep", 1)
        );

        Routine routine1 = new Routine(10, "Morning Routine", morningTasks);
        Routine routine2 = new Routine(20, "Evening Routine", eveningTasks);

        repository.save(routine1);
        repository.save(routine2);

        Subject<List<Routine>> allRoutines = repository.findAll();
        List<Routine> routinesList = allRoutines.getValue();

        assertEquals("should have saved two routines", 2, routinesList.size());
        assertTrue("should contain first routine", routinesList.contains(routine1));
        assertTrue("should contain second routine", routinesList.contains(routine2));
    }

    @Test
    public void testUpdateExisting() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        List<Task> originalTasks = List.of(
                new Task(1, "Wake up", 0)
        );
        List<Task> updatedTasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Exercise", 1)
        );

        Routine original = new Routine(42, "Morning Routine", originalTasks);
        repository.save(original);

        Routine updated = new Routine(42, "Updated Routine", updatedTasks);
        repository.save(updated);

        Subject<Routine> found = repository.find(42);
        assertEquals("routine should be updated", updated, found.getValue());
        assertEquals("count should still be 1", (Integer) 1, repository.count());
    }

    @Test
    public void testObserverNotification() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks);
        repository.save(routine);

        Subject<Routine> routineSubject = repository.find(42);
        final boolean[] wasNotified = {false};
        routineSubject.observe(r -> wasNotified[0] = true);

        List<Task> newTasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Exercise", 1)
        );
        Routine updated = new Routine(42, "Updated Routine", newTasks);
        repository.save(updated);

        assertTrue("observer should be notified of changes", wasNotified[0]);
    }
}