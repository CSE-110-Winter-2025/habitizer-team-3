package edu.ucsd.cse110.habitizer.app;

import android.app.Application;
import androidx.room.Room;
import edu.ucsd.cse110.habitizer.app.data.db.HabitizerDatabase;
import edu.ucsd.cse110.habitizer.app.data.db.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.app.data.db.RoutineEntity;
import java.util.List;
import java.util.stream.Collectors;

public class HabitizerApplication extends Application {
    private RoutineRepository routineRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        HabitizerDatabase.class,
                        "habitizer-database"
                )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        this.routineRepository = new RoomRoutineRepository(
                database.routineDao(),
                database.taskDao()
        );

        var sharedPreferences = getSharedPreferences("habitizer", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            int routineCount = database.routineDao().count();

            if (routineCount == 0
                    && !InMemoryDataSource.DEFAULT_ROUTINES.isEmpty()) {
                List<RoutineEntity> defaultRoutineEntities = InMemoryDataSource.DEFAULT_ROUTINES.stream()
                        .map(RoutineEntity::fromRoutine)
                        .collect(Collectors.toList());

                database.routineDao().insert(defaultRoutineEntities);
            }
            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
}
