package edu.ucsd.cse110.habitizer.lib.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}

