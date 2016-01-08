package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;
import yang.pc.tools.runtimeinspectors.subcomponents.InspectorButton;
import yang.pc.tools.runtimeinspectors.subcomponents.InspectorButtonListener;

public class PropertyButtons extends InspectorComponent implements InspectorButtonListener {

	protected InspectorButton[] mButtons;
	protected JPanel mPanel;
	protected int mPressedButtonId = -1;

	public PropertyButtons(int buttonCount) {
		mButtons = new InspectorButton[buttonCount];
		for(int i=0;i<buttonCount;i++) {
			mButtons[i] = new InspectorButton("Button "+i);
			mButtons[i].setListener(this);
		}
	}

	public PropertyButtons(String... captions) {
		this(captions.length);
		for(int i=0;i<captions.length;i++) {
			mButtons[i].setCaption(captions[i]);
		}
	}

	public InspectorButton getButton(int index) {
		return mButtons[index];
	}

	@Override
	protected void postInit() {
		mPanel = new JPanel();
		mPanel.setBackground(InspectorGUIDefinitions.CL_COMPONENT_DEFAULT_BACKGROUND);
		mPanel.setLayout(new FlowLayout(FlowLayout.LEFT,4,4));
		for(int i=0;i<mButtons.length;i++) {
			mPanel.add(mButtons[i]);
		}
	}

	@Override
	protected Component getComponent() {
		return mPanel;
	}

	@Override
	public PropertyButtons clone() {
		return new PropertyButtons(mButtons.length);
	}

	@Override
	public void set(InspectorComponent template) {
		PropertyButtons buttons = (PropertyButtons)template;
		for(int i=0;i<buttons.mButtons.length;i++) {
			mButtons[i].setCaption(buttons.mButtons[i].getCaption());
		}
	}

	@Override
	public void buttonPressed(InspectorButton sender, int button) {
		for(int i=0;i<mButtons.length;i++) {
			if(sender==mButtons[i])
				mPressedButtonId = i;
		}
		super.notifyValueUserInput();
	}

	@Override
	public int getInt() {
		return mPressedButtonId;
	}

	@Override
	public String getString() {
		return mButtons[mPressedButtonId].getCaption();
	}

	@Override
	public String createUserReadableString(boolean includeObjectName) {
		if(mPressedButtonId<0)
			return null;
		return "ACTION "+getFullName(true)+NAME_SPLITTER+mButtons[mPressedButtonId].getCaption();
	}

	@Override
	public boolean handleShortCut(int code) {
		mPressedButtonId = code;
		return true;
	}

}
