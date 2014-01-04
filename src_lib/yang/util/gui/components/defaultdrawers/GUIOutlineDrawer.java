package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.geometrycreators.outlinedrawer.OrthoStrokeCreator;
import yang.graphics.defaults.geometrycreators.outlinedrawer.OrthoStrokeDefaultProperties;
import yang.graphics.defaults.geometrycreators.outlinedrawer.OrthoStrokeProperties;
import yang.graphics.textures.TextureCoordBounds;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;


public class GUIOutlineDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent>{

	public OrthoStrokeCreator mStroke;
	private float mLstWidth=-1,mLstHeight=-1;
	public float mMargin = 0;
	
	public GUIOutlineDrawer(OrthoStrokeProperties strokeProperties) {
		mStroke = new OrthoStrokeCreator(null,4,strokeProperties);
	}
	
	public GUIOutlineDrawer(TextureCoordBounds texBounds) {
		this(new OrthoStrokeDefaultProperties(texBounds));
	}
	
	public GUIOutlineDrawer() {
		this(new OrthoStrokeDefaultProperties());
	}
	
	public GUIOutlineDrawer setMargin(float margin) {
		mMargin = margin;
		return this;
	}
	
	public void refreshStroke(GUIInteractiveRectComponent component) {
		mLstWidth = component.mProjWidth;
		mLstHeight = component.mProjHeight;
		mStroke.startStroke(component.mProjLeft-mMargin, component.mProjBottom-mMargin);
		mStroke.marchX(mLstWidth+2*mMargin);
		mStroke.marchY(mLstHeight+2*mMargin);
		mStroke.marchX(-mLstWidth+2*mMargin);
		mStroke.marchY(-mLstHeight+2*mMargin);
	}
	
	@Override
	public void draw(DefaultGraphics<?> graphics, GUIInteractiveRectComponent component) {
		mStroke.setGraphics(graphics);
		if(component.mProjWidth!=mLstWidth || component.mProjHeight!=mLstHeight) {
			refreshStroke(component);
		}
		
		mStroke.drawCompletely();
	}
	
}
