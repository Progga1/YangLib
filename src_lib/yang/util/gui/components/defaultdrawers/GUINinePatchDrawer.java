package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.util.ninepatch.NinePatchGrid;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;

public class GUINinePatchDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent> {

	public NinePatchGrid mNinePatch;
	public NinePatchGrid mNinePatchHover;
	public NinePatchGrid mNinePatchPressed;
	public NinePatchGrid mNinePatchDisabled;
	public float mMargin = 0;
	public FloatColor mDefaultColor = FloatColor.WHITE.clone();
	public int mColorIndex = -1;

	public GUINinePatchDrawer setNinePatch(NinePatchGrid ninePatch) {
		mNinePatch = ninePatch;
		if(mNinePatchPressed==null)
			mNinePatchPressed = ninePatch;
		if(mNinePatchDisabled==null)
			mNinePatchDisabled = ninePatch;
		if(mNinePatchHover==null)
			mNinePatchHover = ninePatch;
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

	public GUINinePatchDrawer setNinePatchHover(NinePatchGrid ninePatch) {
		mNinePatchHover = ninePatch;
		return this;
	}

	public GUINinePatchDrawer setMargin(float margin) {
		mMargin = margin;
		return this;
	}

	@Override
	public void draw(DefaultGraphics<?> graphics, GUIInteractiveRectComponent component) {
		NinePatchGrid uNinePatch;
		if(!component.mEnabled)
			uNinePatch = mNinePatchDisabled;
		else if(component.mPressedTime>=0)
			uNinePatch = mNinePatchPressed;
		else if(component.mHoverTime>=0)
			uNinePatch = mNinePatchHover;
		else
			uNinePatch = mNinePatch;
		if(mColorIndex>=0 && component.mColors!=null)
			graphics.setColor(component.mColors[mColorIndex]);
		else
			graphics.setColor(mDefaultColor);
		if(uNinePatch!=null)
			uNinePatch.draw(graphics,component.mProjLeft-mMargin, component.mProjBottom-mMargin, component.mProjLeft+component.mProjWidth+mMargin, component.mProjBottom+component.mProjHeight+mMargin);
	}

}
