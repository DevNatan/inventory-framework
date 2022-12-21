package me.saiintbrisson.minecraft;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;
import me.devnatan.inventoryframework.internal.Job;
import me.saiintbrisson.minecraft.test.TestViewFrame;
import org.junit.jupiter.api.Test;

public class UpdateJobTest {

    @Test
    void shouldThrowExceptionWhenStartingInterruptedTask() {
        AtomicBoolean ran = new AtomicBoolean();
        AbstractView view = new AbstractView(0, "Test", ViewType.CHEST) {};

        Job job = createJob(() -> ran.set(true));
        PlatformViewFrame<?, ?, ?> initiator = mock(TestViewFrame.class);
        when(initiator.schedule(any(), anyLong(), anyLong())).thenReturn(job);

        view.setViewFrame(initiator);
        view.scheduleUpdate(0, 0);

        assertNotNull(view.updateSchedule, "Update must be scheduled after #scheduleUpdate call");

        view.initUpdateScheduler();

        Job updateJob = view.getUpdateJob();
        assertNotNull(job, "Update job must be set after schedule update");

        updateJob.start();
        assertTrue(updateJob.isStarted(), "Update job must be started after #start call");
        assertTrue(ran.get(), "Job must be ran at least one time");
    }

    private Job createJob(Runnable loop) {
        return new Job.InternalJobImpl(loop) {
            @Override
            public void start() {
                super.start();
                this.loop();
            }
        };
    }
}
