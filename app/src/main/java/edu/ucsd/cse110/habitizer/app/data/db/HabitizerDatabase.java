package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = { RoutineEntity.class, TaskEntity.class, AppDao.class },
        version = 1,
        exportSchema = false
)
public abstract class HabitizerDatabase extends RoomDatabase {
    public abstract RoutinesDao routinesDao();
    public abstract TasksDao tasksDao();
    public abstract AppDao appDao();
}
