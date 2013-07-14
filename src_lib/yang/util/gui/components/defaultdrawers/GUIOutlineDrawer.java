package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeCreator;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeDefaultProperties;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeProperties;
import yang.graphics.textures.TextureCoordBounds;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;


public class GUIOutlineDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent>{

	public OrthoStrokeCreator mStroke;
	private float mLstWidth=-1,mLstHeight=-1;
	
	public GUIOutlineDrawer(OrthoStrokeProperties strokeProperties) {
		mStroke = new OrthoStrokeCreator(null,4,strokeProperties);
	}
	
	public GUIOutlineDrawer(TextureCoordBounds texBounds) {
		this(new OrthoStrokeDefaultProperties(texBounds));
	}
	
	public GUIOutlineDrawer() {
		this(new OrthoStrokeDefaultProperties());
	}
	
	@Override
	public void draw(DefaultGraphics<?> graphics, GUIInteractiveRectComponent component) {
		mStroke.setGraphics(graphics);
		if(component.mProjWidth!=mLstWidth || component.mProjHeight!=mLstHeight) {
			mLstWidth = component.mProjWidth;
			mLstHeight = component.mProjHeight;
			mStroke.startStroke(component.mProjLeft, component.mProjBottom);
			mStroke.marchX(mLstWidth);
			mStroke.marchY(mLstHeight);
			mStroke.marchX(-mLstWidth);
			mStroke.marchY(-mLstHeight);
		}
		
		mStroke.drawCompletely();
	}
	
}