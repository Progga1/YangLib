package yang.pc.tools.runtimeproperties;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import yang.util.YangList;

public class InspectorFrame implements ActionListener {

	protected InspectorManager mManager;
	private JFrame mFrame;
	private JPanel mPanel;
	private JPanel mTopPanel;
	private JComboBox mObjectSelection;
	private BorderLayout mLayout;
	private InspectorPanel mActiveInspector = null;
	private YangList<ObjectAndInspector> mInspectedObjects;
	private HashMap<Class<?>,InspectorPanel> mDefaultInspectors = new HashMap<Class<?>,InspectorPanel>(32);
	private boolean mRefreshingLayout = false;

	private class ObjectAndInspector {

		String mNameOrAlias;
		String mOrigName;
		InspectionInterface mObject;
		InspectorPanel mInspector;

		@Override
		public String toString() {
			return mNameOrAlias +" ("+mOrigName+") - "+mObject;
		}

	}

	protected InspectorFrame(InspectorManager manager) {
		mLayout = new BorderLayout();
		mPanel = new JPanel();
		mPanel.setLayout(mLayout);
		mPanel.setPreferredSize(InspectorGUIDefinitions.INITIAL_DIMENSION);
		mInspectedObjects = new YangList<ObjectAndInspector>();
		mManager = manager;
		mObjectSelection = new JComboBox();
		mTopPanel = new JPanel();
		mTopPanel.add(mObjectSelection);
		mTopPanel.setLayout(new BoxLayout(mTopPanel,BoxLayout.PAGE_AXIS));
		mPanel.add(mTopPanel,BorderLayout.NORTH);
		mObjectSelection.addActionListener(this);
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

	public void setSize(int width,int height) {
		if(mFrame!=null)
			mFrame.setSize(width,height);
	}

	public void registerDefaultInspector(Class<?> objectType,InspectorPanel inspector) {
		mDefaultInspectors.put(objectType,inspector);
	}

	public boolean nameExists(String name) {
		for(ObjectAndInspector elem:mInspectedObjects) {
			if(name.equals(elem.mNameOrAlias))
				return true;
		}
		return false;
	}

	private void refreshName(ObjectAndInspector elem) {
		String name = elem.mOrigName;
		int i=0;
		elem.mNameOrAlias = null;
		while(nameExists(name)) {
			i++;
			name = elem.mOrigName+i;
		}
		elem.mNameOrAlias = name;
	}

	public void addObjectToInspect(InspectionInterface object,InspectorPanel inspector) {
		ObjectAndInspector elem = new ObjectAndInspector();
		elem.mObject = object;
		elem.mInspector = inspector;

		elem.mOrigName = object.getName();
		refreshName(elem);
		mInspectedObjects.add(elem);

		refreshLayout();
	}

	public void addObjectToInspect(InspectionInterface object) {
		Class<?> objType = object.getClass();
		InspectorPanel inspector = mDefaultInspectors.get(objType);
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
		if(mRefreshingLayout)
			return;
		boolean refreshAll = false;
		for(ObjectAndInspector elem:mInspectedObjects) {
			if(elem.mOrigName!=elem.mObject.getName()) {
				System.out.println("NAME CHANGED "+elem.mOrigName);
				refreshName(elem);
				refreshAll = true;
			}
		}
		if(refreshAll) {
			refreshLayout();
		}
		int selId = mObjectSelection.getSelectedIndex();
		if(selId<0 || selId>=mInspectedObjects.size()) {
			if(mActiveInspector!=null)
				mPanel.remove(mActiveInspector.getComponent());
			mActiveInspector = null;
			return;
		}
		ObjectAndInspector oi = mInspectedObjects.get(selId);
		InspectorPanel insp = oi.mInspector;
		if(insp!=mActiveInspector) {
			if(mActiveInspector!=null)
				mPanel.remove(mActiveInspector.getComponent());
			mPanel.add(insp.getComponent(),BorderLayout.CENTER);
			mActiveInspector = insp;
		}
		mActiveInspector.setValues(oi.mObject);
	}

	public void refreshLayout() {
		if(mRefreshingLayout)
			return;
		mRefreshingLayout = true;
		mObjectSelection.removeAllItems();
		for(ObjectAndInspector object:mInspectedObjects) {
			mObjectSelection.addItem(object.mNameOrAlias);
		}
		mRefreshingLayout = false;
	}

	public InspectorPanel createInspector() {
		return new InspectorPanel(this);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		refresh();
	}

}
