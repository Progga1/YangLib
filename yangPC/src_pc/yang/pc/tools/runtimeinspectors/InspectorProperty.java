package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class InspectorProperty extends JPanel {

	private static final long serialVersionUID = 1L;

	private InspectorPanel mRTPPanel;
	public JLabel mCaption;
//	private JPanel mPanel;
	private BorderLayout mLayout;
	private InspectorComponent mRTPComponent;

	public InspectorProperty(InspectorPanel panel,InspectorComponent rtpComponent) {
		mRTPPanel = panel;
		mRTPComponent = rtpComponent;
//		mPanel = new JPanel();
		mLayout = new BorderLayout();
		setLayout(mLayout);
		mCaption = new JLabel(rtpComponent.getName());
		add(mCaption,BorderLayout.WEST);
		add(rtpComponent.getComponent(),BorderLayout.CENTER);
		mCaption.setHorizontalAlignment(SwingConstants.RIGHT);
		mCaption.setVerticalAlignment(SwingConstants.CENTER);
		mCaption.setBorder(InspectorGUIDefinitions.PADDING_BORDER);
		setBackground(InspectorGUIDefinitions.CL_LABEL_BACKGROUND);
		setBorder(InspectorGUIDefinitions.PROPERTY_BORDER);
		refreshLayout();
	}

	public void refreshLayout() {
		setCaption(mRTPComponent.mName);
		mCaption.setBackground(Color.RED);
		mCaption.setPreferredSize(new Dimension(mRTPPanel.getCaptionWidth(),mRTPPanel.getDefaultComponentHeight()));
	}

	public void setCaption(String caption) {
		mCaption.setText(caption);
	}

	public InspectorComponent getRTPComponent() {
		return mRTPComponent;
	}

}
