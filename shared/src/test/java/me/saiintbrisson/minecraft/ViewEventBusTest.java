package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.event.EventBus;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViewEventBusTest {

	@Test
	void emitKeyed() {
		AbstractView view = new AbstractView() {};
		AtomicBoolean called = new AtomicBoolean();
		view.on("event", $ -> called.set(true));
		view.emit("event", null);
		assertTrue(called.get());
	}

	@Test
	void emitTyped() {
		class MyEvent {}
		AbstractView view = new AbstractView() {};
		AtomicBoolean called = new AtomicBoolean();
		view.on(MyEvent.class, $ -> called.set(true));
		view.emit(new MyEvent());
		assertTrue(called.get());
	}

	@Test
	void emitToAllContexts() {
		class MyEvent {}
		AbstractView view = new AbstractView() {};
		ViewContext context1 = new BaseViewContext(view, null);
		ViewContext context2 = new BaseViewContext(view, null);
		view.registerContext(context1);
		view.registerContext(context2);

		AtomicInteger calls = new AtomicInteger();
		context1.on(MyEvent.class, $ -> calls.incrementAndGet());
		context2.on(MyEvent.class, $ -> calls.incrementAndGet());

		view.emit(new MyEvent());
		assertEquals(view.getContexts().size(), calls.get());
	}

}
