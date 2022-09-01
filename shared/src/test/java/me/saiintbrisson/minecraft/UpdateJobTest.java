package me.saiintbrisson.minecraft;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;
import me.saiintbrisson.minecraft.test.TestViewFrame;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class UpdateJobTest {

    @Test
    void shouldThrowExceptionWhenStartingInterruptedTask() {
        AtomicBoolean ran = new AtomicBoolean();
        AbstractView view = new AbstractView(0, "Test", ViewType.CHEST) {
            @Override
            protected void onUpdate(@NotNull ViewContext context) {
                System.out.println("updated");
            }
        };

        Job job = createJob();
        Runnable runnable = () -> System.out.println("runnable exec");
        PlatformViewFrame<?, ?, ?> initiator = mock(TestViewFrame.class);
        when(initiator.schedule(eq(runnable), anyLong(), anyLong())).thenReturn(job);

        System.out.println("job = " + job);
        view.setViewFrame(initiator);
        view.scheduleUpdate(0, 0);
        assertNotNull(view.getUpdateJob(), "Update job must be set after schedule update");

        Job updateJob = view.getUpdateJob();
        updateJob.start();
        assertTrue(updateJob.isStarted(), "Update job must be started after #start call");

        System.out.println("view.getUpdateJob() = " + view.getUpdateJob());
        assertTrue(ran.get(), "Job must be ran at least one time");
    }

    private Job createJob() {
        return new Job() {
            boolean started;

            @Override
            public boolean isStarted() {
                return started;
            }

            @Override
            public void start() {
                started = true;
            }

            @Override
            public void cancel() {
                started = false;
            }
        };
    }
}
