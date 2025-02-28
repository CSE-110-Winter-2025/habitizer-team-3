package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.EditTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.util.Subject;
import androidx.lifecycle.Observer;

public class RoomRoutineRepository implements RoutineRepository {
    private final RoutineDao routineDao;
    private final TaskDao taskDao;

    public RoomRoutineRepository(RoutineDao routineDao, TaskDao taskDao) {
        this.routineDao = routineDao;
        this.taskDao = taskDao;
    }
    @Override
    public Integer count() {
        return routineDao.count();
    }

    @Override
    public Subject<Routine> find(int id) {
        LiveData<RoutineWithTasksEntity> entityLiveData = routineDao.getRoutineWithTasksAsLiveData(id);
        LiveData<Routine> routineLiveData = Transformations.map(entityLiveData, routineWithTasks -> {
            if (routineWithTasks == null) return null;
            return routineWithTasks.routine.toRoutine(routineWithTasks.tasks);
        });

        Subject<Routine> routineSubject = new Subject<>();
        routineLiveData.observeForever(new Observer<Routine>() {
            @Override
            public void onChanged(Routine routine) {
                routineSubject.setValue(routine);
            }
        });

        return routineSubject;
    }

    @Override
    public Subject<List<Routine>> findAll() {
        LiveData<List<RoutineEntity>> entitiesLiveData = routineDao.findAllAsLiveData();
        LiveData<List<Routine>> routinesLiveData = Transformations.map(entitiesLiveData, entities -> {
            if (entities == null) return null;
            return entities.stream()
                    .map(RoutineEntity::toRoutine)
                    .collect(Collectors.toList());
        });
        Subject<List<Routine>> routinesSubject = new Subject<>();
        routinesLiveData.observeForever(new Observer<List<Routine>>() {
            @Override
            public void onChanged(List<Routine> routines) {
                routinesSubject.setValue(routines);
            }
        });

        return routinesSubject;
    }

    @Override
    public void save(Routine routine) {
        var entity = RoutineEntity.fromRoutine(routine);
        routineDao.insertRoutine(entity);
    }

    @Override
    public void addTaskToRoutine(int routineId, @NonNull Task task) {
        var taskEntity = TaskEntity.fromTask(task);
        taskEntity.routineId = routineId;
        taskDao.insert(taskEntity);
    }
    @Override
    public void editTask(EditTaskRequest req) {
        var taskEntity = taskDao.find(req.taskId());
        if (taskEntity == null) return;
        taskEntity.name = req.taskName();
        taskEntity.sortOrder = req.sortOrder();
        taskEntity.taskTime = req.taskTime();
        taskDao.insert(taskEntity);
    }
}


