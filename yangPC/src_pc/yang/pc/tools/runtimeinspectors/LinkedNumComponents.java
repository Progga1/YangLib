package yang.pc.tools.runtimeinspectors;

import yang.model.NumberIO;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;
import yang.util.YangList;

public class LinkedNumComponents {

	public boolean mLinkActive = false;

	protected YangList<NumberIO> mLinkedComponents = new YangList<NumberIO>();
	protected double[] mStartValues;
	protected int mCurSenderIndex = -1;
	protected NumberIO mCurSender = null;

	public LinkedNumComponents() {

	}

	public void valueChanged() {
		if(!mLinkActive)
			return;
		double curVal = mCurSender.getDouble();

		double startVal = mStartValues[mCurSenderIndex];
		double ratio;
		if(startVal==0)
			ratio = 0;
		else
			ratio = curVal/startVal;
		int i=0;
		for(NumberIO component:mLinkedComponents) {
			if(component!=mCurSender) {
				double compVal = mStartValues[i];
				if(compVal==0 || ratio==0)
					component.setDouble(curVal);
				else
					component.setDouble(compVal*ratio);
				component.updateGUI();
			}
			i++;
		}
	}

	public boolean canStart() {
		return mLinkActive && mCurSender==null;
	}

	public void addComponent(NumberIO component) {
		mLinkedComponents.add(component);
	}

	public void removeComponent(NumTextField component) {
		mLinkedComponents.remove(component);
	}

	public void startUserInput(NumberIO sender) {
		mCurSenderIndex = mLinkedComponents.indexOf(sender);
		mCurSender = sender;
	}

	public void endUserInput() {
		mCurSenderIndex = -1;
		mCurSender = null;
	}

	public void refreshStartValues() {
		int l = mLinkedComponents.size();
		if(mStartValues==null || mStartValues.length<l)
			mStartValues = new double[l];
		int i=0;
		for(NumberIO component:mLinkedComponents) {
			mStartValues[i] = component.getDouble();
			i++;
		}
	}

}
