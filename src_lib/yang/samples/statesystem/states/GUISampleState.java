package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.font.DrawableString;
import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.Texture;
import yang.graphics.util.clippedrect.ClippedDrawerCallback;
import yang.graphics.util.ninepatch.NinePatchGrid;
import yang.graphics.util.ninepatch.NinePatchTexCoords;
import yang.math.objects.Bounds;
import yang.samples.statesystem.SampleState;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.GUIInteractiveRectComponent;
import yang.util.gui.components.defaultbuttons.DefaultIconButton;
import yang.util.gui.components.defaultbuttons.DefaultNinePatchButton;
import yang.util.gui.components.defaultbuttons.DefaultOutlineButton;
import yang.util.gui.components.defaultbuttons.GUIButton;
import yang.util.gui.components.defaultdrawers.GUIClippedDrawer;
import yang.util.gui.components.defaultdrawers.GUIIconDrawer;
import yang.util.gui.components.defaultdrawers.GUINinePatchDrawer;
import yang.util.gui.components.defaultdrawers.GUIOutlineDrawer;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;
import yang.util.gui.components.defaults.GUIColoredPanel;
import yang.util.gui.components.defaults.GUILabel;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public class GUISampleState extends SampleState implements GUIActionListener,GUIPointerListener {

	private BasicGUI mGUI;
	private GUIColoredPanel mPanel;
	private GUIButton mToggleButton;
	private GUIColoredPanel mInnerPanel;
	private DefaultNinePatchButton mNinePatchButton1;
	private DefaultNinePatchButton mNinePatchButton2;
	private DefaultIconButton mIconButton;
	private DefaultOutlineButton mOutlineButton;

	private class RectDrawer implements ClippedDrawerCallback {

		DefaultGraphics<?> mGraphics;

		public RectDrawer(DefaultGraphics<?> graphics) {
			mGraphics = graphics;
		}

		@Override
		public void draw(Bounds bounds, float shiftX, float shiftY) {
			mGraphics.mTranslator.bindTexture(null);
			mGraphics.setColor(1,0.2f,0);
			mGraphics.drawRectWH(bounds.getLeft()+shiftX+0.02f,bounds.getBottom()+shiftY+0.02f,0.5f,0.6f,null);
		}

	}

	@Override
	protected void initGraphics() {
		mGraphics2D.activate();
		mGUI = new BasicGUI(mGraphics2D,4);
		mGUI.setDefaultActionListener(this);
		mGUI.setDefaultPointerListener(this);
		mPanel = mGUI.addComponent(GUIColoredPanel.class);
		mPanel.getPass(GUIRectDrawer.class).mColor.set(FloatColor.WHITE);
		mPanel.setPosAndExtents(0.1f, 0.3f, 0.9f, 1.2f);

		final DrawableString caption = new DrawableString("Label").setAnchors(DrawableString.ANCHOR_LEFT,DrawableString.ANCHOR_TOP);
		mPanel.addComponent(new GUILabel().setCaption(caption).setPosition(0.1f, 0.1f));

		mToggleButton = mPanel.addComponent(new GUIButton());
		mToggleButton.createCaption("Button1").setPosAndExtents(0.1f, 0.3f, mPanel.mWidth-0.2f, 0.16f);
		mToggleButton.getPass(GUIRectDrawer.class).mColor.set(1, 0.5f, 0);
		mToggleButton.getPass(GUIRectDrawer.class).setBorderSize(0.014f).mBorderColor.set(0.8f, 0.3f, 0);

		GUIRectDrawer.DEFAULT_BORDERSIZE = 0.008f;
		mInnerPanel = mPanel.addComponent(new GUIColoredPanel());
		mInnerPanel.getPass(GUIRectDrawer.class).mColor.set(0.8f);
		mInnerPanel.setPosAndExtents(0.1f, 0.5f, mPanel.mWidth-0.2f, 0.5f);
		GUIRectDrawer.DEFAULT_BORDERSIZE = 0.01f;
		mInnerPanel.addComponent(new GUIButton().createCaption("Inner1").setPosAndExtentsCentered(mInnerPanel.mWidth/2, 0.1f, mInnerPanel.mWidth-0.12f, 0.14f));
		mInnerPanel.addComponent(new GUIButton().createCaption("Inner2").setPosAndExtentsCentered(mInnerPanel.mWidth/2, 0.25f, mInnerPanel.mWidth-0.12f, 0.14f));
		mInnerPanel.addComponent(new GUIButton().createCaption("Inner3").setPosAndExtentsCentered(mInnerPanel.mWidth/2, 0.4f, mInnerPanel.mWidth-0.12f, 0.14f));
		GUIRectDrawer.DEFAULT_BORDERSIZE = 0;

		//Nine patch buttons
		final Texture ninePatchTex = mGFXLoader.getImage("button");
		final NinePatchTexCoords ninePatchTexCoords = new NinePatchTexCoords().init(0,0,0.5f,0.5f, 0.005f,0.005f).setBorder(3f/ninePatchTex.mWidth);
		final NinePatchGrid ninePatch = new NinePatchGrid().setBorderSize(0.01f).setTextureBorder(ninePatchTexCoords);
		final NinePatchGrid ninePatchPressed = ninePatch.cloneWithTextureOffset(0.5f, 0);

		mNinePatchButton1 = mGUI.addComponent(DefaultNinePatchButton.class);
		mNinePatchButton1.getPass(GUINinePatchDrawer.class).setNinePatch(ninePatch).setNinePatchPressed(ninePatchPressed);
		mNinePatchButton1.createCaption("Nine patch button").setPosAndExtentsCentered(2, 0.6f, 0.9f, 0.2f);

		mNinePatchButton2 = mGUI.addComponent(DefaultNinePatchButton.class);
		mNinePatchButton2.getPass(GUINinePatchDrawer.class).setNinePatch(ninePatch).setNinePatchPressed(ninePatchPressed);
		mNinePatchButton2.createCaption("Wider nine patch button").setPosAndExtentsCentered(2, 0.9f, 1.2f, 0.2f);

		mIconButton = mGUI.addComponent(DefaultIconButton.class);
		mIconButton.getPass(GUINinePatchDrawer.class).setNinePatch(ninePatch).setNinePatchPressed(ninePatchPressed);
		mIconButton.getPass(GUIIconDrawer.class).setIcon(new TextureCoordinatesQuad().initBiased(0.5f,0.5f,1,1,0.02f), 0.16f);
		mIconButton.createCaption("Button with icon").setPosAndExtentsCentered(2, 1.2f, 1.0f, 0.2f);

		final Texture outlineTex = mGFXLoader.getImage("cartoon_stroke");
		mOutlineButton = mGUI.addComponent(DefaultOutlineButton.class);
		mOutlineButton.getPass(GUIOutlineDrawer.class).mStroke.mColor.set(0.8f,0.6f,0.1f);
		mOutlineButton.createCaption("Outline button").setPosAndExtentsCentered(2, 1.5f, 1.0f, 0.2f);

		final GUIInteractiveRectComponent scrollPanel = mGUI.addComponent(GUIInteractiveRectComponent.class);
		scrollPanel.setPasses(null,new GUINinePatchDrawer().setNinePatch(ninePatch),null,new GUIClippedDrawer(new RectDrawer(mGraphics2D)),new GUIOutlineDrawer());
		scrollPanel.setPosAndExtents(2.5f, 0.8f, 1.0f, 1.1f);

		mGUI.setPassTexture(0, mGraphics.mWhiteTexture);
		mGUI.setPassTexture(1, ninePatchTex);
		mGUI.setPassTexture(2, ninePatchTex);
		mGUI.setPassTexture(3, null);
		mGUI.setPassTexture(4, outlineTex);
	}

	@Override
	protected void step(float deltaTime) {
		mGUI.step(deltaTime);
	}

	@Override
	protected void draw() {
		mGraphics.clear(0, 0, 0.1f);
		mGraphics3D.setDefaultProgram();
		mGUI.draw();
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		mGUI.handleEvent(event);
		return false;
	}

	@Override
	public void onGUIAction(GUIComponent sender) {
		if(sender==mToggleButton) {
			mInnerPanel.mVisible = !mInnerPanel.mVisible;
		}else{
			if(sender instanceof GUIButton)
				System.out.println("Clicked "+((GUIButton)sender).getCaption().createRawString());
		}
	}

	//Always triggered when clicking on the surface (independent of GUI)
	@Override
	public void pointerUp(float x,float y,SurfacePointerEvent event) {
//		if(event.mButton==AbstractPointerEvent.BUTTON_RIGHT)
//			mPanel.setPosition(mGUI.normToGUIX(x), mGUI.normToGUIY(y));
		if(event.mButton==YangPointerEvent.BUTTON_MIDDLE)
			mPanel.mVisible = !mPanel.mVisible;
	}

	//Triggered, whenever a GUI component was clicked
	@Override
	public void guiPointerUp(float x,float y,GUIPointerEvent event) {
//		if(event.mSender == mGUI.mMainContainer)
//			mPanel.setPosition(x,y);
	}

	@Override
	public void guiClick(GUIPointerEvent pointerEvent) {

	}

	@Override
	public void guiFocusedDrag(GUIPointerEvent event) {

	}

	@Override
	public void guiPointerDown(float x, float y, GUIPointerEvent event) {

	}

	@Override
	public void guiPointerMoved(float x, float y, GUIPointerEvent event) {

	}

	@Override
	public void guiPointerDragged(float x, float y, GUIPointerEvent event) {

	}

	@Override
	public void guiEnter(GUIComponent sender) {

	}

	@Override
	public void guiExit(GUIComponent sender) {

	}

}
