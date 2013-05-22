package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.util.ninepatch.NinePatchGrid;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;

public class GUINinePatchDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent> {

	public NinePatchGrid mNinePatch;
	public NinePatchGrid mNinePatchPressed;
	public NinePatchGrid mNinePatchDisabled;
	
	public GUINinePatchDrawer setNinePatch(NinePatchGrid ninePatch) {
		mNinePatch = ninePatch;
		if(mNinePatchPressed==null)
			mNinePatchPressed = ninePatch;
		if(mNinePatchDisabled==null)
			mNinePatchDisabled = ninePatch;
		return this;
	}
	
	public GUINinePatchDrawer setNinePatchPressed(NinePatchGrid ninePatch) {
		mNinePatchPressed = ninePatch;
		return this;
	}
	
	public GUINinePatchDrawer setNinePatchDisabled(NinePatchGrid ninePatch) {
		mNinePatchDisabled = ninePatch;
		return this;
	}
	
	@Override
	public void draw(DefaultGraphics<?> graphics, GUIInteractiveRectComponent component) {
		NinePatchGrid uNinePatch;
		if(!component.mEnabled)
			uNinePatch = mNinePatchDisabled;
		else if(component.mPressedTime>0)
			uNinePatch = mNinePatchPressed;
		else
			uNinePatch = mNinePatch;
		if(uNinePatch!=null)
			uNinePatch.draw(graphics.mCurrentVertexBuffer,component.mProjLeft, component.mProjBottom, component.mProjLeft+component.mProjWidth, component.mProjBottom+component.mProjHeight);
	}

}
