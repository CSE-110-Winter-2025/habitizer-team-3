package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertRoutine(RoutineEntity routine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<RoutineEntity> routines);

    @Query("SELECT * FROM routines WHERE id = :routineId")
    RoutineEntity find(long routineId);

    @Query("SELECT * FROM routines")
    List<RoutineEntity> findAll();

    @Query("SELECT * FROM routines WHERE id = :routineId")
    LiveData<RoutineEntity> findAsLiveData(long routineId);

    @Query("SELECT * FROM routines")
    LiveData<List<RoutineEntity>> findAllAsLiveData();

    @Update
    void updateRoutine(RoutineEntity routine);

    @Query("DELETE FROM routines WHERE id = :id")
    void deleteRoutineById(long id);

    //grabbing a routine with its tasks
    @Transaction
    @Query("SELECT * FROM routines WHERE id = :routineId")
    RoutineWithTasksEntity getRoutineWithTasks(long routineId);

    //grabbing all routines with their tasks
    @Transaction
    @Query("SELECT * FROM routines")
    List<RoutineWithTasksEntity> getAllRoutinesWithTasks();

    //count routines
    @Query("SELECT COUNT(*) FROM routines")
    int count();

}

