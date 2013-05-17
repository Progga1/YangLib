package yang.pc.tools.fontcreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
		private static int mBorder;	  
	
	    public static void main(String[] args) {
	    	
	    	if(!loadArgs(args)) {
	    		System.out.println("Please give parameters: " +
	    				"\n\t[ 1] font name e.g        : Arial" +
	    				"\n\t[ 2] output name e.g      : MyFont" +
	    				"\n\t[ 3] generation loc e.g   : C:" +
	    				"\n\t[ 4] output width e.g.    : 512" +
	    				"\n\t[ 5] output height e.g.   : 512" +
	    				"\n\t[ 6] first ascii idx e.g. : 33  <- for '!'" +
	    				"\n\t[ 7] last  ascii idx e.g. : 123 <- for 'z'" +
	    				"\n\t[ 8] kern boxes e.g.      : 6" +
	    				"\n\t[ 9] font size e.g.       : 24" +
	    				"\n\t[10] extra border  e.g.   : 3");
	    		System.out.flush();
	    		//System.err.print("Use settings: " );
	    		//System.err.print(mFontName + " , " + mOutputWidth + " x " + mOutputHeight+ " , at: "+ mPath);
	    		//System.err.print(" (width x height [px])\n\n");
	    		System.out.flush();
	    		
	    		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	    	      String toSplit = null;

	    	      //  read the username from the command-line; need to use try/catch with the
	    	      //  readLine() method
	    	      try {
	    	         toSplit = br.readLine();
	    	      } catch (IOException ioe) {
	    	         System.out.println("Error while reading line!");
	    	         System.exit(1);
	    	      }
	    	      
	    	      args = toSplit.split(" ");
	    	      if(!loadArgs(args)) System.exit(1);
	    	}
	    	
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	    }
	    
	    private static boolean loadArgs(String[] args) {			
	    	if(args.length >= 10){
	    		mFontName 		= args[0];
	    		mFilename		= args[1];
	    		mPath		    = args[2];
	    		mOutputWidth 	= Integer.valueOf( args[3] );
	    		mOutputHeight 	= Integer.valueOf( args[4] );
	    		
	    		mAsciiStartID   = Integer.valueOf( args[5] );
	    		mAsciiEndID     = Integer.valueOf( args[6] );
	    		mKernBoxes		= Integer.valueOf( args[7] );
	    		mFontSize		= Integer.valueOf( args[8] );    		
	    		mBorder			= Integer.valueOf( args[9] );
	    		
	    		mFilename = mPath + File.separatorChar + mFilename;	    
	    		return true;
	    	}
			return false;
		}

		private static void createAndShowGUI() {
	    	
	        JFrame f = new JFrame("Swing Paint Demo");
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        FontDrawPanel panel = new FontDrawPanel();
	        panel.setParameters(mFontName, mOutputWidth, mOutputHeight, mAsciiStartID, mAsciiEndID, mKernBoxes, mFontSize, mFilename, mBorder, mDebug);	
	        
	        
	        f.add(panel);
	        f.pack();
	        f.setVisible(true);
	    }
    }
    
    
