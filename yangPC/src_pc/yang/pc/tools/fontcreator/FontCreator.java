package yang.pc.tools.fontcreator;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FontCreator {
    
		static final int DEFAULT_KERN_BOXES = 8;
	
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
		private static int mBorderHorizontal;
		private static int mBorderVertical;
		public static int mFontStyle = java.awt.Font.PLAIN;	  
		private static int paramCount;

		private static LinkedList<Integer> mCharList;

		private static Integer[] mCharIndicesToRender;

		private static boolean mReplaceUndefinedChars;
	
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
	    	
	    	mCharList = new LinkedList<Integer>();
	    	
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
	    		printParam("resolution wxh","1024x1024");
	    		printParam("font size","112 @resolution=1024");
	    		printParam("extra border x:y","9:7 @fontSize=112");
	    		printParam("kern boxes",""+DEFAULT_KERN_BOXES);
	    		printParam("ascii range","33-122");
	    		printParam("generation loc","workspace");
	    		printParam("output name","font name + style");
	    		printParam("replace undef. chars","false, use \"true\" to activate");
	    		System.out.println("\nPlease enter:");
	    		System.out.flush();
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
	    	mKernBoxes = DEFAULT_KERN_BOXES;
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
	    			mFontSize 		= (int)((float)mOutputWidth/1024*(mFontStyle&(Font.ITALIC | Font.BOLD))!=0?112:124);
	    		
//	    		if(args.length>=4) {
//	    			String style = args[3].trim().toUpperCase();
//	    			if(style.equals("BOLD"))
//	    				mFontStyle = Font.BOLD;
//	    			if(style.equals("ITALIC"))
//	    				mFontStyle = Font.ITALIC;
//	    			if(style.equals("ITALIC_BOLD") || style.equals("BOLD_ITALIC"))
//	    				mFontStyle = Font.BOLD | Font.ITALIC;
//	    		}
	    		
	    		if(args.length>=4) {
	    			String[] borders = args[3].split(":");
	    			mBorderHorizontal			= Integer.valueOf( borders[0] );
	    			if(borders.length>1) 
	    				mBorderVertical 		= Integer.valueOf( borders[1] );
	    			else
	    				mBorderVertical			= (int)(mBorderHorizontal*0.9f);
	    		}else{
	    			mBorderHorizontal			= (int)((float)mFontSize/112*((mFontStyle&Font.ITALIC)!=0?11:9));
	    			mBorderVertical				= (int)((float)mFontSize/112*7);
	    		}
	    		
	    		if(args.length>=5)
	    			mKernBoxes		= Integer.valueOf( args[4] );
	    		
	    		
	    		if(args.length>= 6) {
	    			
	    			String[] arrays = args[5].split(",");
	    			
	    			for(String array: arrays) {
		    			res = array.split("-");
			    		mAsciiStartID   = Integer.valueOf( res[0] );
			    		if(res.length>1)
			    			mAsciiEndID     = Integer.valueOf( res[1] );
			    		else {
			    			mAsciiEndID     = Integer.valueOf( res[0] );
			    		}
			    			
			    		for(int i = mAsciiStartID; i<= mAsciiEndID; i++) {
			    			Integer toAdd = new Integer(i);
			    			
							if(!mCharList.contains(toAdd))
								mCharList.add(toAdd);
			    		}	
	    			}	    			
	    			
	    			mCharIndicesToRender = mCharList.toArray(new Integer[0]);
	    			
	    		}
	    		if(args.length>=7) {
	    			mPath		    = args[6];
	    		}
	    		if(args.length>=8) {
	    			mFilename = args[7];
	    		} if(args.length>= 9) {
	    			mReplaceUndefinedChars = args[8].equals("true");
	    			
	    			
	    		} else{
	    			mFilename = fontName[0].trim().toLowerCase();
	    			if((mFontStyle & Font.BOLD)!=0)
	    				mFilename += "_bold";
	    			if((mFontStyle & Font.ITALIC)!=0)
	    				mFilename += "_italic";
	    		}
	    		
	    		mFilename = mPath + File.separatorChar + mFilename;
	    		mArgs = args[0] + " " + mOutputWidth+"x"+mOutputHeight + " " + mFontSize + " " + mBorderHorizontal+":"+mBorderVertical + " " + mKernBoxes + " " + mAsciiStartID+"-"+mAsciiEndID + " ";
	    		return true;
	    	}
	    	
			return false;
		}

		private static void createAndShowGUI() {
	    	
	        JFrame f = new JFrame("Swing Paint Demo");
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        FontDrawPanel panel = new FontDrawPanel();
	        panel.setParameters(mArgs, mFontName, mOutputWidth, mOutputHeight, mCharIndicesToRender, mKernBoxes, mFontSize, mFilename, mBorderHorizontal, mBorderVertical, mDebug, mReplaceUndefinedChars);	
	        
	        f.add(panel);
	        f.pack();
	        f.setVisible(true);
	    }
    }
    
    
