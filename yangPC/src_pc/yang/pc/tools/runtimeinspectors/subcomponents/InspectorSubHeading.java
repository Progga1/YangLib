package yang.pc.tools.runtimeinspectors.subcomponents;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;

public class InspectorSubHeading extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;

	private String mName;
	private JPanel mCaptionPanel;
	private JLabel mCaption;
	private Component mMainPanel;

	public InspectorSubHeading(Component innerComponent,String caption,boolean bright) {
		mMainPanel = innerComponent;
		mCaptionPanel = new JPanel();
//		mCaptionPanel.setLayout(new BoxLayout(mCaptionPanel,BoxLayout.PAGE_AXIS));
		mCaptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		mCaptionPanel.setBackground(bright?InspectorGUIDefinitions.CL_CHAIN_COMPONENT_CAPTION_BACKGROUND_BRIGHT:InspectorGUIDefinitions.CL_CHAIN_COMPONENT_CAPTION_BACKGROUND);
		mCaptionPanel.setBorder(InspectorGUIDefinitions.BORDER_CHAIN_COMPONENT_CAPTION);
		mCaption = new JLabel();
		mCaption.setForeground(InspectorGUIDefinitions.CL_CHAIN_COMPONENT_CAPTION_FONT);
		mCaptionPanel.add(mCaption);
		setName(caption);
		setLayout(new BorderLayout());
		add(mMainPanel,BorderLayout.CENTER);
		add(mCaptionPanel,BorderLayout.NORTH);
		setBorder(bright?InspectorGUIDefinitions.BORDER_CHAIN_COMPONENT_BRIGHT:InspectorGUIDefinitions.BORDER_CHAIN_COMPONENT);
		mCaption.addMouseListener(this);
		mCaptionPanel.addMouseListener(this);
	}

	public InspectorSubHeading(Component innerComponent,boolean bright) {
		this(innerComponent,"",bright);
	}

	@Override
	public void setName(String name) {
		mName = name;
		refreshName();
	}

	protected void refreshName() {
		if(mMainPanel.isVisible()) {
			mCaption.setText(mName);
		}else
			mCaption.setText(mName+" >>>");
	}

	@Override
	public void mouseClicked(MouseEvent ev) {

	}

	@Override
	public void mouseEntered(MouseEvent ev) {

	}

	@Override
	public void mouseExited(MouseEvent ev) {

	}

	@Override
	public void mousePressed(MouseEvent ev) {
		setCollapsed(!isCollapsed());
		refreshName();
	}

	@Override
	public void mouseReleased(MouseEvent ev) {

	}

	public void setCollapsed(boolean collapsed) {
		mMainPanel.setVisible(!collapsed);
		refreshName();
	}

	public boolean isCollapsed() {
		return !mMainPanel.isVisible();
	}


}
