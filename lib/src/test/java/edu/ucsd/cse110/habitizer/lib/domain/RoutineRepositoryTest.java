package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class RoutineRepositoryTest {

    private InMemoryDataSource dataSource;
    private RoutineRepository repository;


    @Before
    public void setup() {
        dataSource = new InMemoryDataSource();
        repository = new SimpleRoutineRepository(dataSource);
    }


    @Test
    public void testInitialState() {
        assertEquals("initial count should be zero", (Integer) 0, repository.count());
    }

    @Test
    public void testSaveAndFind() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0, null),
                new Task(2, "Brush teeth", 1,null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);

        repository.save(routine);
        Subject<Routine> found = repository.find(42);

        assertEquals("saved routine should be retrievable", routine, found.getValue());
        assertEquals("count should be 1 after saving", (Integer) 1, repository.count());
    }

    @Test
    public void testFindAll() {
        List<Task> morningTasks = List.of(
                new Task(1, "Wake up", 0, null),
                new Task(2, "Brush teeth", 1, null)
        );
        List<Task> eveningTasks = List.of(
                new Task(3, "Shower", 0, null),
                new Task(4, "Sleep", 1, null)
        );

        Routine routine1 = new Routine(10, "Morning Routine", new TaskList(morningTasks), 30);
        Routine routine2 = new Routine(20, "Evening Routine", new TaskList(eveningTasks), 30);

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
                new Task(1, "Wake up", 0, null)
        );
        List<Task> updatedTasks = List.of(
                new Task(1, "Wake up", 0, null),
                new Task(2, "Exercise", 1, null)
        );

        Routine original = new Routine(42, "Morning Routine", new TaskList(originalTasks), 30);
        repository.save(original);

        Routine updated = new Routine(42, "Updated Routine", new TaskList(updatedTasks), 30);
        repository.save(updated);

        Subject<Routine> found = repository.find(42);
        assertEquals("routine should be updated", updated, found.getValue());
        assertEquals("count should still be 1", (Integer) 1, repository.count());
    }

    @Test
    public void testObserverNotification() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        Subject<Routine> routineSubject = repository.find(42);
        final boolean[] wasNotified = {false};
        routineSubject.observe(r -> wasNotified[0] = true);

        List<Task> newTasks = List.of(
                new Task(1, "Wake up", 0, null),
                new Task(2, "Exercise", 1, null)
        );
        Routine updated = new Routine(42, "Updated Routine", new TaskList(newTasks), 30);
        repository.save(updated);

        assertTrue("observer should be notified of changes", wasNotified[0]);
    }

    @Test
    public void testAddTaskToRoutine() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        Task newTask = new Task(2, "Wake up", 1, null);

        repository.addTaskToRoutine(42, newTask);

        Routine updatedRoutine = repository.find(42).getValue();
        assertNotNull("Updated routine should not be null", updatedRoutine);
        assertEquals("Updated routine should have 2 tasks", updatedRoutine.taskList().tasks().size(), 2);

        Task addedTask = updatedRoutine.taskList().tasks().get(1);
        assertEquals("Second task should have new task's name", addedTask.name(), newTask.name());
        assertEquals("New task id should be previous task list size", 1, (int) Objects.requireNonNull(addedTask.id()));
        assertEquals("New task sort order should be previous task list size", 1, addedTask.sortOrder());
    }

    @Test
    public void testEditTask() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        EditTaskRequest req = new EditTaskRequest(42, 1, 0, "Eat Breakfast", null);
        repository.editTask(req);

        Routine updatedRoutine = repository.find(42).getValue();
        assertNotNull("Updated routine should not be null", updatedRoutine);
        assertEquals("Updated routine should not have additional tasks", updatedRoutine.taskList().tasks().size(), 1);
        assertEquals("Edited task should have new name", req.taskName(), updatedRoutine.taskList().tasks().get(0).name());
    }

    @Test
    public void testAutoAssignId() {
        List<Task> tasks = List.of(
                new Task(1, "Wake up", 0, null)
        );
        Routine routine = new Routine(null, "Auto Assigned Routine", new TaskList(tasks), 30);
        repository.save(routine);

        Subject<List<Routine>> allRoutines = repository.findAll();
        List<Routine> routines = allRoutines.getValue();
        assertNotNull("Routines list should not be null", routines);
        assertEquals("Should have 1 routine", 1, routines.size());
        Routine saved = routines.get(0);
        assertNotNull("Auto-assigned routine id should not be null", saved.id());

        Subject<Routine> found = repository.find(saved.id());
        assertEquals("Saved routine should be retrievable by its new id", saved, found.getValue());
    }

    @Test
    public void testDeleteTask() {
        List<Task> tasks = List.of(
                new Task(0, "Wake up", 0, null),
                new Task(1, "Brush teeth", 1, null),
                new Task(2, "Get dressed", 2, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        assertEquals("Routine should have 3 tasks initially", 3,
                repository.find(42).getValue().taskList().tasks().size());

        DeleteTaskRequest req = new DeleteTaskRequest(42, 1, 1);
        repository.deleteTask(req);

        Routine updatedRoutine = repository.find(42).getValue();

        assertEquals("Routine should have 2 tasks after deletion", 2,
                updatedRoutine.taskList().tasks().size());


        assertEquals("First task should remain", "Wake up",
                updatedRoutine.taskList().tasks().get(0).name());
        assertEquals("Third task should now be second", "Get dressed",
                updatedRoutine.taskList().tasks().get(1).name());

        assertEquals("First task should keep sort order 0", 0,
                updatedRoutine.taskList().tasks().get(0).sortOrder());
        assertEquals("Third task should now have sort order 1", 1,
                updatedRoutine.taskList().tasks().get(1).sortOrder());
    }

    @Test
    public void testDeleteLastTask() {
        List<Task> tasks = List.of(
                new Task(0, "Wake up", 0, null),
                new Task(1, "Brush teeth", 1, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        DeleteTaskRequest req = new DeleteTaskRequest(42, 1, 1);
        repository.deleteTask(req);

        Routine updatedRoutine = repository.find(42).getValue();

        assertEquals("Routine should have 1 task after deletion", 1,
                updatedRoutine.taskList().tasks().size());

        assertEquals("Only the first task should remain", "Wake up",
                updatedRoutine.taskList().tasks().get(0).name());
        assertEquals("First task should have id 0", 0,
                (int)updatedRoutine.taskList().tasks().get(0).id());
        assertEquals("First task should have sort order 0", 0,
                updatedRoutine.taskList().tasks().get(0).sortOrder());
    }

    @Test
    public void testDeleteFirstTask() {
        List<Task> tasks = List.of(
                new Task(0, "Wake up", 0, null),
                new Task(1, "Brush teeth", 1, null),
                new Task(2, "Get dressed", 2, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        DeleteTaskRequest req = new DeleteTaskRequest(42, 0, 0);
        repository.deleteTask(req);

        Routine updatedRoutine = repository.find(42).getValue();

        assertEquals("Routine should have 2 tasks after deletion", 2,
                updatedRoutine.taskList().tasks().size());

        assertEquals("Second task should now be first", "Brush teeth",
                updatedRoutine.taskList().tasks().get(0).name());
        assertEquals("Third task should now be second", "Get dressed",
                updatedRoutine.taskList().tasks().get(1).name());

        assertEquals("Second task should now have sort order 0", 0,
                updatedRoutine.taskList().tasks().get(0).sortOrder());
        assertEquals("Third task should now have sort order 1", 1,
                updatedRoutine.taskList().tasks().get(1).sortOrder());
    }

    @Test
    public void testDeleteOnlyTask() {
        List<Task> tasks = List.of(
                new Task(0, "Wake up", 0, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        DeleteTaskRequest req = new DeleteTaskRequest(42, 0, 0);
        repository.deleteTask(req);

        Routine updatedRoutine = repository.find(42).getValue();

        assertEquals("Routine should have 0 tasks after deletion", 0,
                updatedRoutine.taskList().tasks().size());
    }

    @Test
    public void testObserverNotificationOnDelete() {
        List<Task> tasks = List.of(
                new Task(0, "Wake up", 0, null),
                new Task(1, "Brush teeth", 1, null)
        );
        Routine routine = new Routine(42, "Morning Routine", new TaskList(tasks), 30);
        repository.save(routine);

        Subject<Routine> routineSubject = repository.find(42);
        final boolean[] wasNotified = {false};
        routineSubject.observe(r -> wasNotified[0] = true);

        DeleteTaskRequest req = new DeleteTaskRequest(42, 1, 1);
        repository.deleteTask(req);

        assertTrue("Observer should be notified when a task is deleted", wasNotified[0]);
    }
}