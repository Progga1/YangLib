package yang.util.gui;

import yang.events.eventtypes.PointerEvent;
import yang.events.eventtypes.YangInputEvent;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.GUIContainer2D;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public class BasicGUI {

	public static GUIPointerEvent[] mGUIEventPool = createEventPool(512);
	public static int mComponentPoolPos;
	
	public boolean mAutoUpdateProjections = true;
	
	private GUICoordinatesMode mCoordinatesMode;
	
	private boolean mFirstFrame = true;
	public GUIContainer2D mMainContainer;
	
	public GraphicsTranslator mGraphics;
	public Default2DGraphics mGraphics2D;
	public float mProjShiftX;
	public float mProjShiftY;
	public float mProjWidthFactor,mProjHeightFactor;
	public float mProjShiftYFactor;
	public float mProjXFactor,mProjYFactor;
	
	protected static GUIPointerEvent[] createEventPool(int capacity) {
		GUIPointerEvent[] result = new GUIPointerEvent[capacity];
		for(int i=0;i<capacity;i++) {
			result[i] = new GUIPointerEvent();
		}
		mComponentPoolPos = 0;
		return result;
	}
	
	public BasicGUI(Default2DGraphics graphics2D,GUICoordinatesMode coordinatesMode,boolean autoUpdateProjections) {
		mGraphics2D = graphics2D;
		mGraphics = graphics2D.mTranslator;
		mMainContainer = new GUIContainer2D();
		mMainContainer.setGUI(this);
		mAutoUpdateProjections = autoUpdateProjections;
		setCoordinatesMode(coordinatesMode);
	}
	
	public BasicGUI(Default2DGraphics graphics2D) {
		this(graphics2D,GUICoordinatesMode.SCREEN,true);
	}
	
	private void setCoordinatesMode(GUICoordinatesMode mode) {
		mCoordinatesMode = mode;
		switch(mode) {
		case SCREEN:
			mProjShiftX = -mGraphics.mRatioX;
			mProjShiftY = mGraphics.mRatioY;
			mProjWidthFactor = 1;
			mProjHeightFactor = 1;
			mProjXFactor = 1;
			mProjYFactor = -1;
			mProjShiftYFactor = -1;
			break;
		case NORMALIZED:
			mProjShiftX = 0;
			mProjShiftY = 0;
			mProjWidthFactor = 1;
			mProjHeightFactor = 1;
			mProjXFactor = 1;
			mProjYFactor = 1;
			mProjShiftYFactor = 0;
			break;
		}
		
	}

	public void setDefaultActionListener(GUIActionListener actionListener) {
		mMainContainer.mActionListener = actionListener;
	}
	
	public void setDefaultPointerListener(GUIPointerListener pointerListener) {
		mMainContainer.mPointerListener = pointerListener;
	}
	
	public void draw() {
		if(mFirstFrame) {
			refreshProjections();
			mFirstFrame = false;
		}else if(mAutoUpdateProjections)
			refreshProjections();
		mGraphics2D.mTranslator.switchZBuffer(false);
		mGraphics2D.mTranslator.bindTexture(null);
		mMainContainer.draw();
	}
	
	public boolean handleEvent(YangInputEvent event) {
		boolean handled = false;
		if(event instanceof PointerEvent) {
			GUIPointerEvent guiEvent = mGUIEventPool[mComponentPoolPos++];
			if(mComponentPoolPos>=mGUIEventPool.length)
				mComponentPoolPos = 0;
			guiEvent.createFromPointerEvent((PointerEvent)event, mMainContainer);
			guiEvent.mX = guiEvent.mX*mProjXFactor + mProjShiftX;
			guiEvent.mY = guiEvent.mY*mProjYFactor + mProjShiftY;
			mMainContainer.rawPointerEvent(guiEvent);
			handled = true;
		}
		return handled;
	}
	
	public float getGUILeft() {
		return 0;
	}
	
	public float getGUITop() {
		return 0;
	}
	
	public float getGUIRight() {
		return -mProjShiftX*2;
	}
	
	public float getGUIBottom() {
		return mProjShiftY*2;
	}
	
	public float getGUICenterX() {
		return -mProjShiftX;
	}
	
	public float getGUICenterY() {
		return mProjShiftY;
	}
	
	public float getGUIWidth() {
		return mGraphics.mRatioX*2;
	}
	
	public float getGUIHeight() {
		return mGraphics.mRatioY*2;
	}
	
	public void refreshProjections() {
		mMainContainer.refreshProjections(0,0);
	}

	public <ComponentType extends GUIComponent> ComponentType addComponent(ComponentType component) {
		return mMainContainer.addComponent(component);
	}
	
	public void setGlobalShift(float shiftX,float shiftY) {
		mMainContainer.mPosX = shiftX;
		mMainContainer.mPosY = shiftY;
	}
	
}
