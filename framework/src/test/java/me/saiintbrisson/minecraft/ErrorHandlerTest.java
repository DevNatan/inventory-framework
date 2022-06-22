package me.saiintbrisson.minecraft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorHandlerTest {

	@Test
	void shouldCatchException() {
		AbstractView view = new AbstractView(0, null, ViewType.CHEST) {
		};
		view.setErrorHandler(($, exception) -> assertTrue(exception instanceof IllegalStateException));

		assertDoesNotThrow(() -> view.runCatching(null, () -> {
			throw new IllegalStateException();
		}));
	}

	@Test
	void shouldCatchUnhandledException() {
		AbstractView view = new AbstractView(0, null, ViewType.CHEST) {
		};

		try {
			view.runCatching(null, () -> {
				throw new IllegalStateException();
			});
			Assertions.fail();
		} catch (IllegalStateException ignored) {
		}
	}

	@Test
	void shouldContextUnhandledException() {
		AbstractView view = new AbstractView(0, null, ViewType.CHEST) {
		};
		BaseViewContext context = new BaseViewContext(view, null);

		try {
			view.runCatching(context, () -> {
				throw new IllegalStateException();
			});
			Assertions.fail();
		} catch (IllegalStateException ignored) {
		}
	}

	@Test
	void shouldHandleContextException() {
		AbstractView view = new AbstractView(0, null, ViewType.CHEST) {
		};
		BaseViewContext context = new BaseViewContext(view, null);
		context.setErrorHandler(($, exception) -> assertTrue(exception instanceof IllegalStateException));

		assertDoesNotThrow(() -> view.runCatching(context, () -> {
			throw new IllegalStateException();
		}));
	}

	@Test
	void shouldPropagateContextException() {
		AbstractView view = new AbstractView(0, null, ViewType.CHEST) {
		};
		view.setErrorHandler(($, exception) -> assertTrue(exception instanceof IllegalStateException));

		BaseViewContext context = new BaseViewContext(view, null);
		context.setErrorHandler(($, exception) -> assertTrue(exception instanceof IllegalStateException));

		assertDoesNotThrow(() -> view.runCatching(context, () -> {
			throw new IllegalStateException();
		}));
	}

	@Test
	void shouldCancelContextExceptionPropagation() {
		AbstractView view = new AbstractView(0, null, ViewType.CHEST) {
		};
		view.setErrorHandler(($, exception) -> Assertions.fail());

		BaseViewContext context = new BaseViewContext(view, null);
		context.setPropagateErrors(false);
		context.setErrorHandler(($, exception) -> assertTrue(exception instanceof IllegalStateException));

		assertDoesNotThrow(() -> view.runCatching(context, () -> {
			throw new IllegalStateException();
		}));
	}

	// TODO
//	@Test
//	void shouldPropagateContextExceptionToViewFrame() {
//		PlatformViewFrame<?, ?, ?> vf = new TestViewFrame();
//		vf.setErrorHandler(($, exception) -> assertTrue(exception instanceof IllegalStateException));
//
//		AbstractView view = new AbstractView(0, null, ViewType.CHEST) {
//		};
//		view.setViewFrame(vf);
//
//		BaseViewContext context = new BaseViewContext(view, null);
//		assertDoesNotThrow(() -> view.runCatching(context, () -> {
//			throw new IllegalStateException();
//		}));
//	}

}
