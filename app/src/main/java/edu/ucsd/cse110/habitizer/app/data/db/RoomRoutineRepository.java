package edu.ucsd.cse110.habitizer.app.data.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.app.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.habitizer.lib.domain.DeleteTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.EditRoutineRequest;
import edu.ucsd.cse110.habitizer.lib.domain.EditTaskRequest;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskList;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class RoomRoutineRepository implements RoutineRepository {
    private static final String TAG = "RoomRoutineRepository";
    private final RoutinesDao routinesDao;
    private final TasksDao taskDao;

    public RoomRoutineRepository(RoutinesDao routinesDao, TasksDao taskDao) {
        this.routinesDao = routinesDao;
        this.taskDao = taskDao;
    }

    @Override
    public Integer count() {
        return routinesDao.count();
    }

    @Override
    public Subject<Routine> find(int id) {
        LiveData<RoutineEntity> entityLiveData = routinesDao.findAsLiveData(id);
        LiveData<Routine> routineLiveData = Transformations.map(entityLiveData, this::convertEntityToDomainWithTasks);
        return new LiveDataSubjectAdapter<>(routineLiveData);
    }

    @Override
    public Subject<List<Routine>> findAll() {
        var entitiesLiveData = routinesDao.findAllAsLiveData();
        var routinesLiveData = Transformations.map(entitiesLiveData, entities -> {
            Log.d(TAG, "Transforming " + entities.size() + " routine entities to domain objects");

            return entities.stream()
                    .map(this::convertEntityToDomainWithTasks)
                    .collect(Collectors.toList());
        });

        return new LiveDataSubjectAdapter<>(routinesLiveData);
    }

    private Routine convertEntityToDomainWithTasks(RoutineEntity entity) {
        // Get tasks for this routine
        List<TaskEntity> taskEntities = taskDao.findByRoutineId(entity.id);

        Log.d(TAG, "Found " + (taskEntities != null ? taskEntities.size() : 0) +
                " tasks for routine " + entity.id + " (" + entity.name + ")");

        List<Task> tasks = new ArrayList<>();

        if (taskEntities != null && !taskEntities.isEmpty()) {
            // Convert task entities to domain tasks
            tasks = taskEntities.stream()
                    .map(taskEntity -> {
                        Task task = taskEntity.toDomain();
                        Log.d(TAG, "  Task: " + task.name() + " (ID: " + task.id() +
                                ", Sort Order: " + task.sortOrder() + ")");
                        return task;
                    })
                    .collect(Collectors.toList());
        } else {
            Log.w(TAG, "No tasks found for routine " + entity.id + " (" + entity.name + ")");
        }

        // Create a TaskList with these tasks
        TaskList taskList = new TaskList(tasks);

        // Create and return a complete Routine with tasks
        return new Routine(entity.id, entity.name, taskList, entity.time);
    }

    @Override
    public void save(Routine routine) {
        // Convert domain Routine -> RoutineEntity, store in DB
        Log.d(TAG, "Saving routine: " + routine.id() + " (" + routine.name() + ")");
        routinesDao.insert(RoutineEntity.fromRoutine(routine));

        // If the routine has tasks, make sure those are saved/updated too
        if (routine.taskList() != null && routine.taskList().tasks() != null) {
            Log.d(TAG, "  Routine has " + routine.taskList().tasks().size() + " tasks");

            // For simplicity, we could delete and re-insert all tasks
            // This is a bit heavy-handed but ensures consistency
            for (Task task : routine.taskList().tasks()) {
                addTaskToRoutine(routine.id(), task);
            }
        }
    }

    @Override
    public void addTaskToRoutine(int routineId, @NonNull Task task) {
        Log.d("RoomRoutineRepository", "Adding task to routine. RoutineID: " + routineId +
                ", TaskID: " + task.id() + ", Name: " + task.name());

        // Build a TaskEntity from the domain Task
        var entity = TaskEntity.fromDomain(task, routineId);

        Log.d("RoomRoutineRepository", "Inserting TaskEntity. ID: " + entity.id +
                ", RoutineID: " + entity.routineId);

        int result = taskDao.append(entity);

        Log.d("RoomRoutineRepository", "Insert result: " + result);
    }

    @Override
    public void editTask(EditTaskRequest req) {
        // Find the existing TaskEntity by ID
        var oldEntity = taskDao.findById(req.taskId());
        if (oldEntity == null) {
            Log.e(TAG, "Task with ID " + req.taskId() + " not found");
            return;
        }

        Log.d(TAG, "Editing task: " + oldEntity.id + " (" + oldEntity.name + " -> " + req.taskName() + ")");

        // Update fields
        oldEntity.name = req.taskName();
        oldEntity.sortOrder = req.sortOrder();
        oldEntity.taskTime = req.taskTime();

        // Then update in DB
        taskDao.update(oldEntity);
    }

    @Override
    public void deleteTask(DeleteTaskRequest req) {
        taskDao.delete(req.taskId());
    }

    @Override
    public void editRoutineName(EditRoutineRequest req) {
        var oldEntity = routinesDao.find(req.routineId());
        if (oldEntity == null) {
            Log.e(TAG, "Routine with ID " + req.routineId() + " not found");
            return;
        }

        Log.d(TAG, "Editing routine: " + oldEntity.id + " (" + oldEntity.name + " -> " + req.routineName() + ")");

        oldEntity.name = req.routineName();
        routinesDao.update(oldEntity);
    }
}