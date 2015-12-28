package yang.pc.tools.runtimeinspectors;

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
	private JPanel mMainPanel;
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
		mMainPanel = new JPanel();
		mMainPanel.setLayout(mLayout);
		mMainPanel.setPreferredSize(InspectorGUIDefinitions.INITIAL_DIMENSION);
		mInspectedObjects = new YangList<ObjectAndInspector>();
		mManager = manager;
		mObjectSelection = new JComboBox();
		mTopPanel = new JPanel();
		mTopPanel.add(mObjectSelection);
		mTopPanel.setLayout(new BoxLayout(mTopPanel,BoxLayout.PAGE_AXIS));
		mMainPanel.add(mTopPanel,BorderLayout.NORTH);
		mObjectSelection.addActionListener(this);
	}

	public void setFramed() {
		mFrame = new JFrame();
		mFrame.add(mMainPanel);
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
		int selId = mObjectSelection.getSelectedIndex()-1;
		boolean objSel = selId>-1;
		ObjectAndInspector oi = objSel?mInspectedObjects.get(selId):null;
		InspectorPanel insp = objSel?oi.mInspector:null;
		boolean changedObject = insp!=mActiveInspector;
		if(changedObject) {
			if(mActiveInspector!=null)
				mMainPanel.remove(mActiveInspector.getComponent());
			if(insp!=null)
				mMainPanel.add(insp.getComponent(),BorderLayout.CENTER);
			mActiveInspector = insp;
		}

		if(mActiveInspector!=null)
			mActiveInspector.setValuesByObject(oi.mObject);

		if(changedObject)
			mFrame.setVisible(true);
		mFrame.repaint();
	}

	public void refreshLayout() {
		if(mRefreshingLayout)
			return;
		mRefreshingLayout = true;
		mObjectSelection.removeAllItems();
		mObjectSelection.addItem("<none>");
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
