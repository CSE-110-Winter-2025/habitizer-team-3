package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskTimeTest {

    @Test
    public void testTimeTextCheckedOffUnderFiveSeconds() {
        Task task = new Task(1, "TimeTask1",0,3);
        task.setCheckedOff(true);

        String text = getTimeText(task);
        assertEquals("5 s", text);
    }

    @Test
    public void testTimeTextCheckedOffExactlyOneMinute() {
        Task task = new Task(1, "TimeTask2",0,60);
        task.setCheckedOff(true);

        String text = getTimeText(task);
        assertEquals("1 m", text);
    }

    @Test
    public void testTimeTextCheckedOffMoreThanOneMinute() {
        Task task = new Task(1, "TimeTask3",0,121);
        task.setCheckedOff(true);

        String text = getTimeText(task);
        assertEquals("3 m", text);
    }

    private String getTimeText(Task task) {
        String timeText = " ";

        if (task.isCheckedOff() && task.taskTime() != null) {
            if(task.taskTime() < 5){
                timeText = "5 s";
            }
            else if(task.taskTime()== 60){
                timeText = "1 m";
            }
            else{
                timeText = (task.taskTime()/60 + 1) + " m";
            }
        }
        return timeText;
    }
}



