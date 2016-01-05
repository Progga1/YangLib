package yang.pc.tools.runtimeinspectors;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class InspectorGUIDefinitions {

	public final static Dimension INITIAL_DIMENSION = new Dimension(480,600);
	public final static int DEFAULT_CAPTION_WIDTH = 200;
	public final static int DEFAULT_COMPONENT_HEIGHT = 26;

	public final static Color CL_LABEL_BACKGROUND = new Color(220,220,220);
	public final static Color CL_CHAIN_COMPONENT_OUTLINE = new Color(100,100,100);
	public final static Color CL_CHAIN_COMPONENT_CAPTION_BACKGROUND = new Color(120,120,120);
	public final static Color CL_CHAIN_COMPONENT_CAPTION_FONT = new Color(240,240,240);
	public final static Color CL_COMPONENT_DEFAULT_BACKGROUND = new Color(236,236,236);
	public final static Color CL_UNUSED_SPACE = new Color(242,242,242);
	public final static Color CL_COMPONENT_SPLITTER = new Color(160,160,160);
	public final static Color CL_DEFAULT_COMPONENT_OUTLINE = new Color(100,100,100);
	public final static Color CL_WEAK_OUTLINE = new Color(190,190,190);
	public final static Color CL_SCROLL_WIDGET = new Color(190,190,210);
	public static final Color CL_LINKED = new Color(60,60,140);
	public static final Color CL_UNLINKED = new Color(170,170,190);
	public static final Color CL_LABEL = new Color(20,20,20);
	public static final Color CL_TEXT = new Color(60,60,60);

	public static final Color CL_BUTTON_BACKGROUND = new Color(215,215,215);
	public static final Color CL_BUTTON_FONT = new Color(30,30,30);
	public static final Color CL_BUTTON_FONT_DISABLED = new Color(110,110,110);
	public static final Color CL_BUTTON_OUTLINE = new Color(130,130,130);
	public static final Color CL_BUTTON_INNER_LINE = new Color(180,180,180);

	public static final int PADDING = 6;
	public static final int COMPONENT_PADDING = 3;
	public static final Border BORDER_PADDING = BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING);
	public static final Border BORDER_PROPERTY = BorderFactory.createMatteBorder(0,0,1,0, CL_COMPONENT_SPLITTER);
	public static final Border BORDER_SUB_PROPERTY = BorderFactory.createMatteBorder(0,0,1,0, CL_WEAK_OUTLINE);
	public static final Border BORDER_COMPONENT_DEFAULT = BorderFactory.createEmptyBorder(COMPONENT_PADDING,COMPONENT_PADDING,COMPONENT_PADDING,COMPONENT_PADDING);
	public static final Border BORDER_TEXT_FIELD = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,1,1,1,CL_DEFAULT_COMPONENT_OUTLINE),BorderFactory.createEmptyBorder(1,1,1,1));
	public static final Border BORDER_CHAIN_COMPONENT = BorderFactory.createMatteBorder(1,2,1,2, CL_CHAIN_COMPONENT_OUTLINE);
	public static final Border BORDER_CHAIN_COMPONENT_CAPTION = BorderFactory.createEmptyBorder(3,4,4,4);
	public static final Border BORDER_BUTTON = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1,1,1,1, CL_BUTTON_OUTLINE),BorderFactory.createMatteBorder(1,1,1,1, CL_BUTTON_INNER_LINE));

}
