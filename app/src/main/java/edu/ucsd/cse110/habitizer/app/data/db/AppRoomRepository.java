package edu.ucsd.cse110.habitizer.app.data.db;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import edu.ucsd.cse110.habitizer.app.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.habitizer.lib.domain.App;
import edu.ucsd.cse110.habitizer.lib.domain.AppRepository;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class AppRoomRepository implements AppRepository {
    private final AppDao appDao;
    public AppRoomRepository(AppDao appDao) {
        this.appDao = appDao;
    }

    public Subject<App> find() {
        LiveData<AppEntity> entityLiveData = appDao.findAsLiveData();
        LiveData<App> appLiveData = Transformations.map(entityLiveData, AppEntity::toApp);
        return new LiveDataSubjectAdapter<>(appLiveData);
    }
    public void save(App app) {
        Log.d(TAG, "Saving App State");
        appDao.insert(AppEntity.fromApp(app));
    }
}
