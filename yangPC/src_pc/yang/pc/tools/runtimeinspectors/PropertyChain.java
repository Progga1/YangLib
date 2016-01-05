package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;


public abstract class PropertyChain extends InspectorComponent implements MouseListener {

	private JPanel mCaptionPanel;
	private PropertiesPanel mMainPanel;
	private JPanel mTopLevelPanel;
	private JLabel mCaption;
	protected InspectorComponent mComponents[];

	protected abstract InspectorComponent[] createComponents();

	@Override
	protected void postInit() {
		mMainPanel = new PropertiesPanel(mPropPanel);
		mCaptionPanel = new JPanel();
//		mCaptionPanel.setLayout(new BoxLayout(mCaptionPanel,BoxLayout.PAGE_AXIS));
		mCaptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		mCaptionPanel.setBackground(InspectorGUIDefinitions.CL_CHAIN_COMPONENT_CAPTION_BACKGROUND);
		mCaptionPanel.setBorder(InspectorGUIDefinitions.CHAIN_COMPONENT_CAPTION_BORDER);
		mCaption = new JLabel(mName);
		mCaption.setForeground(InspectorGUIDefinitions.CL_CHAIN_COMPONENT_CAPTION_FONT);
		mCaptionPanel.add(mCaption);
		mComponents = createComponents();
		for(InspectorComponent component:mComponents) {
			mMainPanel.add(component);
		}
		mTopLevelPanel = new JPanel();
		mTopLevelPanel.setLayout(new BorderLayout());
		mTopLevelPanel.add(mMainPanel,BorderLayout.CENTER);
		mTopLevelPanel.add(mCaptionPanel,BorderLayout.NORTH);
		mTopLevelPanel.setBorder(InspectorGUIDefinitions.CHAIN_COMPONENT_BORDER);
		mCaption.addMouseListener(this);
		mTopLevelPanel.addMouseListener(this);
	}

//	@Override
//	protected String getStringOutput(InspectionInterface object) {
//		super.getStringOutput(object);
//		if(mSaveString==null) {
////			mSaveString = "-->"+mName+"\r\n";
//			mSaveString = "{\r\n";
//			for(InspectorComponent component:mComponents) {
//				//String subStr = component.getStringOutput(object);
//				String subStr = component.mSaveString;
//				if(subStr!=null) {
//					mSaveString += component.mName+"="+subStr+"\r\n";
//				}
//			}
//			mSaveString += "}";
//		}
//
//		return mName+"="+mSaveString;
//	}

	@Override
	protected String getFileOutputString() {
		String result = "{\r\n";
		for(InspectorComponent component:mComponents) {
			//String subStr = component.getStringOutput(object);
			String subStr = component.getFileOutputString();
			if(subStr!=null) {
				result += component.mName+"="+subStr+"\r\n";
			}
		}
		result += "}";
		return result;
	}

	@Override
	public void loadFromStream(String value,BufferedReader reader) throws IOException {
		mMainPanel.loadFromStream(null,reader);
	}

	@Override
	protected Component getComponent() {
		return mTopLevelPanel;
	}

	@Override
	protected void refreshOutValue() {
		for(InspectorComponent component:mComponents) {
			component.refreshOutValue();
		}
	}

	@Override
	protected void postValueChanged() {
		for(InspectorComponent component:mComponents) {
			component.postValueChanged();
		}
	}

	@Override
	protected void updateGUI() {
		for(InspectorComponent component:mComponents) {
			component.updateGUI();
		}
	}

	@Override
	protected boolean useDefaultCaptionLayout() {
		return false;
	}

	@Override
	public boolean hasFocus() {
		for(InspectorComponent component:mComponents) {
			if(component.hasFocus())
				return true;
		}
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent ev) {
		mMainPanel.setVisible(!mMainPanel.isVisible());
		if(mMainPanel.isVisible()) {
			mCaption.setText(mName);
		}else
			mCaption.setText(mName+" >>>");
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

}
