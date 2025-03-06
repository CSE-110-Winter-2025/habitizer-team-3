package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<TaskEntity> tasks);

    @Update
    int update(TaskEntity task);

    @Delete
    int delete(TaskEntity task);

    // Query all tasks for a given routine
    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY sortOrder ASC")
    List<TaskEntity> findByRoutineId(int routineId);

    // Query a single task by ID
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    TaskEntity findById(int taskId);
}
