package yang.model;

import java.io.IOException;

public class DebugYang {

	public static boolean AUTO_RECORD_MACRO = true;

	public static boolean FORCE_FULLSCREEN = false;
	
	public static int debugLevel = 1;
	public static boolean drawKerning = false;
	public static boolean showStart = false;
	public static boolean drawTails = true;
	public static boolean DRAW_GFX_VALUES = false;
	
	public static String stateString = null;
	
	public static void showStackTrace() {
		try{
			throw new RuntimeException();
		}catch(RuntimeException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void showStackTrace(String msg, int depth) {
		StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
		int currentIndex = -1;
		for (int i = 0; i < stackTraceElement.length; i++) {
			if (stackTraceElement[i].getMethodName().compareTo("showStackTrace") == 0) {
				currentIndex = i + 1;
				break;
			}
		}

		int start = Math.max(0, currentIndex+1);
		int end = Math.min(stackTraceElement.length, depth-1+start);
		
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
		if(debugLevel>=level)
			System.out.println(message);
	}
	
	public static void println(Object message) {
		println(message,1);
	}
	
	public static void printerr(Object message,int level) {
		if(debugLevel>=level)
			System.err.println(message);
	}
	
	public static void printerr(Object message) {
		printerr(1);
	}

	public static void exception(IOException e) {
		if(debugLevel>=1) {
			e.printStackTrace();
		}
	}
	
	public static void stateString(Object object) {
		stateString = object.toString();
	}
	
	public static void stateString(int i) {
		stateString = ""+i;
	}
	
	public static void stateString(float f) {
		stateString = ""+f;
	}
	
	public static void stateString(boolean b) {
		stateString = ""+b;
	}
	
	public static void appendState(Object object) {
		if(stateString==null)
			stateString = "";
		stateString += object;
	}
	
	public static void appendState(int i) {
		appendState(""+i);
	}
	
	public static void appendState(float f) {
		appendState(""+f);
	}
	
	public static void appendState(boolean b) {
		appendState(""+b);
	}
	
	public static void clearState() {
		stateString = null;
	}
	
}
