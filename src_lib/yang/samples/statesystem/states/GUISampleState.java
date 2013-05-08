package yang.samples.statesystem.states;

import yang.samples.statesystem.SampleState;
import yang.util.gui.BasicGUI;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.defaultbuttons.DefaultRectButton;
import yang.util.gui.components.defaults.ColoredGUIPanel;
import yang.util.gui.interfaces.GUIActionListener;

public class GUISampleState extends SampleState implements GUIActionListener {

	private BasicGUI mGUI;
	
	@Override
	protected void initGraphics() {
		mGraphics2D.activate();
		mGUI = new BasicGUI(mGraphics2D);
		ColoredGUIPanel panel = mGUI.addComponent(new ColoredGUIPanel());
		panel.setPosAndExtends(0.1f, 0.1f, 0.7f, 1);
		
		DefaultRectButton button1 = panel.addComponent(new DefaultRectButton());
		button1.createCaption("Button1").setColor(1, 0.5f, 0).setPosAndExtends(0.1f, 0.1f, 0.5f, 0.2f);

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
	public void onGUIAction(GUIComponent sender) {
		
	}

}
