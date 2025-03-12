package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(AppEntity app);

    @Query("SELECT * FROM app LIMIT 1")
    AppEntity find();

    @Query("SELECT * FROM app LIMIT 1")
    LiveData<AppEntity> findAsLiveData();

    @Query("SELECT COUNT(*) FROM app")
    int count();

    @Update
    int update(AppEntity app);

    @Query("DELETE FROM app")
    void delete();
}
