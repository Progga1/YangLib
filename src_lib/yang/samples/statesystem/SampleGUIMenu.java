package yang.samples.statesystem;

import java.util.HashMap;

import yang.events.eventtypes.YangInputEvent;
import yang.samples.statesystem.states.IcyTerrainState;
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
		addMenuItem("Icy terrain", new IcyTerrainState());
	}
	
	public void addMenuItem(String caption, YangProgramState<?> state) {
		DefaultRectButton newButton = new DefaultRectButton();
		newButton.createCaption(caption).setExtends(0.6f, 0.15f);
		newButton.setPosition(mGUI.getGUICenterX()-0.3f, 0.15f+mButtons.size()*0.24f);
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
		mGUI.handleEvent(event);
	}

	@Override
	public void onGUIAction(GUIComponent sender) {
		YangProgramState<?> state = mProgramStates.get(sender);
		if(state!=null)
			mStateSystem.setState(state);
	}

}
