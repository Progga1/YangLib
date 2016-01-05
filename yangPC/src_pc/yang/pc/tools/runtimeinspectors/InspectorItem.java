package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import yang.pc.tools.runtimeinspectors.subcomponents.CheckLabel;

public class InspectorItem extends JPanel {

	private static final long serialVersionUID = 1L;

	protected InspectorPanel mInspector;
	protected CheckLabel mLinkCheckLabel;
	protected InspectorComponent mInspectorComponent;
	public JLabel mCaption;
	private JPanel mCaptionPanel;


	public InspectorItem(InspectorPanel panel,InspectorComponent inspectorComponent) {
		mInspector = panel;
		mInspectorComponent = inspectorComponent;
		inspectorComponent.mHolder = this;
		mCaption = new JLabel(inspectorComponent.getName());
		mCaption.setForeground(InspectorGUIDefinitions.CL_LABEL);
//		mCaption.setHorizontalAlignment(SwingConstants.RIGHT);
//		mCaption.setVerticalAlignment(SwingConstants.CENTER);
		if(mInspectorComponent.useDefaultCaptionLayout()) {
			setLayout(new BorderLayout());
			mCaptionPanel = new JPanel();
//			mCaptionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
			mCaptionPanel.setLayout(new BorderLayout());
			mCaptionPanel.setBackground(InspectorGUIDefinitions.CL_LABEL_BACKGROUND);
			mCaptionPanel.add(mCaption,BorderLayout.EAST);
			if(inspectorComponent.isLinkingSupported()) {
				setLinkable();
			}
			add(mCaptionPanel,BorderLayout.WEST);
			add(inspectorComponent.getComponent(),BorderLayout.CENTER);
			mCaptionPanel.setBorder(InspectorGUIDefinitions.BORDER_PADDING);
			setBorder(InspectorGUIDefinitions.BORDER_PROPERTY);
		}else{
			mCaptionPanel = null;
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			add(inspectorComponent.getComponent());
		}

		setBackground(InspectorGUIDefinitions.CL_UNUSED_SPACE);
		refreshLayout();
	}

	public void setLinkable() {
		if(mLinkCheckLabel!=null)
			return;
		if(!mInspectorComponent.isLinkingSupported())
			mInspectorComponent.setLinkable();
		mLinkCheckLabel = new CheckLabel("Link",InspectorGUIDefinitions.CL_LINKED,InspectorGUIDefinitions.CL_UNLINKED);
		mCaptionPanel.add(mLinkCheckLabel,BorderLayout.WEST);
		mLinkCheckLabel.setListener(mInspectorComponent);
	}

	public void refreshLayout() {
		setCaption(mInspectorComponent.mName);
		if(mCaptionPanel!=null)
			mCaptionPanel.setPreferredSize(new Dimension(mInspector.getCaptionWidth(),mInspector.getDefaultComponentHeight()));
	}

	public void setCaption(String caption) {
		mCaption.setText(caption);
	}

	public InspectorComponent getInspectorComponent() {
		return mInspectorComponent;
	}

}
