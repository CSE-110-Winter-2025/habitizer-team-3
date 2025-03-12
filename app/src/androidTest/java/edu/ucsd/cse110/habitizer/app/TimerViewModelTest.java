package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TimerViewModelTest {

    @Test
    public void testStartTimer() throws InterruptedException {
        // Create the ViewModel
        TimerViewModel viewModel = new TimerViewModel();

        // Initially, should be 0
        assertEquals("Initial elapsed time should be 0",
                0, (int) viewModel.getElapsedSeconds().getValue());

        // Start the timer
        viewModel.startTimer();

        // Wait at least 1 second for the timer to increment
        Thread.sleep(1500);

        assertTrue("Elapsed time should be >= 1 after ~1.5s",
                viewModel.getElapsedSeconds().getValue() >= 1);

        viewModel.stopTimer();
    }

    @Test
    public void testStopTimer() throws InterruptedException {
        TimerViewModel viewModel = new TimerViewModel();
        viewModel.startTimer();
        Thread.sleep(1500);

        int beforeStop = viewModel.getElapsedSeconds().getValue();
        viewModel.stopTimer();
        Thread.sleep(1500);

        // After stopping the elapsed time should not increase
        int afterStop = viewModel.getElapsedSeconds().getValue();
        assertEquals("Elapsed time should not increase after stopTimer()",
                beforeStop, afterStop);
    }

    @Test
    public void testForwardTimer() throws InterruptedException {
        TimerViewModel viewModel = new TimerViewModel();
        assertEquals(0, (int) viewModel.getElapsedSeconds().getValue());

        viewModel.stopTimer();
        viewModel.forwardTimer();
        Thread.sleep(500);

        assertEquals(15, (int) viewModel.getElapsedSeconds().getValue());
    }

    @Test
    public void testGetElapsedSeconds() throws InterruptedException {
        TimerViewModel viewModel = new TimerViewModel();
        assertEquals(0, (int) viewModel.getElapsedSeconds().getValue());

        // Start timer, wait
        viewModel.startTimer();
        Thread.sleep(1500);

        // Check if incremented
        int elapsed = viewModel.getElapsedSeconds().getValue();
        assertTrue("Elapsed time should be >= 1 after ~1.5s", elapsed >= 1);

        viewModel.stopTimer();
    }

    @Test
    public void testMultipleStartTimer() throws InterruptedException {
        TimerViewModel viewModel = new TimerViewModel();
        viewModel.startTimer();
        Thread.sleep(1500);
        int firstValue = viewModel.getElapsedSeconds().getValue();
        // Calling startTimer() again should not reset the timer
        viewModel.startTimer();
        Thread.sleep(1500);
        int secondValue = viewModel.getElapsedSeconds().getValue();
        // The timer should continue running and not restart from 0.
        assertTrue("Timer should continue after calling startTimer() twice",
                secondValue >= firstValue + 1);
        viewModel.stopTimer();
    }

    @Test
    public void testStopTimerWithoutStart() {
        TimerViewModel viewModel = new TimerViewModel();
        // Calling stopTimer() without starting should leave elapsed time at 0.
        viewModel.stopTimer();
        assertEquals("Elapsed time should remain 0 if timer is never started",
                0, (int) viewModel.getElapsedSeconds().getValue());
    }

    @Test
    public void testMultipleStopTimer() throws InterruptedException {
        TimerViewModel viewModel = new TimerViewModel();
        viewModel.startTimer();
        Thread.sleep(1500);
        viewModel.stopTimer();
        int stoppedValue = viewModel.getElapsedSeconds().getValue();
        // Calling stopTimer() a second time should not change anything.
        viewModel.stopTimer();
        Thread.sleep(1500);
        int valueAfterMultipleStops = viewModel.getElapsedSeconds().getValue();
        assertEquals("Elapsed time should not change after multiple stopTimer() calls",
                stoppedValue, valueAfterMultipleStops);
    }

    @Test
    public void testMultipleForwardTimer() throws InterruptedException {
        TimerViewModel viewModel = new TimerViewModel();

        // ForwardTimer only works when isMocking = true
        viewModel.stopTimer();

        // Call forwardTimer() twice consecutively
        viewModel.forwardTimer();
        Thread.sleep(500);
        viewModel.forwardTimer();
        Thread.sleep(500);

        // Expected value = 15 + 15 = 30
        assertEquals("Elapsed time should be 30 after calling forwardTimer() twice",
                30, (int) viewModel.getElapsedSeconds().getValue());
    }

    @Test
    public void testForwardTimerAfterStop() throws InterruptedException {
        TimerViewModel viewModel = new TimerViewModel();

        viewModel.startTimer();
        Thread.sleep(1500);
        viewModel.stopTimer();

        int stoppedValue = viewModel.getElapsedSeconds().getValue();

        // After stopping, forwardTimer should still increment by 15 seconds
        viewModel.forwardTimer();
        Thread.sleep(500);

        assertEquals("Elapsed time should increment by 15 after forwardTimer() when timer is stopped",
                stoppedValue + 15, (int) viewModel.getElapsedSeconds().getValue());
    }

}
