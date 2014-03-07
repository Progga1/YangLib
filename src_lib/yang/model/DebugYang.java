package yang.model;

import java.io.IOException;

public class DebugYang {


	public static boolean AUTO_RECORD_MACRO = true;

	public static boolean FORCE_FULLSCREEN = false;

	public static int DEBUG_LEVEL = 1;
	public static int curStateStringDebugLevel = 1;
	public static boolean showStart = false;
	public static boolean drawTails = true;
	public static boolean DRAW_GFX_VALUES = false;
	public static boolean DRAW_POINTERS = false;
	public static String PLAY_MACRO_FILENAME = null;

	public static String stateString = null;

	public static boolean useAltForMiddleButton = false;
	public static boolean useCtrlForRightButton = false;

	public static void setRelease() {
		DEBUG_LEVEL = 0;
		DRAW_GFX_VALUES = false;
	}

	public static void showStackTrace() {
		try{
			throw new RuntimeException();
		}catch(final RuntimeException ex) {
			ex.printStackTrace();
		}
	}

	public static void showStackTrace(String msg, int depth) {
		final StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
		int currentIndex = -1;
		for (int i = 0; i < stackTraceElement.length; i++) {
			if (stackTraceElement[i].getMethodName().compareTo("showStackTrace") == 0) {
				currentIndex = i + 1;
				break;
			}
		}

		final int start = Math.max(0, currentIndex+1);
		final int end = Math.min(stackTraceElement.length, depth-1+start);

		String fullClassName = stackTraceElement[currentIndex].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		String methodName = stackTraceElement[currentIndex].getMethodName();
		int lineNumber = stackTraceElement[currentIndex].getLineNumber();
		String trace = "	at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")";
		System.out.println(msg + " " + trace);

		for (int i = start; i < end; i++) {
        	fullClassName = stackTraceElement[i].getClassName();
        	className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        	methodName = stackTraceElement[i].getMethodName();
        	lineNumber = stackTraceElement[i].getLineNumber();
        	if (lineNumber < 0) {
        		trace = "	at " + fullClassName + "." + methodName + "(Unknown Source)";
        	} else {
        		trace = "	at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")";
        	}
            System.out.println(trace);
        }
    }

	public static void println(Object message,int level) {
		if(DEBUG_LEVEL>=level)
			System.out.println(message);
	}

	public static void println(Object message) {
		println(message,1);
	}

	public static void printerr(Object message,int level) {
		if(DEBUG_LEVEL>=level)
			System.err.println(message);
	}

	public static void printerr(Object message) {
		printerr(message,1);
	}

	public static void exception(IOException e) {
		if(DEBUG_LEVEL>=1) {
			e.printStackTrace();
		}
	}

	public static void stateString(Object object) {
		if(DEBUG_LEVEL>=curStateStringDebugLevel)
			stateString = object.toString();
	}

	public static void stateString(int i) {
		if(DEBUG_LEVEL>=curStateStringDebugLevel)
			stateString = ""+i;
	}

	public static void stateString(float f) {
		if(DEBUG_LEVEL>=curStateStringDebugLevel)
			stateString = ""+f;
	}

	public static void stateString(boolean b) {
		if(DEBUG_LEVEL>=curStateStringDebugLevel)
			stateString = ""+b;
	}

	public static void appendState(Object object) {
		if(DEBUG_LEVEL>=curStateStringDebugLevel) {
			if(stateString==null)
				stateString = "";
			stateString += object;
		}
	}

	public static void appendStateLn(Object object) {
		if(DEBUG_LEVEL>=curStateStringDebugLevel) {
			if(stateString==null)
				stateString = "";
			stateString += object +"\n";
		}
	}

	public static void appendStateLn() {
		if(DEBUG_LEVEL>=curStateStringDebugLevel) {
			if(stateString==null)
				stateString = "";
			stateString += "\n";
		}
	}

//	public static void appendState(int i) {
//		if(DEBUG_LEVEL>=curStateStringDebugLevel)
//			appendState(""+i);
//	}
//
//	public static void appendState(float f) {
//		if(DEBUG_LEVEL>=curStateStringDebugLevel)
//			appendState(""+f);
//	}
//
//	public static void appendState(boolean b) {
//		if(DEBUG_LEVEL>=curStateStringDebugLevel)
//			appendState(""+b);
//	}

	public static void clearState() {
		stateString = null;
	}

}
