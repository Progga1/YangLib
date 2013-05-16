package yang.util.gui;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.util.NonConcurrentList;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.GUIContainer2D;
import yang.util.gui.components.GUIInteractiveComponent;
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
	public float mGUILeft,mGUIBottom,mGUIRight,mGUITop;
	public float mProjShiftX;
	public float mProjShiftY;
	public float mProjWidthFactor,mProjHeightFactor;
	public float mProjShiftYFactor;
	public float mProjXFactor,mProjYFactor;
	public float mCurrentTime;
	public GUIInteractiveComponent mPressedComponent;
	public NonConcurrentList<Texture> mPassTextures;
	
	protected static GUIPointerEvent[] createEventPool(int capacity) {
		GUIPointerEvent[] result = new GUIPointerEvent[capacity];
		for(int i=0;i<capacity;i++) {
			result[i] = new GUIPointerEvent();
		}
		mComponentPoolPos = 0;
		return result;
	}
	
	public BasicGUI(Default2DGraphics graphics2D,GUICoordinatesMode coordinatesMode,boolean autoUpdateProjections,int maxPasses) {
		mGraphics2D = graphics2D;
		mGraphics = graphics2D.mTranslator;
		mMainContainer = new GUIContainer2D();
		mMainContainer.setGUI(this);
		mAutoUpdateProjections = autoUpdateProjections;
		setCoordinatesMode(coordinatesMode);
		mPassTextures = new NonConcurrentList<Texture>();
		setPassTexture(0,null);
		setPassTexture(maxPasses,null);
	}
	
	public void setPassTexture(int pass,Texture texture) {
		while(mPassTextures.size()<=pass)
			mPassTextures.add(null);
		mPassTextures.set(pass,texture);
	}
	
	public BasicGUI(Default2DGraphics graphics2D,int maxPasses) {
		this(graphics2D,GUICoordinatesMode.SCREEN,true,maxPasses);
	}
	
	public GUICoordinatesMode getCoordinatesMode() {
		return mCoordinatesMode;
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
			mGUILeft = 0;
			mGUIRight = mGraphics.mRatioX*2;
			mGUITop = 0;
			mGUIBottom = mGraphics.mRatioY*2;
			break;
		case NORMALIZED:
			mProjShiftX = 0;
			mProjShiftY = 0;
			mProjWidthFactor = 1;
			mProjHeightFactor = 1;
			mProjXFactor = 1;
			mProjYFactor = 1;
			mProjShiftYFactor = 0;
			mGUILeft = -mGraphics.mRatioX;
			mGUIRight = mGraphics.mRatioX;
			mGUITop = mGraphics.mRatioY;
			mGUIBottom = -mGraphics.mRatioY;
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
		int pass=0;
		for(Texture texture:mPassTextures) {
			mGraphics.bindTexture(texture);
			mMainContainer.draw(pass);
			pass++;
		}
	}
	
	public void step(float deltaTime) {
		mCurrentTime += deltaTime;
	}
	
	public GUIComponent handleEvent(YangEvent event) {
		boolean handled = false;
		if(event instanceof YangPointerEvent) {
			YangPointerEvent pointerEvent = (YangPointerEvent)event;
			
			int index = mComponentPoolPos++;
			if(mComponentPoolPos>=mGUIEventPool.length) {
				mComponentPoolPos = 0;
				index=0;
			}
			GUIPointerEvent guiEvent = mGUIEventPool[index];
			guiEvent.createFromPointerEvent((YangPointerEvent)event, mMainContainer);
			guiEvent.mX = (guiEvent.mX - mProjShiftX)*mProjXFactor;
			guiEvent.mY = (guiEvent.mY - mProjShiftY)*mProjYFactor;
			
			if(pointerEvent.mAction==YangPointerEvent.ACTION_POINTERDRAG && mPressedComponent!=null) {
				GUIPointerEvent dragEvent = BasicGUI.mGUIEventPool[BasicGUI.mComponentPoolPos++];
				if(BasicGUI.mComponentPoolPos>BasicGUI.mGUIEventPool.length)
					BasicGUI.mComponentPoolPos = 0;
				dragEvent.createFromPointerEvent(pointerEvent,mPressedComponent);
				mPressedComponent.guiFocusedDrag(dragEvent);
				return mPressedComponent;
			}else{
			
				GUIComponent result =  mMainContainer.rawPointerEvent(guiEvent);
				
				if(mPressedComponent!=null && pointerEvent.mAction==YangPointerEvent.ACTION_POINTERUP) {
					mPressedComponent.mPressedTime = -mCurrentTime;
					mPressedComponent = null;
				}
						
				return result;
			}
			

		}
		return null;
	}
	
	public float getGUILeft() {
		return mGUILeft;
	}
	
	public float getGUITop() {
		return mGUITop;
	}
	
	public float getGUIRight() {
		return mGUIRight;
	}
	
	public float getGUIBottom() {
		return mGUIBottom;
	}
	
	public float getGUICenterX() {
		return (mGUILeft+mGUIRight)*0.5f;
	}
	
	public float getGUICenterY() {
		return (mGUITop+mGUIBottom)*0.5f;
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
	
	public <ComponentType extends GUIComponent> ComponentType addComponent(Class<ComponentType> component) {
		return mMainContainer.addComponent(component);
	}
	
	public void setGlobalShift(float shiftX,float shiftY) {
		mMainContainer.mPosX = shiftX;
		mMainContainer.mPosY = shiftY;
	}

	public float normToGUIX(float x) {
		return (x - mProjShiftX)*mProjXFactor;
	}
	
	public float normToGUIY(float y) {
		return (y - mProjShiftY)*mProjYFactor;
	}

	public void setPressedComponent(GUIInteractiveComponent component) {
		mPressedComponent = component;
		mPressedComponent.mPressedTime = mCurrentTime;
	}
	
}
