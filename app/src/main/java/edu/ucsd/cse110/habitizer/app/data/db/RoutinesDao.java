package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoutinesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(RoutineEntity routine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<RoutineEntity> routines);

    @Query("SELECT * FROM routines WHERE id = :id")
    RoutineEntity find(int id);

    @Query("SELECT * FROM routines")
    List<RoutineEntity> findAll();

    @Query("SELECT * FROM routines WHERE id = :id")
    LiveData<RoutineEntity> findAsLiveData(int id);

    @Query("SELECT * FROM routines")
    LiveData<List<RoutineEntity>> findAllAsLiveData();

    @Query("SELECT COUNT(*) from routines")
    int count();

    @Update
    int update(RoutineEntity routine);

    @Query("DELETE FROM routines WHERE id = :id")
    void delete(int id);
}
