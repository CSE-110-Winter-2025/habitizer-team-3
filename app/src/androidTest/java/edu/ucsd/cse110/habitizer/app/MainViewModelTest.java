package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModelTest {

    private FakeRoutineRepository fakeRepo;
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        fakeRepo = new FakeRoutineRepository();
        viewModel = new MainViewModel(fakeRepo);
    }

    @Test
    public void testInitialAllRoutinesIsNull() {
        // Empty Repo should be null.
        assertNull("Expected initial allRoutines to be null",
                viewModel.getAllRoutines().getValue());
    }

    @Test
    public void testAllRoutinesUpdatesWhenRepositoryEmits() {
        // Create a list of routines.
        Routine routine1 = new Routine(1, "Morning Routine", Collections.emptyList(), 60);
        Routine routine2 = new Routine(2, "Evening Routine", Collections.emptyList(), 45);
        List<Routine> routines = Arrays.asList(routine1, routine2);

        // Simulate the repository containing a list of routines.
        fakeRepo.fakeAllRoutines.setValue(routines);

        // The MainViewModel's subject should now reflect the repository data.
        assertEquals("Expected allRoutines to update with repository data",
                routines, viewModel.getAllRoutines().getValue());
    }

    @Test
    public void testMultipleEmissionsUpdateAllRoutines() {
        // 1st routine.
        Routine routineA = new Routine(1, "Routine A", Collections.emptyList(), 30);
        List<Routine> routines1 = Collections.singletonList(routineA);
        fakeRepo.fakeAllRoutines.setValue(routines1);
        assertEquals("Expected allRoutines to update with 1st routine",
                routines1, viewModel.getAllRoutines().getValue());

        // 2nd and 3rd routine.
        Routine routineB = new Routine(2, "Routine B", Collections.emptyList(), 40);
        Routine routineC = new Routine(3, "Routine C", Collections.emptyList(), 50);
        List<Routine> routines2 = Arrays.asList(routineB, routineC);
        fakeRepo.fakeAllRoutines.setValue(routines2);
        assertEquals("Expected allRoutines to update with 2nd and 3rd",
                routines2, viewModel.getAllRoutines().getValue());
    }

    @Test
    public void testUpdateRoutineCallsRepositorySave() {
        Routine testRoutine = new Routine(null, "Test Routine", Collections.emptyList(), 120);
        viewModel.updateRoutine(testRoutine);
        // The fake repository records the last routine passed to save.
        assertEquals("updateRoutine should call repository.save()",
                testRoutine, fakeRepo.savedRoutine);
    }

    @Test
    public void testAddTaskToRoutineCallsRepository() {
        int routineId = 1;
        Task newTask = new Task(null, "New Task", 0);

        viewModel.addTaskToRoutine(routineId, newTask);

        // Verify the repository method was called (would require a spy/mocking framework like Mockito)
        // Here we assume FakeRoutineRepository records the last add task request
        assertEquals("Repository should receive correct routine ID", routineId, fakeRepo.lastAddedTaskRoutineId);
        assertEquals("Repository should receive correct task", newTask, fakeRepo.lastAddedTask);
    }

    @Test
    public void testEditTaskCallsRepository() {
        EditTaskRequest request = new EditTaskRequest(1, 2, 1, "Updated Task");

        viewModel.editTask(request);

        // Verify the repository method was called
        assertEquals("Repository should receive correct edit request", request, fakeRepo.lastEditTaskRequest);
    }

//    A fake implementation of RoutineRepository.
    private static class FakeRoutineRepository extends RoutineRepository {
        // Expose a Subject so we can simulate routines.
        final Subject<List<Routine>> fakeAllRoutines = new Subject<>();
        Routine savedRoutine = null;
        Task lastAddedTask;
        int lastAddedTaskRoutineId;
        EditTaskRequest lastEditTaskRequest;

        public FakeRoutineRepository() {
            // Pass a fake InMemoryDataSource to satisfy the superclass constructor.
            super(new FakeInMemoryDataSource());
        }

        @Override
        public Subject<List<Routine>> findAll() {
            return fakeAllRoutines;
        }

        @Override
        public void save(Routine routine) {
            savedRoutine = routine;
        }

        @Override
        public void addTaskToRoutine(int routineId, Task task) {
            this.lastAddedTaskRoutineId = routineId;
            this.lastAddedTask = task;
        }

        @Override
        public void editTask(EditTaskRequest req) {
            this.lastEditTaskRequest = req;
        }
    }

//    A fake implementation of InMemoryDataSource.
    private static class FakeInMemoryDataSource extends InMemoryDataSource {
        @Override
        public List<Routine> getRoutines() {
            return Collections.emptyList();
        }

        @Override
        public Subject<Routine> getRoutineSubject(int id) {
            return new Subject<>();
        }

        @Override
        public Subject<List<Routine>> getAllRoutinesSubject() {
            return new Subject<>();
        }

        @Override
        public void putRoutine(Routine routine) {
            // No-op for testing.
        }
    }
}
