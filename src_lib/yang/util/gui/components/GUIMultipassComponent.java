package yang.util.gui.components;

import yang.graphics.model.FloatColor;


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
		mPasses[passId].draw(mGUI.mGraphics2D, this);
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
	
	@SuppressWarnings("unchecked")
	public <PassType extends GUIComponentDrawPass> PassType getPass(Class<PassType> passClass) {
		for(GUIComponentDrawPass pass:mPasses)
			if(pass!=null && pass.getClass() == passClass)
				return (PassType)pass;
		return null;
	}
	
	public GUIMultipassComponent cloneSwallow(boolean ownPassesArray) {
		GUIMultipassComponent instance = (GUIMultipassComponent)super.cloneSwallow();
		if(ownPassesArray) {
			instance.mPasses = new GUIComponentDrawPass[mPasses.length];
			for(int i=0;i<mPasses.length;i++)
				instance.mPasses[i] = mPasses[i];
		}else
			instance.mPasses = mPasses;
		return instance;
	}
	
	public GUIMultipassComponent cloneSwallow() {
		return cloneSwallow(true);
	}
	
}
