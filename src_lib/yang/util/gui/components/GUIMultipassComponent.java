package yang.util.gui.components;

import yang.graphics.model.FloatColor;
import yang.util.Util;


@SuppressWarnings("rawtypes")
public class GUIMultipassComponent extends GUIComponent {

	protected GUIComponentDrawPass[] mPasses;
	public FloatColor[] mColors = null;


	public GUIMultipassComponent setPasses(GUIComponentDrawPass... passes) {
		mPasses = passes;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(int passId) {
		if(mPasses==null || passId>=mPasses.length || mPasses[passId]==null)
			return;
		mPasses[passId].draw(mGUI.mGraphics, this);
	}

	protected void initColors(int count) {
		if(mColors!=null && mColors.length>=count)
			return;
		mColors = new FloatColor[count];
		for(int i=0;i<count;i++) {
			mColors[i] = FloatColor.WHITE.clone();
		}
	}

	public float getProjCenterX() {
		return mProjLeft;
	}

	public float getProjCenterY() {
		return mProjBottom;
	}

	public float getProjWidth() {
		return 0;
	}

	public float getProjHeight() {
		return 0;
	}

	public float getProjZ() {
		return mProjZ;
	}

	@SuppressWarnings("unchecked")
	public <PassType extends GUIComponentDrawPass> PassType getPass(Class<PassType> passClass) {
		for(final GUIComponentDrawPass pass:mPasses)
			if(pass!=null && pass.getClass() == passClass)
				return (PassType)pass;
		return null;
	}

	public GUIMultipassComponent cloneSwallow(boolean ownPassesArray) {
		final GUIMultipassComponent instance = (GUIMultipassComponent)super.cloneSwallow();
		if(ownPassesArray) {
			instance.mPasses = new GUIComponentDrawPass[mPasses.length];
			for(int i=0;i<mPasses.length;i++)
				instance.mPasses[i] = mPasses[i];
		}else
			instance.mPasses = mPasses;
		return instance;
	}

	@Override
	public GUIMultipassComponent cloneSwallow() {
		return cloneSwallow(true);
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder(50);
		result.append(Util.getClassName(this));
		result.append(": ");
		boolean first = true;
		if(mPasses!=null)
			for(final GUIComponentDrawPass pass:mPasses) {
				if(pass!=null) {
					if(!first)
						result.append("; ");
					first = false;
					result.append(pass.toString());
				}
			}
		return result.toString();
	}

}
