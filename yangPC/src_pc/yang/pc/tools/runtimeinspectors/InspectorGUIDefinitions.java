package yang.pc.tools.runtimeinspectors;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class InspectorGUIDefinitions {

	public final static Dimension INITIAL_DIMENSION = new Dimension(480,600);
	public final static int DEFAULT_CAPTION_WIDTH = 200;
	public final static int DEFAULT_COMPONENT_HEIGHT = 26;

	public final static Color CL_LABEL_BACKGROUND = new Color(215,215,215);
	public final static Color CL_CHAIN_COMPONENT_OUTLINE = new Color(100,100,100);
	public final static Color CL_CHAIN_COMPONENT_CAPTION_BACKGROUND = new Color(120,120,120);
	public final static Color CL_CHAIN_COMPONENT_CAPTION_FONT = new Color(240,240,240);
	public final static Color CL_VALUE_DEFAULT_BACKGROUND = new Color(236,236,236);
	public final static Color CL_UNUSED_SPACE = new Color(242,242,242);
	public final static Color CL_COMPONENT_SPLITTER = new Color(160,160,160);
	public final static Color CL_DEFAULT_COMPONENT_OUTLINE = new Color(100,100,100);
	public final static Color CL_WEAK_OUTLINE = new Color(190,190,190);
	public final static Color CL_SCROLL_WIDGET = new Color(190,190,210);
	public static final Color CL_LINKED = new Color(70,70,140);
	public static final Color CL_UNLINKED = new Color(180,180,200);
	public static final Color CL_LABEL = new Color(20,20,20);

	public static final int PADDING = 6;
	public static final int COMPONENT_PADDING = 3;
	public static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING);
	public static final Border PROPERTY_BORDER = BorderFactory.createMatteBorder(0,0,1,0, CL_COMPONENT_SPLITTER);
	public static final Border SUB_PROPERTY_BORDER = BorderFactory.createMatteBorder(0,0,1,0, CL_WEAK_OUTLINE);
	public static final Border COMPONENT_DEFAULT_BORDER = BorderFactory.createEmptyBorder(COMPONENT_PADDING,COMPONENT_PADDING,COMPONENT_PADDING,COMPONENT_PADDING);
	public static final Border TEXT_FIELD_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,1,1,1,CL_DEFAULT_COMPONENT_OUTLINE),BorderFactory.createEmptyBorder(1,1,1,1));
	public static final Border CHAIN_COMPONENT_BORDER = BorderFactory.createMatteBorder(1,2,1,2, CL_CHAIN_COMPONENT_OUTLINE);
//	public static final Border CHAIN_COMPONENT_CAPTION_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,1,1,1,CL_WEAK_OUTLINE),BorderFactory.createEmptyBorder(4,4,4,4));
	public static final Border CHAIN_COMPONENT_CAPTION_BORDER = BorderFactory.createEmptyBorder(3,4,4,4);
//	public static final Border PROPERTY_BORDER = BorderFactory.createCompoundBorder(PADDING_BORDER,OUTLINE_BORDER);

}
