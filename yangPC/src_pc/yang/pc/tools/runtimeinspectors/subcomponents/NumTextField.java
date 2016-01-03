package yang.pc.tools.runtimeinspectors.subcomponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import yang.model.NumberIO;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;
import yang.pc.tools.runtimeinspectors.LinkedNumComponents;

public class NumTextField extends JPanel implements MouseMotionListener,MouseListener,ActionListener,FocusListener,NumberIO {

	private static final long serialVersionUID = 1L;

	public static Border SCROLL_WIDGET_BORDER = BorderFactory.createBevelBorder(0);
	public static Border BORDER = BorderFactory.createMatteBorder(1,0,1,1,InspectorGUIDefinitions.CL_OUTLINE);

	private LinkedNumComponents mLinks;
	private JFormattedTextField mTextField;
	private JPanel mScrollWidget;
	private float mScrollFactor = 0.01f;
	private ActionListener mListener;
	private int mMaxDigits = 3;
	private double mCurValue;
	private int mMouseDown = -1;
	private double mDefaultValue = 0;
	private double mMinValue = -Double.MAX_VALUE;
	private double mMaxValue = Double.MAX_VALUE;
	private boolean mLinking = false;

	private int mLstX = Integer.MAX_VALUE,mLstY = Integer.MAX_VALUE;

	public static String maxDigitsString(String string,int maxDigits) {
		int commaInd = string.indexOf(".");
		if(commaInd>-1 && string.length()-1-commaInd>maxDigits)
			string = string.substring(0,commaInd+maxDigits+1);
		return string;
	}

	public NumTextField() {
		mTextField = new JFormattedTextField();
		mScrollWidget = new JPanel();
		mScrollWidget.setBackground(InspectorGUIDefinitions.CL_SCROLL_WIDGET);
//		mScrollWidget.setBorder(SCROLL_WIDGET_BORDER);
//		mTextField.setValue(new Float(0));
		mTextField.setText("0");
		setLayout(new BorderLayout());
		mTextField.addFocusListener(this);
		add(mTextField,BorderLayout.CENTER);
		add(mScrollWidget,BorderLayout.EAST);
		setScrollWidgetWidth(10);
		mScrollWidget.addMouseListener(this);
		mScrollWidget.addMouseMotionListener(this);
		mTextField.setMinimumSize(new Dimension(12,0));
		this.setMinimumSize(new Dimension(12,0));
		mTextField.setPreferredSize(new Dimension(12,0));
		mTextField.setBorder(InspectorGUIDefinitions.TEXT_FIELD_BORDER);
		mTextField.addActionListener(this);
		mTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		mTextField.addMouseListener(this);
		mScrollWidget.setBorder(BORDER);
	}

	@Override
	public void setMinValue(double minValue) {
		mMinValue = minValue;
	}

	@Override
	public void setMaxValue(double maxValue) {
		mMaxValue = maxValue;
	}

	@Override
	public void setDefaultValue(double defaultValue) {
		mDefaultValue = defaultValue;
	}

	public void setMaxDigits(int maxDigits) {
		mMaxDigits = maxDigits;
	}

	public void reset() {
		setDouble(mDefaultValue);
	}

	@Override
	public void setScrollFactor(float stepsPerPixel) {
		mScrollFactor = stepsPerPixel;
	}

	public void setScrollWidgetWidth(int pixels) {
		mScrollWidget.setPreferredSize(new Dimension(pixels,0));
	}

	public void setActionListener(ActionListener listener) {
		mListener = listener;
		mTextField.addActionListener(listener);
	}

	public void setLinkedNumberIOs(LinkedNumComponents links) {
		mLinks = links;
	}

	private void setVal(double val) {
		if(val>mMaxValue)
			mCurValue = mMaxValue;
		else if(val<mMinValue)
			mCurValue = mMinValue;
		else
			mCurValue = val;
	}

	@Override
	public void setDouble(double val) {
		if(val==mCurValue)
			return;
		setVal(val);
		mTextField.setText(maxDigitsString(Double.toString(mCurValue),mMaxDigits));
	}

	public void setFloat(float val) {
		if(val==mCurValue)
			return;
		setVal(val);
		mTextField.setText(maxDigitsString(Double.toString(mCurValue),mMaxDigits));
	}

	@Override
	public double getDouble() {
		return mCurValue;
	}

	public float getFloat() {
		return (float)mCurValue;
	}

	@Override
	public boolean hasFocus() {
		return mTextField.hasFocus() || mMouseDown>=0;
	}

	@Override
	public void mouseDragged(MouseEvent ev) {
		int x = ev.getX();
		int y = ev.getY();
		if(mLstX!=Integer.MAX_VALUE) {
			double prevVal = mCurValue;
			int deltaX = x-mLstX;
			int deltaY = y-mLstY;
			float fac = mScrollFactor;
			switch(mMouseDown) {
			case 1:
				break;
			case 3:
				fac *= 0.1f;
				break;
			case 2:
				fac *= 10;
				break;
			}
			setDouble(mCurValue - deltaY*fac + deltaX*fac);
			notifyLinks();
			mListener.actionPerformed(null);
		}
		mLstX = x;
		mLstY = y;
	}

	@Override
	public void mouseMoved(MouseEvent ev) {

	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		if(ev.getClickCount()>1)
			mTextField.selectAll();
	}

	@Override
	public void mouseEntered(MouseEvent ev) {

	}

	@Override
	public void mouseExited(MouseEvent ev) {

	}

	@Override
	public void mousePressed(MouseEvent ev) {
		if(ev.getSource()!=mScrollWidget)
			return;
		mLstX = Integer.MAX_VALUE;
		mLstY = Integer.MAX_VALUE;
		if(ev.getButton()==MouseEvent.BUTTON1)
			mMouseDown = 1;
		if(ev.getButton()==MouseEvent.BUTTON2)
			mMouseDown = 2;
		if(ev.getButton()==MouseEvent.BUTTON3)
			mMouseDown = 3;
		startLink();
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		if(ev.getSource()!=mScrollWidget)
			return;
		mMouseDown = -1;
		endLink();
	}

	private void startLink() {
		if(mLinks!=null && mLinks.canStart()) {
			mLinking = true;
			mLinks.startUserInput(this);
		}
	}

	private void notifyLinks() {
		if(mLinking)
			mLinks.valueChanged();
	}

	private void endLink() {
		if(mLinking) {
			mLinks.endUserInput();
			mLinking = false;
		}
	}

	private void onValueUserInput() {
		String text = mTextField.getText();
		if(!text.equals("")) {
			try{
				startLink();
				mCurValue = Double.parseDouble(text);
				if(mCurValue<mMinValue)
					setDouble(mMinValue);
				else if(mCurValue>mMaxValue)
					setDouble(mMaxValue);
				notifyLinks();
				endLink();
			}catch(NumberFormatException ex) {

			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		onValueUserInput();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
//		mTextField.selectAll();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		onValueUserInput();
		this.mListener.actionPerformed(null);
	}

	public void setRange(float minValue, float maxValue) {
		mMinValue = minValue;
		mMaxValue = maxValue;
	}

}
