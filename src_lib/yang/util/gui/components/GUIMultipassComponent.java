package yang.util.gui.components;


@SuppressWarnings("rawtypes")
public class GUIMultipassComponent extends GUIComponent {

	protected GUIComponentDrawPass[] mPasses;
	
//	public GUIMultipassComponent setPasses(GUIComponentDrawPass[] passes) {
//		mPasses = passes;
//		return this;
//	}
	
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
