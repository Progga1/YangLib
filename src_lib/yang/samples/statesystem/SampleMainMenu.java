package yang.samples.statesystem;

import java.util.HashMap;

import yang.events.Keys;
import yang.events.eventtypes.YangEvent;
import yang.samples.statesystem.states.GUISampleState;
import yang.samples.statesystem.states.IcyTerrainState;
import yang.samples.statesystem.states.MatrixStackState;
import yang.samples.statesystem.states.ParticleSampleState;
import yang.samples.statesystem.states.PolygonSampleState;
import yang.samples.statesystem.states.StringSampleState;
import yang.samples.statesystem.states.StrokeDrawerState;
import yang.samples.statesystem.states.TailSampleState;
import yang.util.NonConcurrentList;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUICoordinatesMode;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.defaultbuttons.DefaultRectButton;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleMainMenu extends YangProgramState<YangProgramStateSystem> implements GUIActionListener {

	public static float SCALE = 1.16f;
	
	protected BasicGUI mGUI;
	protected NonConcurrentList<DefaultRectButton> mButtons;
	protected HashMap<GUIComponent,YangProgramState<?>> mProgramStates;
	
	public SampleMainMenu() {
		mButtons = new NonConcurrentList<DefaultRectButton>();
		mProgramStates = new HashMap<GUIComponent,YangProgramState<?>>(16);
	}
	
	@Override
	public void initGraphics() {
		mGUI = new BasicGUI(mGraphics2D,GUICoordinatesMode.SCREEN,true,4);
		//Alternatively: normalized: mGUI = new BasicGUI(mGraphics2D,GUICoordinatesMode.NORMALIZED,true);
		mGUI.setDefaultActionListener(this);
		addMenuItem("Strings", new StringSampleState());
		addMenuItem("GUI", new GUISampleState());
		addMenuItem("Tails", new TailSampleState());
		addMenuItem("Matrix stack", new MatrixStackState());
		addMenuItem("Stroke", new StrokeDrawerState());
		addMenuItem("Icy terrain", new IcyTerrainState());
		addMenuItem("Polygon", new PolygonSampleState());
		addMenuItem("Particles", new ParticleSampleState());
		refreshLayout();
	}
	
	private void refreshLayout() {
		//mGUI.refreshCoordinateSystem();
		float y = mGUI.getGUICenterY()-0.24f*(mButtons.size()-1)*0.5f;
		for(DefaultRectButton button:mButtons) {
			button.setPosCentered(mGUI.getGUICenterX(), y);
			y += 0.2f*SCALE;
		}
	}
	
	public void addMenuItem(String caption, YangProgramState<?> state) {
		DefaultRectButton newButton = new DefaultRectButton();
		newButton.getPass(GUICaptionDrawer.class).createCaption(caption);
		newButton.getPass(GUIRectDrawer.class).setBorderSize(0.01f);
		//In normalized coordinates: newButton.setPosAndDimCentered(0, -1+(0.15f+mButtons.size()*0.24f)*SCALE,1*SCALE, 0.15f*SCALE);
		mGUI.addComponent(newButton);
		newButton.setExtents(SCALE, 0.15f*SCALE);
		mButtons.add(newButton);
		mProgramStates.put(newButton, state);
	}
	
	@Override
	public void step(float deltaTime) {
		mGUI.step(deltaTime);
	}

	@Override
	public void draw() {
		mGraphics2D.activate();
		mGraphics.clear(0, 0, 0.1f);
		mGUI.draw();
	}
	
	@Override
	public boolean rawEvent(YangEvent event) {
		if(mGUI!=null)
			return mGUI.handleEvent(event)!=null;
		else
			return false;
	}

	@Override
	public void onGUIAction(GUIComponent sender) {
		YangProgramState<?> state = mProgramStates.get(sender);
		if(state!=null)
			mStateSystem.setState(state);
	}
	
	@Override
	public void keyUp(int code) {
		if(code==Keys.ESC)
			mStateSystem.exit();
	}

}
