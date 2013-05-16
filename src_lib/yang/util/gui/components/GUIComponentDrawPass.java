package yang.util.gui.components;

import yang.graphics.defaults.DefaultGraphics;


public abstract class GUIComponentDrawPass<ComponentType extends GUIComponent> {

	public abstract void draw(DefaultGraphics<?> graphics,ComponentType component);
	
}
