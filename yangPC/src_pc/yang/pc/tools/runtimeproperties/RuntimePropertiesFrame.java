package yang.pc.tools.runtimeproperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import yang.util.YangList;

public class RuntimePropertiesFrame {

	protected RuntimePropertiesManager mManager;
	private JFrame mFrame;
	private JPanel mPanel;
	private JPanel mTopPanel;
	private JComboBox mObjectSelection;
	private BorderLayout mLayout;
	private RuntimePropertiesInspector mActiveInspector = null;
	private YangList<ObjectAndInspector> mInspectedObjects;
	private HashMap<Class<?>,RuntimePropertiesInspector> mDefaultInspectors = new HashMap<Class<?>,RuntimePropertiesInspector>(32);

	private class ObjectAndInspector {

		PropertyInterface mObject;
		RuntimePropertiesInspector mInspector;

	}

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

	public void registerDefaultInspector(Class<?> objectType,RuntimePropertiesInspector inspector) {
		mDefaultInspectors.put(objectType,inspector);
	}

	public void addObjectToInspect(PropertyInterface object,RuntimePropertiesInspector inspector) {
		ObjectAndInspector elem = new ObjectAndInspector();
		elem.mObject = object;
		elem.mInspector = inspector;
		mInspectedObjects.add(elem);

		refreshLayout();
	}

	public void addObjectToInspect(PropertyInterface object) {
		Class<?> objType = object.getClass();
		RuntimePropertiesInspector inspector = mDefaultInspectors.get(objType);
		while(inspector==null && objType.getSuperclass()!=null) {
			objType = objType.getSuperclass();
			inspector = mDefaultInspectors.get(objType);
		}
		if(inspector==null)
			throw new RuntimeException("No default inspector for type: "+object.getClass());
		addObjectToInspect(object,inspector);
	}

	public void refresh() {
		if(mFrame!=null && !mFrame.isVisible())
			return;
		if(mInspectedObjects.size()==0)
			return;
		ObjectAndInspector oi = mInspectedObjects.get(0);
		RuntimePropertiesInspector insp = oi.mInspector;
		if(insp!=mActiveInspector) {
			if(mActiveInspector!=null)
				mPanel.remove(mActiveInspector.getPanel());
			mPanel.add(insp.getPanel(),BorderLayout.CENTER);
			mActiveInspector = insp;
		}
		mActiveInspector.setValues(oi.mObject);
	}

	public void refreshLayout() {

		refresh();
	}

	public RuntimePropertiesInspector createInspector() {
		return new RuntimePropertiesInspector(this);
	}

}
