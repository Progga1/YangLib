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

	public static final int MAX_CLICK_MILLIS = 200;
	public static Border SCROLL_WIDGET_BORDER = BorderFactory.createBevelBorder(0);
	public static Border BORDER = BorderFactory.createMatteBorder(1,0,1,1,InspectorGUIDefinitions.CL_DEFAULT_COMPONENT_OUTLINE);

	//Properties
	private int mMaxDigits = 3;
	private double mDefaultValue = 0;
	private double mMinValue = -Double.MAX_VALUE;
	private double mMaxValue = Double.MAX_VALUE;
	private boolean mCyclic = false;
	private float mScrollFactor = 0.01f;
	private float mClickSteps = 1;

	//State
	private boolean mLinking = false;
	protected double mCurValue;
	private int mMouseDown = -1;
	private long mMouseDownTime = -1;
	private double mStartDragValue = -1;

	//Objects
	private String mOrigText = "";
	private LinkedNumComponents mLinks;
	private JFormattedTextField mTextField;
	private JPanel mScrollWidget;
	private ActionListener mListener;

	private int mLstX = Integer.MAX_VALUE,mLstY = Integer.MAX_VALUE;

	public static String maxDigitsString(String string,int maxDigits) {
		int commaInd = string.indexOf(".");
		if(commaInd>-1) {
			int eId = string.indexOf("E");
			if(eId>=0)
				return "0";
			else if(maxDigits<=0) {
				string = string.substring(0,commaInd);
			}else if(string.length()-1-commaInd>maxDigits)
				string = string.substring(0,commaInd+maxDigits+1);
		}
		return string;
	}

	public NumTextField() {
		mTextField = new JFormattedTextField();
		mTextField.setText("0");
		mOrigText = "0";
		mScrollWidget = new JPanel();
		mScrollWidget.setBackground(InspectorGUIDefinitions.CL_SCROLL_WIDGET);
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
		mTextField.setBorder(InspectorGUIDefinitions.BORDER_TEXT_FIELD);
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
		setDouble(mDefaultValue,true);
	}

	@Override
	public void setScrollFactor(float stepsPerPixel) {
		mScrollFactor = stepsPerPixel;
	}

	public void setClickSteps(float stepsPerClick) {
		mClickSteps = stepsPerClick;
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

	private double wrap(double val) {
		if(mCyclic) {
			if(mMaxValue==Double.MAX_VALUE || mMinValue==-Double.MIN_VALUE || mMaxValue==mMinValue) {
				throw new RuntimeException("No range given, can't cycle");
			}
			double diff = mMaxValue-mMinValue;
			while(val>mMaxValue) {
				val -= diff;
			}
			while(val<mMinValue) {
				val += diff;
			}
			return val;
		}else{
			if(val>mMaxValue)
				return mMaxValue;
			else if(val<mMinValue)
				return mMinValue;
			else
				return val;
		}
	}

	@Override
	public void updateGUI() {
		mTextField.setText(maxDigitsString(mOrigText,mMaxDigits));
	}

	public void setDouble(double val,boolean updateText) {
		double newVal = wrap(val);
		if(mCurValue==newVal)
			return;
		mCurValue = newVal;
		mOrigText = Double.toString(mCurValue);
		if(updateText)
			updateGUI();
	}

	@Override
	public void setDouble(double val) {
		 setDouble(val,false);
	}

	public void setFloat(float val,boolean updateText) {
		double newVal = wrap(val);
		if(mCurValue==newVal)
			return;
		mCurValue = newVal;
		mOrigText = Float.toString((float)mCurValue);
		if(updateText)
			updateGUI();
	}

	public void setFloat(float val) {
		setFloat(val,false);
	}

	public String getValueString() {
		return mOrigText;
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
		if(ev.getSource()==mScrollWidget) {
			int x = ev.getX();
			int y = ev.getY();
			if(mLstX!=Integer.MAX_VALUE) {
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
				setDouble(mCurValue - deltaY*fac + deltaX*fac,true);
				notifyLinks();
				mListener.actionPerformed(null);
			}
			mLstX = x;
			mLstY = y;
		}
	}

	@Override
	public void mouseMoved(MouseEvent ev) {

	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		if(ev.getSource()==mTextField && ev.getClickCount()>1)
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
		if(ev.getSource()==mScrollWidget) {
			mLstX = Integer.MAX_VALUE;
			mLstY = Integer.MAX_VALUE;
			if(ev.getButton()==MouseEvent.BUTTON1)
				mMouseDown = 1;
			if(ev.getButton()==MouseEvent.BUTTON2)
				mMouseDown = 2;
			if(ev.getButton()==MouseEvent.BUTTON3)
				mMouseDown = 3;
			mMouseDownTime = System.currentTimeMillis();
			mStartDragValue = mCurValue;
			startLink();
		}
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		if(ev.getSource()==mScrollWidget) {
			mMouseDown = -1;
			if(System.currentTimeMillis()-mMouseDownTime<=MAX_CLICK_MILLIS) {
				int dir = ev.getButton()==MouseEvent.BUTTON1?1:(ev.getButton()==MouseEvent.BUTTON3?-1:0);
				setDouble(mStartDragValue+mClickSteps*dir,true);
				notifyLinks();
				mListener.actionPerformed(null);
			}
			endLink();
		}
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
				double val = Double.parseDouble(text);
				if(val<mMinValue || val>mMaxValue) {
					setDouble(wrap(val));
					updateGUI();
				}else{
					mCurValue = val;
					mOrigText = text;
				}
			}catch(NumberFormatException ex) {

			}finally{
				notifyLinks();
				endLink();
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
		mListener.actionPerformed(null);
	}

	public void setRange(float minValue, float maxValue) {
		mMinValue = minValue;
		mMaxValue = maxValue;
	}

	public void unlink() {
		if(mLinks!=null) {
			mLinks.removeComponent(this);
			mLinks = null;
		}
	}

	public boolean isCyclic() {
		return mCyclic;
	}

	public void setCyclic(boolean cyclic) {
		mCyclic = cyclic;
	}

	public void copyParameters(NumTextField template) {
		mMinValue = template.mMinValue;
		mMaxValue = template.mMaxValue;
		mScrollFactor = template.mScrollFactor;
		mClickSteps = template.mClickSteps;
		mCyclic = template.mCyclic;
		mMaxDigits = template.mMaxDigits;
	}

}
