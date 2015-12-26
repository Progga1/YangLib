package yang.pc.tools.runtimeproperties;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class InspectorGUIDefinitions {

	public final static Color CL_LABEL_BACKGROUND = new Color(225,225,225);
	public final static Color CL_VALUE_DEFAULT_BACKGROUND = new Color(236,236,236);
	public final static Color CL_UNUSED_SPACE = new Color(242,242,242);
	public final static Color CL_OUTLINE = new Color(50,50,50);

	public static final int PADDING = 6;
	public static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING);
	public static final Border PROPERTY_BORDER = BorderFactory.createMatteBorder(0,0,1,0, CL_OUTLINE);

//	public static final Border PROPERTY_BORDER = BorderFactory.createCompoundBorder(PADDING_BORDER,OUTLINE_BORDER);

}
