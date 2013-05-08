package yang.samples.statesystem;

import java.util.HashMap;

import yang.events.eventtypes.YangInputEvent;
import yang.samples.statesystem.states.GUISampleState;
import yang.samples.statesystem.states.IcyTerrainState;
import yang.samples.statesystem.states.PolygonSampleState;
import yang.samples.statesystem.states.StringSampleState;
import yang.samples.statesystem.states.TailsAndPointerSample;
import yang.util.NonConcurrentList;
import yang.util.gui.BasicGUI;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.defaultbuttons.DefaultRectButton;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleGUIMenu extends YangProgramState<YangProgramStateSystem> implements GUIActionListener {

	public static float SCALE = 1.2f;
	
	protected BasicGUI mGUI;
	protected NonConcurrentList<DefaultRectButton> mButtons;
	protected HashMap<GUIComponent,YangProgramState<?>> mProgramStates;
	
	public SampleGUIMenu() {
		mButtons = new NonConcurrentList<DefaultRectButton>();
		mProgramStates = new HashMap<GUIComponent,YangProgramState<?>>(16);
	}
	
	@Override
	public void initGraphics() {
		mGUI = new BasicGUI(mGraphics2D);
		mGUI.setDefaultActionListener(this);
		addMenuItem("Tails", new TailsAndPointerSample());
		addMenuItem("Strings", new StringSampleState());
		addMenuItem("GUI", new GUISampleState());
		addMenuItem("Icy terrain", new IcyTerrainState());
		addMenuItem("Polygon", new PolygonSampleState());
	}
	
	public void addMenuItem(String caption, YangProgramState<?> state) {
		DefaultRectButton newButton = new DefaultRectButton();
		newButton.createCaption(caption).setExtends(0.6f*SCALE, 0.15f*SCALE);
		newButton.setPosition(mGUI.getGUICenterX()-0.3f*SCALE, (0.15f+mButtons.size()*0.24f)*SCALE);
		mGUI.addComponent(newButton);
		mButtons.add(newButton);
		mProgramStates.put(newButton, state);
	}
	
	@Override
	public void step(float deltaTime) {
		
	}

	@Override
	public void draw() {
		mGraphics2D.activate();
		mGUI.draw();
	}
	
	@Override
	public void rawEvent(YangInputEvent event) {
		if(mGUI!=null)
			mGUI.handleEvent(event);
	}

	@Override
	public void onGUIAction(GUIComponent sender) {
		YangProgramState<?> state = mProgramStates.get(sender);
		if(state!=null)
			mStateSystem.setState(state);
	}

}
