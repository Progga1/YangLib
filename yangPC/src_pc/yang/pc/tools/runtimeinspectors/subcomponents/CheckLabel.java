package yang.pc.tools.runtimeinspectors.subcomponents;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import yang.pc.tools.runtimeinspectors.InspectorComponent;

public class CheckLabel extends JLabel implements MouseListener {

	private static final long serialVersionUID = 1L;

	public static final Color DEFAULT_CL_SELECTED = new Color(60,60,60);
	public static final Color DEFAULT_CL_UNSELECTED = new Color(180,180,180);

	private boolean mSelected = false;
	public Color mSelectedColor;
	public Color mUnselectedColor;
	private CheckLabelListener mListener;

	public CheckLabel(String caption,Color selectedColor,Color unselectedColor) {
		super(caption);
		mSelectedColor = selectedColor;
		mUnselectedColor = unselectedColor;
		addMouseListener(this);
		refresh();
	}

	public CheckLabel(String caption) {
		this(caption,DEFAULT_CL_SELECTED,DEFAULT_CL_UNSELECTED);
	}

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean selected) {
		if(selected==mSelected)
			return;
		mSelected = selected;
		refresh();
		mListener.selectionChanged(this);
	}

	private void refresh() {
		setForeground(mSelected?mSelectedColor:mUnselectedColor);
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
		if(ev.getButton()==MouseEvent.BUTTON1)
			setSelected(!mSelected);
	}

	@Override
	public void mouseReleased(MouseEvent ev) {

	}

	public void setListener(CheckLabelListener listener) {
		mListener = listener;
	}

}
