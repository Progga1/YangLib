package yang.pc.tools.runtimeproperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import yang.util.YangList;

public class RuntimePropertiesFrame {

	private RuntimePropertiesManager mManager;
	private JFrame mFrame;
	private JPanel mPanel;
	private BorderLayout mLayout;
	private RuntimePropertiesInspector mActiveInspector = null;

	private class ObjectAndInspector {

		PropertyInterface mObject;
		RuntimePropertiesInspector mInspector;

	}

	private YangList<ObjectAndInspector> mInspectedObjects;

	protected RuntimePropertiesFrame(RuntimePropertiesManager manager) {
		mLayout = new BorderLayout();
		mPanel = new JPanel();
		mPanel.setLayout(mLayout);
		mPanel.setPreferredSize(new Dimension(360,600));
		mInspectedObjects = new YangList<ObjectAndInspector>();
		mManager = manager;
	}

	public void setFramed() {
		mFrame = new JFrame();
		mFrame.add(mPanel);
		mFrame.pack();
		mFrame.setTitle("Inspector");
		mFrame.setVisible(true);
	}

	public void setFrameTitle(String title) {
		mFrame.setTitle(title);
	}

	public void addObjectToInspect(PropertyInterface object,RuntimePropertiesInspector inspector) {
		ObjectAndInspector elem = new ObjectAndInspector();
		elem.mObject = object;
		elem.mInspector = inspector;
		mInspectedObjects.add(elem);

		refreshLayout();
	}

	public void refreshSelection() {
		if(mInspectedObjects.size()==0)
			return;
		ObjectAndInspector oi = mInspectedObjects.get(0);
		RuntimePropertiesInspector insp = oi.mInspector;
		if(insp!=mActiveInspector) {
			if(mActiveInspector!=null)
				mPanel.remove(mActiveInspector.getPanel());
			mPanel.add(insp.getPanel(),BorderLayout.CENTER);
			mPanel.setBackground(Color.cyan);
			mActiveInspector = insp;
		}
		mActiveInspector.setValues(oi.mObject);
	}

	public void refreshLayout() {

		refreshSelection();
	}

}
