package yang.pc.tools.fontcreator;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import yang.util.Util;

public class FontCreator {
    
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
		private static String mArgs 		= null;
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
		public static int mFontStyle = java.awt.Font.PLAIN;	  
		private static int paramCount;
	
		public static void printParam(String param,String defaultVal) {
			System.out.print((++paramCount)+". ");
			while(param.length()<26)
				param += " ";
			System.out.print(param);
			if(defaultVal!=null)
				System.out.print("[DEFAULT = "+defaultVal+"]");
			System.out.print("\n");
		}
		
	    public static void main(String[] args) {
	    	
	    	if(!loadArgs(args)) {
//	    		System.out.println("Please give parameters: " +
//	    				"\n\t[ 1] font name e.g        : Arial" +
//	    				"\n\t[ 2] output name e.g      : MyFont" +
//	    				"\n\t[ 3] generation loc e.g   : C:" +
//	    				"\n\t[ 4] output width e.g.    : 512" +
//	    				"\n\t[ 5] output height e.g.   : 512" +
//	    				"\n\t[ 8] kern boxes e.g.      : 6" +
//	    				"\n\t[ 9] font size e.g.       : 24" +
//	    				"\n\t[10] extra border  e.g.   : 3" +
//						"\n\t[ 6] first ascii idx e.g. : 33  (for '!')" +
//						"\n\t[ 7] last  ascii idx e.g. : 123 (for 'z')"
//	    				);
	    		paramCount = 0;
	    		System.out.println("----PARAMETERS----");
	    		printParam("font name : style (e.g. 'Arial:bold_italic'",null);
	    		printParam("resolution","1024x1024");
	    		printParam("font size","112 @resolution=1024");
	    		//printParam("font style (bold,italic)","plain");
	    		printParam("extra border","7 @fontSize=112");
	    		printParam("kern boxes","6");
	    		printParam("ascii range","23-123");
	    		printParam("generation loc","workspace");
	    		printParam("output name","font name + style");
	    		System.out.println("\nPlease enter:");
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
	    	mAsciiStartID = 33;
	    	mAsciiEndID = 123;
	    	mKernBoxes = 6;
	    	mFontStyle = Font.PLAIN;
	    	mPath = "../../";
	    	
	    	if(args.length >= 1){
	    		String[] fontName = args[0].split(":");
	    		mFontName 		= fontName[0].replace("_", " ");
	    		
	    		if(fontName.length>1) {
	    			String style = fontName[1].trim().toUpperCase();
	    			if(style.equals("BOLD"))
	    				mFontStyle = Font.BOLD;
	    			if(style.equals("ITALIC"))
	    				mFontStyle = Font.ITALIC;
	    			if(style.equals("ITALIC_BOLD") || style.equals("BOLD_ITALIC"))
	    				mFontStyle = Font.BOLD | Font.ITALIC;
	    		}
	    		
	    		String[] res;
	    		if(args.length>=2)
	    			res = args[1].split("x");
	    		else
	    			res = new String[]{"1024"};
	    		mOutputWidth = Integer.valueOf( res[0] );
	    		if(res.length>1)
	    			mOutputHeight 	= Integer.valueOf( res[1] );
	    		else
	    			mOutputHeight = mOutputWidth;
	    		
	    		if(args.length>=3)
	    			mFontSize		= Integer.valueOf( args[2] );
	    		else
	    			mFontSize 		= (int)((float)mOutputWidth/1024*(mFontStyle&Font.BOLD)!=0?112:124);
	    		
//	    		if(args.length>=4) {
//	    			String style = args[3].trim().toUpperCase();
//	    			if(style.equals("BOLD"))
//	    				mFontStyle = Font.BOLD;
//	    			if(style.equals("ITALIC"))
//	    				mFontStyle = Font.ITALIC;
//	    			if(style.equals("ITALIC_BOLD") || style.equals("BOLD_ITALIC"))
//	    				mFontStyle = Font.BOLD | Font.ITALIC;
//	    		}
	    		
	    		if(args.length>=4)
	    			mBorder			= Integer.valueOf( args[3] );
	    		else
	    			mBorder			= (int)((float)mFontSize/112*((mFontStyle&Font.ITALIC)!=0?10:7));
	    		
	    		if(args.length>=5)
	    			mKernBoxes		= Integer.valueOf( args[4] );
	    		
	    		
	    		if(args.length>= 6) {
	    			res = args[5].split("-");
		    		mAsciiStartID   = Integer.valueOf( res[0] );
		    		mAsciiEndID     = Integer.valueOf( res[1] );
	    		}
	    		if(args.length>=7) {
	    			mPath		    = args[6];
	    		}
	    		if(args.length>=8) {
	    			mFilename = args[7];
	    		}else{
	    			mFilename = fontName[0].trim().toLowerCase();
	    			if((mFontStyle & Font.BOLD)!=0)
	    				mFilename += "_bold";
	    			if((mFontStyle & Font.ITALIC)!=0)
	    				mFilename += "_italic";
	    		}
	    		
	    		mFilename = mPath + File.separatorChar + mFilename;
	    		mArgs = args[0] + " " + mOutputWidth+"x"+mOutputHeight + " " + mFontSize + " " + mBorder + " " + mKernBoxes + " " + mAsciiStartID+"-"+mAsciiEndID + " ";
	    		return true;
	    	}
	    	
			return false;
		}

		private static void createAndShowGUI() {
	    	
	        JFrame f = new JFrame("Swing Paint Demo");
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        FontDrawPanel panel = new FontDrawPanel();
	        panel.setParameters(mArgs, mFontName, mOutputWidth, mOutputHeight, mAsciiStartID, mAsciiEndID, mKernBoxes, mFontSize, mFilename, mBorder, mDebug);	
	        
	        
	        f.add(panel);
	        f.pack();
	        f.setVisible(true);
	    }
    }
    
    
