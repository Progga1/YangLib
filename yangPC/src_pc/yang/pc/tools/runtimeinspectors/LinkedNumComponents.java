package yang.pc.tools.runtimeinspectors;

import yang.model.NumberIO;
import yang.util.YangList;

public class LinkedNumComponents {

	public boolean mLinkActive = false;

	protected YangList<NumberIO> mLinkedComponents = new YangList<NumberIO>();

	public LinkedNumComponents() {

	}

	public void valueChanged(NumberIO sender,double diff) {

	}

}
