package yang.util.window;

import yang.events.eventtypes.YangEvent;
import yang.graphics.defaults.DefaultGraphics;
import yang.math.objects.Point3f;
import yang.util.NonConcurrentList;

public class YangWindowCollection {

	public NonConcurrentList<YangWindow<?>> mWindows;
	public Point3f mLookAtReference = null;
	public DefaultGraphics<?> mGraphics;

	public YangWindowCollection(DefaultGraphics<?> graphics) {
		mWindows = new NonConcurrentList<YangWindow<?>>();
		mGraphics = graphics;
	}

	public void setLookAtPointReference(Point3f reference) {
		mLookAtReference = reference;
		for(final YangWindow<?> window:mWindows) {
			if(window instanceof YangBillboardWindow) {
				((YangBillboardWindow<?>)window).setLookAtPointReference(reference);
			}
		}
	}

	public Iterable<YangWindow<?>> getWindows() {
		return mWindows;
	}

	public void addWindow(YangWindow<?> window) {
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
		for(final YangWindow<?> window:mWindows) {
			if(!window.mVisible)
				continue;
			if(event.handle(window))
				return true;
		}
		return false;
	}

	public void draw() {
		for(final YangWindow<?> window:mWindows) {
			if(!window.mVisible)
				continue;
			window.draw();
		}

		mGraphics.mTranslator.flush();
		mGraphics.setColorFactor(1);
		mGraphics.setWhite();
		mGraphics.mTranslator.switchZWriting(true);
	}

	public void setDebugAlpha(float alpha) {
		for(final YangWindow<?> window:mWindows) {
			window.mDebugPointsAlpha = alpha;
		}
	}

}
