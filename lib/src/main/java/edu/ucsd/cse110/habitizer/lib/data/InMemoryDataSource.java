package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskList;
import edu.ucsd.cse110.habitizer.lib.util.MutableSubject;
import edu.ucsd.cse110.habitizer.lib.util.SimpleSubject;
import edu.ucsd.cse110.habitizer.lib.util.Subject;
/**
 * Class used as a sort of "database" of decks and flashcards that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private final Map<Integer, Routine> routines = new HashMap<>();
    private final Map<Integer, MutableSubject<Routine>> routineSubjects = new HashMap<>();
    private final MutableSubject<List<Routine>> allRoutinesSubject = new SimpleSubject<>();

    public InMemoryDataSource() {

    }

    public List<Routine> getRoutines() {
        return List.copyOf(routines.values());
    }

    public Routine getRoutine(int id) {
        return routines.get(id);
    }

    public Subject<Routine> getRoutineSubject(int id) {
        if (!routineSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Routine>();
            subject.setValue(getRoutine(id));
            routineSubjects.put(id, subject);
        }
        return routineSubjects.get(id);
    }

    public Subject<List<Routine>> getAllRoutinesSubject() {
        return allRoutinesSubject;
    }

    public void putRoutine(Routine routine) {
        routines.put(routine.id(), routine);
        if (routineSubjects.containsKey(routine.id())) {
            routineSubjects.get(routine.id()).setValue(routine);
        }
        allRoutinesSubject.setValue(getRoutines());
    }

    public final static TaskList morningTasks = new TaskList(List.of(
            new Task(0, "Shower", 0, null),
            new Task(1, "Brush Teeth", 1, null),
            new Task(2, "Dress", 2, null),
            new Task(3, "Make Coffee", 3, null),
            new Task(4, "Make Lunch", 4, null),
            new Task(5, "Dinner Prep", 5, null),
            new Task(6, "Pack Bag", 6, null)
        )
    );

    public final static TaskList eveningTasks = new TaskList(List.of(
            new Task(0, "Dinner", 0, null),
            new Task(1, "Wash Dishes", 1, null),
            new Task(2, "Finish Homework", 2, null),
            new Task(3, "Review Lecture Notes", 3, null),
            new Task(4, "Plan Next Day", 4, null),
            new Task(5, "Watch Show", 5, null),
            new Task(6, "Read", 6, null)
        )
    );


    public final static List<Routine> DEFAULT_ROUTINES = List.of(
            new Routine(0, "Morning", morningTasks, 30),
            new Routine(1, "Evening", eveningTasks, 45)
    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        for (Routine r : DEFAULT_ROUTINES) {
            data.putRoutine(r);
        }
        return data;
    }

    public void removeTask(int routineId, int taskId) {
        Routine routine = routines.get(routineId);
        if (routine == null) return;

        // Find the task to remove
        Task taskToRemove = null;
        for (Task task : routine.taskList().tasks()) {
            if (task.id() == taskId) {
                taskToRemove = task;
                break;
            }
        }

        if (taskToRemove == null) return;

        int sortOrder = taskToRemove.sortOrder();

        // Get current max sort order for this routine's tasks
        int maxSortOrder = routine.taskList().tasks().stream()
                .mapToInt(Task::sortOrder)
                .max()
                .orElse(-1);

        // Create a new task list without the removed task
        List<Task> updatedTasks = routine.taskList().tasks().stream()
                .filter(task -> task.id() != taskId)
                .collect(Collectors.toList());

        // Update the routine
        TaskList updatedTaskList = new TaskList(updatedTasks);
        updatedTaskList.setLastCheckoffTime(routine.taskList().lastTaskCheckoffTime());
        updatedTaskList.setCurrentTaskId(routine.taskList().currentTaskId());
        putRoutine(routine.withTasks(updatedTaskList));

        // Shift sort orders for tasks that came after the removed task
        shiftTaskSortOrders(routineId, sortOrder + 1, maxSortOrder, -1);
    }

    public void shiftTaskSortOrders(int routineId, int from, int to, int by) {
        Routine routine = routines.get(routineId);
        if (routine == null) return;

        // Get tasks that need sort order updates and create copies with updated sort orders
        List<Task> tasksToUpdate = new ArrayList<>();

        for (Task task : routine.taskList().tasks()) {
            if (task.sortOrder() >= from && task.sortOrder() <= to) {
                // Create a copy with new sort order (assuming Task is immutable)
                Task updatedTask = new Task(
                        task.id(),
                        task.name(),
                        task.sortOrder() + by,
                        task.taskTime()
                );
                tasksToUpdate.add(updatedTask);
            }
        }

        if (tasksToUpdate.isEmpty()) return;

        // Create updated task list
        List<Task> allTasks = new ArrayList<>(routine.taskList().tasks());

        // Replace old tasks with updated ones
        for (int i = 0; i < allTasks.size(); i++) {
            Task task = allTasks.get(i);
            if (task.sortOrder() >= from && task.sortOrder() <= to) {
                // Find the updated version of this task
                for (Task updatedTask : tasksToUpdate) {
                    if (updatedTask.id() == task.id()) {
                        allTasks.set(i, updatedTask);
                        break;
                    }
                }
            }
        }

        // Create new TaskList and update routine
        TaskList updatedTaskList = new TaskList(allTasks);
        updatedTaskList.setLastCheckoffTime(routine.taskList().lastTaskCheckoffTime());
        updatedTaskList.setCurrentTaskId(routine.taskList().currentTaskId());

        putRoutine(routine.withTasks(updatedTaskList));
    }
}

