package yang.util.window;

import yang.events.eventtypes.YangEvent;
import yang.graphics.defaults.DefaultGraphics;
import yang.math.objects.Point3f;
import yang.util.YangList;

public class YangWindowCollection<WindowType extends YangWindow<?>> {

	public YangList<WindowType> mWindows;
	public Point3f mLookAtReference = null;
	public DefaultGraphics<?> mGraphics;
	protected boolean mInteracting = false;

	public YangWindowCollection(DefaultGraphics<?> graphics) {
		mWindows = new YangList<WindowType>();
		mGraphics = graphics;
	}

	public void setLookAtPointReference(Point3f reference) {
		mLookAtReference = reference;
		for(final WindowType window:mWindows) {
			if(window instanceof YangBillboardWindow) {
				((YangBillboardWindow<?>)window).setLookAtPointReference(reference);
			}
		}
	}

	public Iterable<WindowType> getWindows() {
		return mWindows;
	}

	public void addWindow(WindowType window) {
		mWindows.add(window);
		if(mLookAtReference!=null && (window instanceof YangBillboardWindow)) {
			((YangBillboardWindow<?>)window).setLookAtPointReference(mLookAtReference);
		}
	}

	public void removeWindow(YangWindow<?> window) {
		mWindows.remove(window);
	}

	public void step(float deltaTime) {
		for(final YangWindow<?> window:mWindows) {
			window.step(deltaTime);
		}
	}

	public boolean handleEvent(YangEvent event) {
		mInteracting = false;
		for(final YangWindow<?> window:mWindows) {
			if(!window.mVisible)
				continue;
			if(event.handle(window)) {
				mInteracting = true;
			}
		}
		return mInteracting;
	}

	public void draw(int drawPass,boolean solid) {
		for(final WindowType window:mWindows) {
			if(!window.mVisible || solid^window.mSolid)
				continue;
			window.draw(drawPass);
		}

		mGraphics.mTranslator.flush();
		mGraphics.setColorFactor(1);
		mGraphics.setWhite();
		mGraphics.mTranslator.switchZWriting(true);
	}

	public void draw(boolean solid) {
		draw(YangWindow.PASS_BACKGROUND,solid);
		draw(YangWindow.PASS_MAIN,solid);
		draw(YangWindow.PASS_DEBUG,solid);
	}

	public void setDebugAlpha(float alpha) {
		for(final YangWindow<?> window:mWindows) {
			window.mDebugPointsAlpha = alpha;
		}
	}

	public void setDrawDebug(boolean drawDebug) {
		for(final YangWindow<?> window:mWindows) {
			window.mDrawDebugPoints = drawDebug;
		}
	}

	public boolean isInteracting() {
		return mInteracting;
	}

}
