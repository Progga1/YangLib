package yang.pc.tools.fontcreator;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FontCreater {
    
		/*
		// BELLIGERENT SETUP
		static String mFontName 			= "Belligerent Madness";
		private static String mFileFontName = "belligerent";
		private static int mOutputWidth 	= 512;
		private static int mOutputHeight 	= 512;
		
		private static int mAsciiStartID 	= 0x21; // "!"
		private static int mAsciiEndID 		= 0x7b; // "z"
		private static int mKernBoxes		= 8;
		private static int mFontSize		= 58;
		//*/
		
		//*
		// DEBUG FONT SETUP
		static String mFontName 			= "Myriad Pro Light";
		private static String mFileFontName = "console";
		private static int mOutputWidth 	= 256;
		private static int mOutputHeight 	= 128;
		
		private static int mAsciiStartID 	= 0x21; // "!"
		private static int mAsciiEndID 		= 0x7b; // "z"
		private static int mKernBoxes		= 1;
		private static int mFontSize		= 24;
		//*/
		
		private static String mPath 		= "C:"+File.separatorChar+"temp"+File.separatorChar;		
		private static String mFilename		= mPath+mFileFontName;
		private static int mDebug;	  
	
	    public static void main(String[] args) {
	    	
	    	if(args.length >= 3){
	    		mFontName 		= args[0];
	    		mOutputWidth 	= Integer.valueOf( args[1] );
	    		mOutputHeight 	= Integer.valueOf( args[2] );
	    		
	    	} else {
	    		System.out.println("To change settings give parameters: \n\t[1] font name e.g      : Arial\n\t[2] output width e.g.  : 512\n\t[3] output height e.g. : 512\n");
	    		System.out.flush();
	    		System.err.print("Use settings: " );
	    		System.err.print(mFontName + " , " + mOutputWidth + " x " + mOutputHeight);
	    		System.err.print(" (width x height [px])\n\n");
	    		System.out.flush();
	    	}
	    	
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	    }
	    
	    private static void createAndShowGUI() {
	    	
	        JFrame f = new JFrame("Swing Paint Demo");
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        FontDrawPanel panel = new FontDrawPanel();
	        panel.setParameters(mFontName, mOutputWidth, mOutputHeight, mAsciiStartID, mAsciiEndID, mKernBoxes, mFontSize, mFilename, mDebug);	
	        
	        
	        f.add(panel);
	        f.pack();
	        f.setVisible(true);
	    }
    }
    
    
