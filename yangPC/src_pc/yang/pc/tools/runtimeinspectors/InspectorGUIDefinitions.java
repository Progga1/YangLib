package yang.pc.tools.runtimeinspectors;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class InspectorGUIDefinitions {

	public final static Dimension INITIAL_DIMENSION = new Dimension(400,600);
	public final static int DEFAULT_CAPTION_WIDTH = 150;
	public final static int DEFAULT_COMPONENT_HEIGHT = 26;

	public final static Color CL_LABEL_BACKGROUND = new Color(225,225,225);
	public final static Color CL_VALUE_DEFAULT_BACKGROUND = new Color(236,236,236);
	public final static Color CL_UNUSED_SPACE = new Color(242,242,242);
	public final static Color CL_OUTLINE = new Color(50,50,50);
	public final static Color CL_WEAK_OUTLINE = new Color(190,190,190);
	public final static Color CL_SCROLL_WIDGET = new Color(190,190,210);

	public static final int PADDING = 6;
	public static final int COMPONENT_PADDING = 3;
	public static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING);
	public static final Border PROPERTY_BORDER = BorderFactory.createMatteBorder(0,0,1,0, CL_OUTLINE);
	public static final Border SUB_PROPERTY_BORDER = BorderFactory.createMatteBorder(0,0,1,0, CL_WEAK_OUTLINE);
	public static final Border COMPONENT_PADDING_BORDER = BorderFactory.createEmptyBorder(COMPONENT_PADDING,COMPONENT_PADDING,COMPONENT_PADDING,COMPONENT_PADDING);
	public static final Border TEXT_FIELD_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,1,1,1,CL_OUTLINE),BorderFactory.createEmptyBorder(1,1,1,1));

//	public static final Border PROPERTY_BORDER = BorderFactory.createCompoundBorder(PADDING_BORDER,OUTLINE_BORDER);

}
