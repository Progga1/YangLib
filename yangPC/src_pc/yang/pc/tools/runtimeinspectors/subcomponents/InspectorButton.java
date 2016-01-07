package yang.pc.tools.runtimeinspectors.subcomponents;

import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;

public class InspectorButton extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;

	public static final int BUTTON_LEFT = MouseEvent.BUTTON1;
	public static final int BUTTON_MIDDLE = MouseEvent.BUTTON2;
	public static final int BUTTON_RIGHT = MouseEvent.BUTTON3;

	protected JLabel mCaption;
	protected InspectorButtonListener mListener;

	public InspectorButton(String caption) {
		setBorder(InspectorGUIDefinitions.BORDER_BUTTON);
		mCaption = new JLabel();
		mCaption.addMouseListener(this);
//		this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		this.setLayout(new GridBagLayout());
		this.add(mCaption);
		mCaption.setHorizontalAlignment(SwingConstants.CENTER);
		mCaption.setVerticalAlignment(SwingConstants.CENTER);
		mCaption.setForeground(InspectorGUIDefinitions.CL_BUTTON_FONT);
		this.setBackground(InspectorGUIDefinitions.CL_BUTTON_BACKGROUND);
		addMouseListener(this);
		setCaption(caption);
	}

	public InspectorButton() {
		this("");
	}

	public void setCaption(String caption) {
		mCaption.setText(caption);
	}

	public void setListener(InspectorButtonListener listener) {
		mListener = listener;
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
		if(mListener!=null)
			mListener.buttonPressed(this,ev.getButton());
	}

	@Override
	public void mouseReleased(MouseEvent ev) {

	}

}
