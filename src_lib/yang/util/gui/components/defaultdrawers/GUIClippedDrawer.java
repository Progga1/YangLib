package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.util.clippedrect.ClippedDrawerCallback;
import yang.graphics.util.clippedrect.ClippedRect;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;

public class GUIClippedDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent> {

	public ClippedRect mDrawer;
	public ClippedDrawerCallback mCallback;
	
	public GUIClippedDrawer(ClippedDrawerCallback callback) {
		mCallback = callback;
	}
	
	
	
	@Override
	public void draw(DefaultGraphics<?> graphics, GUIInteractiveRectComponent component) {
		if(mDrawer==null) {
			mDrawer = new ClippedRect(graphics.mTranslator,mCallback);
		}
		mDrawer.mBounds.set(component.mProjLeft,component.mProjBottom, component.mProjLeft+component.mProjWidth,component.mProjBottom+component.mProjHeight);
		mDrawer.draw();
	}

	
	
}
