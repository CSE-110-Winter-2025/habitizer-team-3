package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

public class RoutineBuilderTest {

    @Test
    public void setId() {
        RoutineBuilder builder = new RoutineBuilder();
        int expectedId = 123;
        Routine routine = builder.setId(expectedId).build();
        assertEquals("Id should be set", (Integer) expectedId, routine.id());
    }

    @Test
    public void setName() {
        RoutineBuilder builder = new RoutineBuilder();
        String expectedName = "Custom Routine";
        Routine routine = builder.setName(expectedName).build();
        assertEquals("Name should be set", expectedName, routine.name());
    }

    @Test
    public void setTaskList() {
        // Create a new, empty TaskList (or one with specific tasks if desired)
        TaskList expectedTaskList = new TaskList(new ArrayList<>());
        Routine routine = new RoutineBuilder().setTaskList(expectedTaskList).build();
        assertEquals("TaskList should be set", expectedTaskList, routine.taskList());
    }

    @Test
    public void setTime() {
        RoutineBuilder builder = new RoutineBuilder();
        int expectedTime = 45;
        Routine routine = builder.setTime(expectedTime).build();
        assertEquals("Time should be set", (Integer) expectedTime, routine.time());
    }

    @Test
    public void build() {
        // Test default values from the builder
        Routine routine = new RoutineBuilder().build();
        assertEquals("Default name should be 'New Routine'", "New Routine", routine.name());
        assertNotNull("Default TaskList should not be null", routine.taskList());
        assertEquals("Default time should be 30", (Integer) 30, routine.time());
    }
}
