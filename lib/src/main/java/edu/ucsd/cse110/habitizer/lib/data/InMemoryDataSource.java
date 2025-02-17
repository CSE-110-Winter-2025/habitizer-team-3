package edu.ucsd.cse110.habitizer.lib.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Person;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.util.Subject;
/**
 * Class used as a sort of "database" of decks and flashcards that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private final Map<Integer, Routine> routines = new HashMap<>();
    private final Map<Integer, Subject<Routine>> routineSubjects = new HashMap<>();
    private final Subject<List<Routine>> allRoutinesSubject = new Subject<>();

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
            var subject = new Subject<Routine>();
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

    public final static List<Routine> DEFAULT_ROUTINES = List.of(
            new Routine(0, "Morning", List.of(
                    new Task(0, "Shower", 0, null),
                    new Task(1, "Brush Teeth", 1, null),
                    new Task(2, "Dress", 2, null),
                    new Task(3, "Make Coffee", 3, null),
                    new Task(4, "Make Lunch", 4, null),
                    new Task(5, "Dinner Prep", 5, null),
                    new Task(6, "Pack Bag", 6, null)
            ), 30)
    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        for (Routine r : DEFAULT_ROUTINES) {
            data.putRoutine(r);
        }
        return data;
    }
}

