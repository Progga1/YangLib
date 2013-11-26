package yang.util.gui;

import yang.events.YangEventQueue;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.listeners.RawEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.font.DrawableAnchoredLines;
import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.model.callback.Drawable;
import yang.util.YangList;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.GUIContainer2D;
import yang.util.gui.components.GUIInteractiveComponent;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public class BasicGUI implements RawEventListener,Drawable {

	public static GUIPointerEvent[] mGUIEventPool = createEventPool(512);
	public static int componentPoolPos;

	public boolean mAutoUpdateProjections = true;

	private GUICoordinatesMode mCoordinatesMode;

	private boolean mFirstFrame = true;
	public GUIContainer2D mMainContainer;

	public GraphicsTranslator mTranslator;
	public DefaultGraphics<?> mGraphics;
	public float mGUILeft,mGUIBottom,mGUIRight,mGUITop;
	public boolean mDimensionsBySurface = true;
	public float mWidth,mHeight;
	public float mProjShiftX;
	public float mProjShiftY;
	public float mProjWidthFactor,mProjHeightFactor;
	public float mProjShiftYFactor;
	public float mProjXFactor,mProjYFactor;
	public float mCurrentTime = 1;
	public GUIPointerData[] mPointerData = new GUIPointerData[YangEventQueue.MAX_POINTERS];
	public YangList<Texture> mPassTextures;

	protected static GUIPointerEvent[] createEventPool(int capacity) {
		final GUIPointerEvent[] result = new GUIPointerEvent[capacity];
		for(int i=0;i<capacity;i++) {
			result[i] = new GUIPointerEvent();
		}
		componentPoolPos = 0;
		return result;
	}

	public BasicGUI(DefaultGraphics<?> graphics,GUICoordinatesMode coordinatesMode,boolean autoUpdateProjections,int maxPasses) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		mMainContainer = new GUIContainer2D();
		mMainContainer.init(this);
		mAutoUpdateProjections = autoUpdateProjections;
		setCoordinatesMode(coordinatesMode);
		mPassTextures = new YangList<Texture>();
		setPassTexture(0,mTranslator.mWhiteTexture);
		setPassTexture(maxPasses,null);
		for(int i=0;i<YangEventQueue.MAX_POINTERS;i++) {
			mPointerData[i] = new GUIPointerData();
		}
	}

	public BasicGUI(DefaultGraphics<?> graphics,int maxPasses) {
		this(graphics,GUICoordinatesMode.SCREEN,true,maxPasses);
	}

	public BasicGUI(DefaultGraphics<?> graphics) {
		this(graphics,5);
	}

	public void setPassTexture(int pass,Texture texture) {
		while(mPassTextures.size()<=pass)
			mPassTextures.add(null);
		mPassTextures.set(pass,texture);
	}

//	private void addGUILayer() {
//	GUILayer newLayer = new GUILayer();
//	newLayer.mMainContainer = new GUIContainer2D();
//	newLayer.mMainContainer.setGUI(this);
//	mLayers.add(newLayer);
//}

	public GUICoordinatesMode getCoordinatesMode() {
		return mCoordinatesMode;
	}

	private void setCoordinatesMode(GUICoordinatesMode mode) {
		mCoordinatesMode = mode;
		if(mDimensionsBySurface) {
			mWidth = mTranslator.mCurrentSurface.getSurfaceRatioX()*2;
			mHeight = mTranslator.mCurrentSurface.getSurfaceRatioY()*2;
		}
		final float w = mWidth*0.5f;
		final float h = mHeight*0.5f;
		switch(mode) {
		case SCREEN:
			mProjShiftX = -w;
			mProjShiftY = h;
			mProjWidthFactor = 1;
			mProjHeightFactor = 1;
			mProjXFactor = 1;
			mProjYFactor = -1;
			mProjShiftYFactor = -1;
			mGUILeft = 0;
			mGUIRight = w*2;
			mGUITop = 0;
			mGUIBottom = h*2;
			break;
		case NORMALIZED:
			mProjShiftX = 0;
			mProjShiftY = 0;
			mProjWidthFactor = 1;
			mProjHeightFactor = 1;
			mProjXFactor = 1;
			mProjYFactor = 1;
			mProjShiftYFactor = 0;
			mGUILeft = -w;
			mGUIRight = w;
			mGUITop = h;
			mGUIBottom = -h;
			break;
		}

	}

	public float projX(float guiX) {
		return mProjShiftX+guiX*mProjXFactor;
	}

	public float projY(float guiY) {
		return mProjShiftY+guiY*mProjYFactor;
	}

	public void setDefaultActionListener(GUIActionListener actionListener) {
		mMainContainer.mActionListener = actionListener;
	}

	public void setDefaultPointerListener(GUIPointerListener pointerListener) {
		mMainContainer.addPointerListener(pointerListener);
	}

	@Override
	public void draw() {
		mGraphics.setWhite();
		if(mFirstFrame) {
			refreshProjections();
			mFirstFrame = false;
		}else if(mAutoUpdateProjections)
			refreshProjections();
		int pass=0;
		for(final Texture texture:mPassTextures) {
			if(texture!=null)
				mTranslator.bindTexture(texture);
			mMainContainer.draw(pass);
			pass++;
		}
	}

	public void step(float deltaTime) {
		mCurrentTime += deltaTime;
	}

	public GUIComponent handleEvent(YangEvent event) {
		if(event instanceof SurfacePointerEvent) {
			final SurfacePointerEvent pointerEvent = (SurfacePointerEvent)event;

			int index = componentPoolPos++;
			if(componentPoolPos>=mGUIEventPool.length) {
				componentPoolPos = 0;
				index=0;
			}
			final GUIPointerEvent guiEvent = mGUIEventPool[index];
			guiEvent.createFromPointerEvent((SurfacePointerEvent)event, mMainContainer);
			guiEvent.mX = (guiEvent.mX - mProjShiftX)*mProjXFactor;
			guiEvent.mY = (guiEvent.mY - mProjShiftY)*mProjYFactor;
			final int id = pointerEvent.mId;

			final GUIInteractiveComponent pressedComp = mPointerData[id].mPressedComponent;

			if(pointerEvent.mAction==SurfacePointerEvent.ACTION_POINTERDRAG && pressedComp!=null) {
				final GUIPointerEvent dragEvent = BasicGUI.mGUIEventPool[BasicGUI.componentPoolPos++];
				if(BasicGUI.componentPoolPos>BasicGUI.mGUIEventPool.length)
					BasicGUI.componentPoolPos = 0;
				dragEvent.createFromPointerEvent(pointerEvent,pressedComp);
				pressedComp.guiFocusedDrag(dragEvent);
				return mPointerData[id].mPressedComponent;
			}else{

				final GUIComponent result =  mMainContainer.rawPointerEvent(guiEvent);

				if(pressedComp!=null && pointerEvent.mAction==SurfacePointerEvent.ACTION_POINTERUP) {
					pressedComp.mPressedTime = -mCurrentTime;
					mPointerData[id].mPressedComponent = null;
				}

				if(pointerEvent.mAction==SurfacePointerEvent.ACTION_POINTERDRAG || pointerEvent.mAction==SurfacePointerEvent.ACTION_POINTERMOVE) {
					mPointerData[id].mLastMovement = mCurrentTime;
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
		return mWidth;
	}

	public float getGUIHeight() {
		return mHeight;
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

	public void setPressedComponent(int id,GUIInteractiveComponent component) {
		mPointerData[id].mPressedComponent = component;
		if(mCurrentTime!=0)
			mPointerData[id].mPressedComponent.mPressedTime = mCurrentTime;
		else
			mPointerData[id].mPressedComponent.mPressedTime = 1;
	}

	public void setDimensions(float width,float height) {
		mWidth = width;
		mHeight = height;
		mDimensionsBySurface = false;
		refreshCoordinateSystem();
	}

	public void refreshCoordinateSystem() {
		setCoordinatesMode(mCoordinatesMode);
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		return handleEvent(event)!=null;
	}

	public static DrawableString createCaptionString(String string,StringProperties properties,float anchor) {
		return new DrawableAnchoredLines(string).setProperties(properties).setHorizontalAnchor(anchor).setConstant();
	}

	public static DrawableString createCaptionString(String string,StringProperties properties) {
		return createCaptionString(string,properties,DrawableString.DEFAULT_HORIZONTAL_ANCHOR);
	}

	public static DrawableString[] createCaptionStringArray(String[] strings,StringProperties properties) {
		DrawableString[] result = new DrawableString[strings.length];
		for(int i=0;i<result.length;i++) {
			result[i] = createCaptionString(strings[i],properties);
		}
		return result;
	}

}
