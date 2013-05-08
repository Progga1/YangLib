package yang.util.gui.components;

import yang.events.eventtypes.YangPointerEvent;
import yang.util.NonConcurrentList;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUIPointerEvent;

public class GUIContainer extends RectangularInteractiveGUIComponent {

	protected NonConcurrentList<GUIComponent> mAllComponents;
	protected NonConcurrentList<InteractiveGUIComponent> mInteractiveComponents;
	public InteractiveGUIComponent mPressedComponent;
	
	public GUIContainer() {
		super(1024,1024);
		mInteractiveComponents = new NonConcurrentList<InteractiveGUIComponent>();
		mAllComponents = new NonConcurrentList<GUIComponent>();
	}
	
	public void rawPointerEvent(GUIPointerEvent pointerEvent) {

			float x = pointerEvent.mX;
			float y = pointerEvent.mY;
			if(pointerEvent.mAction==YangPointerEvent.ACTION_POINTERDRAG && mPressedComponent!=null) {
				GUIPointerEvent guiEvent = BasicGUI.mGUIEventPool[BasicGUI.mComponentPoolPos++];
				if(BasicGUI.mComponentPoolPos>BasicGUI.mGUIEventPool.length)
					BasicGUI.mComponentPoolPos = 0;
				guiEvent.createFromPointerEvent(pointerEvent,mPressedComponent);
				mPressedComponent.guiFocusedDrag(guiEvent);
			}else{
				for(InteractiveGUIComponent component:mInteractiveComponents) {
					if(component.mVisible && component.mEnabled && component.inArea(x, y)) {
						GUIPointerEvent guiEvent = BasicGUI.mGUIEventPool[BasicGUI.mComponentPoolPos++];
						if(BasicGUI.mComponentPoolPos>=BasicGUI.mGUIEventPool.length)
							BasicGUI.mComponentPoolPos = 0;
						guiEvent.createFromPointerEvent(pointerEvent, component);
						component.rawPointerEvent(guiEvent);
						guiEvent.handle(component);

						if(pointerEvent.mAction==YangPointerEvent.ACTION_POINTERDOWN) {
							if(component.isPressable()) {
								mPressedComponent = component;
								break;
							}
						}
						
						if(pointerEvent.mAction==YangPointerEvent.ACTION_POINTERUP) {
							if(mPressedComponent==component)
								component.guiClick(guiEvent);
							mPressedComponent = null;
							break;
						}
						return;
					}
				}
				super.rawPointerEvent(pointerEvent);
			}
	}
	
	@Override
	public void refreshProjections(float offsetX,float offsetY) {
		super.refreshProjections(offsetX,offsetY);
		for(GUIComponent component:mAllComponents) {
			component.refreshProjections(mPosX+offsetX, mPosY+offsetY);
		}
		
	}
	
	@Override
	public void draw() {
		for(GUIComponent component:mAllComponents) {
			if(component.mVisible)
				component.draw();
		}
	}
	
	public <ComponentType extends GUIComponent> ComponentType addComponent(ComponentType component) {
		if(component instanceof InteractiveGUIComponent) {
			InteractiveGUIComponent interComponent = (InteractiveGUIComponent)component;
			mInteractiveComponents.add(interComponent);
			if(mActionListener!=null)
				interComponent.setActionListener(mActionListener);
			if(mPointerListener!=null)
				interComponent.setPointerListener(mPointerListener);
		}
		mAllComponents.add(component);
		component.setGUI(mGUI);
		return component;
	}
	
}