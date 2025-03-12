package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.DeleteTaskRequest;

@Dao
public interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<TaskEntity> tasks);

    @Update
    int update(TaskEntity task);

    @Query("DELETE FROM tasks WHERE id = :id")
    int delete(int id);

    @Query("SELECT * FROM tasks")
    LiveData<List<TaskEntity>> findAllAsLiveData();

    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY sortOrder ASC")
    List<TaskEntity> findByRoutineId(int routineId);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    TaskEntity findById(int taskId);

    @Query("SELECT MAX(sortOrder) FROM tasks WHERE routineId = :routineId")
    int getMaxSortOrder(int routineId);

    @Transaction
    default int append(TaskEntity task) {
        var maxSortOrder = getMaxSortOrder(task.routineId);
        var newTask = new TaskEntity(
                task.routineId,
                task.name,
                maxSortOrder + 1,
                task.checkedOff,
                task.taskTime
        );
        return Math.toIntExact(insert(newTask));
    }
}
