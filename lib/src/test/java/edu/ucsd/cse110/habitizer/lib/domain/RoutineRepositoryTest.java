package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class RoutineRepositoryTest {

    private InMemoryDataSource dataSource;
    private RoutineRepository repository;


    @Before
    public void setup() {
        dataSource = new InMemoryDataSource();
        repository = new RoutineRepository(dataSource);
    }


    @Test
    public void testInitialState() {
        assertEquals("initial count should be zero", (Integer) 0, repository.count());
    }

    @Test
    public void testSaveAndFind() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Brush teeth", 1)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks, 30);

        repository.save(routine);
        Subject<Routine> found = repository.find(42);

        assertEquals("saved routine should be retrievable", routine, found.getValue());
        assertEquals("count should be 1 after saving", (Integer) 1, repository.count());
    }

    @Test
    public void testFindAll() {
        List<Task> morningTasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Brush teeth", 1)
        );
        List<Task> eveningTasks = List.of(
                new Task(3, "Shower", 0),
                new Task(4, "Sleep", 1)
        );

        Routine routine1 = new Routine(10, "Morning Routine", morningTasks, 30);
        Routine routine2 = new Routine(20, "Evening Routine", eveningTasks, 30);

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
        List<Task> originalTasks = List.of(
                new Task(1, "Wake up", 0)
        );
        List<Task> updatedTasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Exercise", 1)
        );

        Routine original = new Routine(42, "Morning Routine", originalTasks, 30);
        repository.save(original);

        Routine updated = new Routine(42, "Updated Routine", updatedTasks, 30);
        repository.save(updated);

        Subject<Routine> found = repository.find(42);
        assertEquals("routine should be updated", updated, found.getValue());
        assertEquals("count should still be 1", (Integer) 1, repository.count());
    }

    @Test
    public void testObserverNotification() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks, 30);
        repository.save(routine);

        Subject<Routine> routineSubject = repository.find(42);
        final boolean[] wasNotified = {false};
        routineSubject.observe(r -> wasNotified[0] = true);

        List<Task> newTasks = List.of(
                new Task(1, "Wake up", 0),
                new Task(2, "Exercise", 1)
        );
        Routine updated = new Routine(42, "Updated Routine", newTasks, 30);
        repository.save(updated);

        assertTrue("observer should be notified of changes", wasNotified[0]);
    }

    @Test
    public void testAddTaskToRoutine() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks, 30);
        repository.save(routine);

        Task newTask = new Task(2, "Wake up", 1);

        repository.addTaskToRoutine(42, newTask);

        Routine updatedRoutine = repository.find(42).getValue();
        assertNotNull("Updated routine should not be null", updatedRoutine);
        assertEquals("Updated routine should have 2 tasks", updatedRoutine.tasks().size(), 2);

        Task addedTask = updatedRoutine.tasks().get(1);
        assertEquals("Second task should have new task's name", addedTask.name(), newTask.name());
        assertEquals("New task id should be previous task list size", 1, (int) Objects.requireNonNull(addedTask.id()));
        assertEquals("New task sort order should be previous task list size", 1, addedTask.sortOrder());
    }

    @Test
    public void testEditTask() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0)
        );
        Routine routine = new Routine(42, "Morning Routine", tasks, 30);
        repository.save(routine);

        EditTaskRequest req = new EditTaskRequest(42, 1, 0, "Eat Breakfast");
        repository.editTask(req);

        Routine updatedRoutine = repository.find(42).getValue();
        assertNotNull("Updated routine should not be null", updatedRoutine);
        assertEquals("Updated routine should not have additional tasks", updatedRoutine.tasks().size(), 1);
        assertEquals("Edited task should have new name", req.taskName(), updatedRoutine.tasks().get(0).name());
    }
}