package edu.ucsd.cse110.habitizer.app;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;

import edu.ucsd.cse110.habitizer.app.data.db.HabitizerDatabase;
import edu.ucsd.cse110.habitizer.app.data.db.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.app.data.db.RoutineEntity;
import edu.ucsd.cse110.habitizer.app.data.db.TaskEntity;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;

public class HabitizerApplication extends Application {
    private static final String TAG = "HabitizerApplication";
    private RoutineRepository routineRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // 1) Build the database (with both RoutinesDao & TaskDao).
        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        HabitizerDatabase.class,
                        "habitizer-database"
                )
                .allowMainThreadQueries()
                .build();

        // 2) Inject *both* DAOs into your repository.
        this.routineRepository = new RoomRoutineRepository(
                database.routinesDao(),
                database.tasksDao()
        );

        // 3) If first run, populate default data.
        var sharedPreferences = getSharedPreferences("habitizer", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun || database.routinesDao().count() == 0) {
            Log.d(TAG, "First run or no routines - populating default data");

            // We'll use a more explicit approach
            for (Routine r : InMemoryDataSource.DEFAULT_ROUTINES) {
                Log.d(TAG, "Adding default routine: " + r.id() + " - " + r.name());

                // First save just the routine itself, not its tasks
                RoutineEntity routineEntity = RoutineEntity.fromRoutine(r);
                database.routinesDao().insert(routineEntity);

                // Then manually add each task
                for (Task t : r.taskList().tasks()) {
                    Log.d(TAG, "Adding task: " + t.id() + " - " + t.name() + " to routine: " + r.id());
                    TaskEntity taskEntity = TaskEntity.fromDomain(t, r.id());
                    database.tasksDao().insert(taskEntity);
                }
            }

            // Verify the data was inserted correctly
            Log.d(TAG, "After initialization: " + database.routinesDao().count() + " routines in database");
            Log.d(TAG, "After initialization - routine 0 tasks: " + database.tasksDao().findByRoutineId(0).size());
            Log.d(TAG, "After initialization - routine 1 tasks: " + database.tasksDao().findByRoutineId(1).size());

            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
}