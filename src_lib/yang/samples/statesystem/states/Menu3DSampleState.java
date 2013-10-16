package yang.samples.statesystem.states;

import yang.events.eventtypes.YangEvent;
import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.translator.glconsts.GLMasks;
import yang.samples.statesystem.SampleStateCameraControl;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUICoordinatesMode;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.defaultbuttons.DefaultRectButton;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.window.YangBillboardWindow;

public class Menu3DSampleState extends SampleStateCameraControl implements GUIActionListener {

	public BasicGUI mGUI1;
	public YangBillboardWindow<BasicGUI> mWindow1;

	@Override
	protected void postInit() {

		final StringProperties properties = new StringProperties(DrawableString.DEFAULT_PROPERTIES.mFont);
		properties.mGraphics = mGraphics3D;
		DrawableString.DEFAULT_HORIZONTAL_ANCHOR = DrawableString.ANCHOR_CENTER;
		DrawableString.DEFAULT_VERTICAL_ANCHOR = DrawableString.ANCHOR_MIDDLE;

		super.postInit();
		mGUI1 = new BasicGUI(mGraphics3D,GUICoordinatesMode.NORMALIZED,true,4);

		mGUI1.setDimensions(2, 2);
		mGUI1.setDefaultActionListener(this);

		final DefaultRectButton button1 = mGUI1.addComponent(DefaultRectButton.class);
		button1.setPosAndExtents(-0.5f, 0.5f, 1, 0.2f);
		button1.setCaption(new DrawableString("Button 1").setProperties(properties));
		button1.getPass(GUIRectDrawer.class).setBorderSize(0.01f);

		final DefaultRectButton button2 = mGUI1.addComponent(DefaultRectButton.class);
		button2.setPosAndExtents(-0.5f,0.2f, 1, 0.2f);
		button2.setCaption(new DrawableString("Button 2").setProperties(properties));
		button2.getPass(GUIRectDrawer.class).setBorderSize(0.01f);

		mWindow1 = new YangBillboardWindow<BasicGUI>(mGUI1,mGraphics3D);

		super.mOrthogonalProjection = false;
	}

	@Override
	protected void step(float deltaTime) {
		super.step(deltaTime);
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		super.rawEvent(event);
		return mWindow1.rawEvent(event);
	}

	@Override
	protected void draw() {
		mGraphics.clear(GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.activate();
		mGraphics3D.setDefaultProgram();
		mGraphics3D.setWhite();
		mGraphics.switchCulling(false);
		mGraphics.switchZBuffer(false);
		setCamera();
		mWindow1.draw();
		mGraphics3D.fillBuffers();
//		mGraphics3D.setColor(0.7f);
//		mGraphics.bindTexture(null);
//		mGraphics3D.drawCubeCentered(0, 0, 0, 1);
	}

	@Override
	public void onGUIAction(GUIComponent sender) {
		System.out.println("Clicked: "+sender);
	}

	@Override
	public void stop() {
		mGraphics3D.switchGameCoordinates(true);
	}

}
