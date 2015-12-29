package yang.pc.tools.runtimeinspectors;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import yang.util.YangList;

public class PropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected InspectorPanel mInspector;
	protected YangList<InspectorItem> mItems = new YangList<InspectorItem>();

	public PropertiesPanel(InspectorPanel inspector) {
		mInspector = inspector;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	}

//	public void refreshLayout() {
//		this.removeAll();
//		for(InspectorProperty item:mItems) {
//			this.add(item);
//		}
//	}

	public YangList<InspectorItem> getItems() {
		return mItems;
	}

	public void add(InspectorItem property) {
		mItems.add(property);
		super.add(property);
//		refreshLayout();
	}

	public InspectorItem add(InspectorComponent component) {
		InspectorItem newItem = new InspectorItem(mInspector,component);
		add(newItem);
		return newItem;
	}

}
