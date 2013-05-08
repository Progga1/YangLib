package yang.samples.statesystem.states;

import yang.events.eventtypes.AbstractPointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.FloatColor;
import yang.graphics.font.DrawableString;
import yang.samples.statesystem.SampleState;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.defaultbuttons.DefaultRectButton;
import yang.util.gui.components.defaults.ColoredGUIPanel;
import yang.util.gui.components.defaults.GUILabel;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public class GUISampleState extends SampleState implements GUIActionListener,GUIPointerListener {

	private BasicGUI mGUI;
	private ColoredGUIPanel mPanel;
	private DefaultRectButton mToggleButton;
	private ColoredGUIPanel mInnerPanel;
	
	@Override
	protected void initGraphics() {
		mGraphics2D.activate();
		mGUI = new BasicGUI(mGraphics2D);
		mGUI.setDefaultActionListener(this);
		mGUI.setDefaultPointerListener(this);
		mPanel = mGUI.addComponent(new ColoredGUIPanel());
		mPanel.setColor(FloatColor.WHITE);
		mPanel.setPosAndExtends(0.1f, 0.3f, 0.9f, 1.2f);
		
		DrawableString caption = new DrawableString("Label").setAnchors(DrawableString.ANCHOR_LEFT,DrawableString.ANCHOR_TOP);
		mPanel.addComponent(new GUILabel(caption).setPosition(0.1f, 0.1f));
		
		mToggleButton = mPanel.addComponent(new DefaultRectButton());
		mToggleButton.createCaption("Button1").setColor(1, 0.5f, 0).setPosAndExtends(0.1f, 0.3f, mPanel.mWidth-0.2f, 0.16f);
		
		mInnerPanel = mPanel.addComponent(new ColoredGUIPanel());
		mInnerPanel.setColor(0.8f,0.8f,0.8f).setPosAndExtends(0.1f, 0.5f, mPanel.mWidth-0.2f, 0.5f);
		mInnerPanel.addComponent(new DefaultRectButton().createCaption("Inner1").setPosAndDimCentered(mInnerPanel.mWidth/2, 0.1f, mInnerPanel.mWidth-0.12f, 0.14f));
		mInnerPanel.addComponent(new DefaultRectButton().createCaption("Inner2").setPosAndDimCentered(mInnerPanel.mWidth/2, 0.25f, mInnerPanel.mWidth-0.12f, 0.14f));
		mInnerPanel.addComponent(new DefaultRectButton().createCaption("Inner3").setPosAndDimCentered(mInnerPanel.mWidth/2, 0.4f, mInnerPanel.mWidth-0.12f, 0.14f));
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics.clear(0, 0, 0.1f);
		
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
			if(sender instanceof DefaultRectButton)
				System.out.println("Clicked "+((DefaultRectButton)sender).getCaption().createRawString());
		}
	}
	
	//Always triggered when clicking on the surface (independent of GUI)
	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		if(event.mButton==AbstractPointerEvent.BUTTON_RIGHT)
			mPanel.setPosition(mGUI.normToGUIX(x), mGUI.normToGUIY(y));
		if(event.mButton==AbstractPointerEvent.BUTTON_MIDDLE)
			mPanel.mVisible = !mPanel.mVisible;
	}
	
	//Triggered, whenever a GUI component was clicked
	@Override
	public void guiPointerUp(float x,float y,GUIPointerEvent event) {
		if(event.mSender == mGUI.mMainContainer)
			mPanel.setPosition(x,y);
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

}
