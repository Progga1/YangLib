package yang.samples.statesystem.states;

import yang.samples.statesystem.SampleState;
import yang.util.gui.BasicGUI;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.defaultbuttons.DefaultRectButton;
import yang.util.gui.components.defaults.ColoredGUIPanel;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class GUISampleState extends SampleState implements GUIActionListener {

	private BasicGUI mGUI;
	
	@Override
	protected void initGraphics() {
		mGUI = new BasicGUI(mGraphics2D);
		ColoredGUIPanel panel = new ColoredGUIPanel();
		
		DefaultRectButton button1 = new DefaultRectButton();
		button1.createCaption("Button1").setColor(1, 0.5f, 0).setPosAndExtends(0.1f, 0.1f, 0.5f, 0.2f);
		
		panel.addComponent(button1);
		
		mGUI.addComponent(panel);
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		
	}

	@Override
	public void onGUIAction(GUIComponent sender) {
		
	}

}
