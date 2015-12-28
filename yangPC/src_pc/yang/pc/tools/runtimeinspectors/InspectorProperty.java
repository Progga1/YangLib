package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class InspectorProperty extends JPanel {

	private static final long serialVersionUID = 1L;

	private InspectorPanel mInspector;
	public JLabel mCaption;
	private JPanel mCaptionPanel;
	private BorderLayout mLayout;
	private InspectorComponent mInspectorComponent;

	public InspectorProperty(InspectorPanel panel,InspectorComponent rtpComponent) {
		mInspector = panel;
		mInspectorComponent = rtpComponent;
//		mPanel = new JPanel();
		mLayout = new BorderLayout();
		setLayout(mLayout);
		mCaptionPanel = new JPanel();
//		mCaptionPanel.setLayout(new BoxLayout(mCaptionPanel,BoxLayout.PAGE_AXIS));
		mCaptionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
		mCaptionPanel.setBackground(InspectorGUIDefinitions.CL_LABEL_BACKGROUND);
		mCaption = new JLabel(rtpComponent.getName());
		mCaptionPanel.add(mCaption);
		add(mCaptionPanel,BorderLayout.WEST);
		add(rtpComponent.getComponent(),BorderLayout.CENTER);
//		mCaption.setHorizontalAlignment(SwingConstants.RIGHT);
	//	mCaption.setVerticalAlignment(SwingConstants.CENTER);
		mCaptionPanel.setBorder(InspectorGUIDefinitions.PADDING_BORDER);
		setBackground(InspectorGUIDefinitions.CL_UNUSED_SPACE);
		setBorder(InspectorGUIDefinitions.PROPERTY_BORDER);
		refreshLayout();
	}

	public void refreshLayout() {
		setCaption(mInspectorComponent.mName);
		mCaptionPanel.setPreferredSize(new Dimension(mInspector.getCaptionWidth(),mInspector.getDefaultComponentHeight()));
//		mCaption.setPreferredSize(new Dimension(mInspector.getCaptionWidth(),mInspector.getDefaultComponentHeight()));
	}

	public void setCaption(String caption) {
		mCaption.setText(caption);
	}

	public InspectorComponent getRTPComponent() {
		return mInspectorComponent;
	}

}
