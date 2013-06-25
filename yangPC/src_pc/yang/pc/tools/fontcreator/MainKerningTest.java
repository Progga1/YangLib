package yang.pc.tools.fontcreator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MainKerningTest extends JFrame {
	private JTextField mTextField;
	private MyPanel mPanel;
	private int mWidth;
	private int mHeight;
	private BufferedImage mCanvas;
	private SampleFont mFont;
	BufferedImage mTexture; 

	public MainKerningTest() {
        setTitle("Kerning test");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initUI();
        
        mWidth = 300;
        mHeight = 300;
        mFont = new SampleFont();
        
        
        try {
            mTexture = ImageIO.read(new File(mFont.texture));
        } catch (IOException e) {
        	System.err.println("Texture not found"); 
        }
        
     }

     public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 MainKerningTest ex = new MainKerningTest();
                 ex.setVisible(true);
             }
         });
     }
     
     public final void initUI() {

         mPanel = new MyPanel();
         getContentPane().add(mPanel);

         mPanel.setLayout(null);

         JButton quitButton = new JButton("Update");
         quitButton.setBounds(10, 10, 80, 30);
         quitButton.addActionListener(new ActionListener() {
        	 
             public void actionPerformed(ActionEvent event) {
            	 update();            	 
            }
             
         });
         
         mTextField = new JTextField();
         mTextField.setBounds(100, 10, 300, 30);
         mTextField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				update();
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				
			}
		});
        
         mPanel.add(quitButton);
         mPanel.add(mTextField);

         setTitle("Quit button");
         setLocationRelativeTo(null);
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         
         
      }
     
     public void update() {
    	 String label = mTextField.getText();
//    	 System.out.println("New text: "+label);
    	 
    	mCanvas = new BufferedImage(mWidth, mHeight, BufferedImage.TYPE_INT_ARGB);    	
     	Graphics2D g2 = mCanvas.createGraphics();
     	 
     	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
     	 
     	g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, mWidth, mHeight); 
    	g2.setColor(Color.BLACK);
    	
    	//g2.drawString(label, 10, 80);
    	
    	drawString(g2,label,10,80);
    	
    	mPanel.updateUI();
     }
     
    
     
     private void drawString(Graphics2D g, String label, int x, int y) { 
    	 int kernBoxes = mFont.mKerningInfos[0].length;
    	 
    	 int X 			= 0;
		 int Y 			= 80;
		 int virtOff 	= 100;
		 int space 		= 2;
		 
		 //Set all kerning lines to zero
		 int kernDist[] = new int[kernBoxes];
		 for(int k=0; k<kernBoxes; k++) kernDist[k] = 0;
    	 		 
		 //iterate through each character of the string
    	 for(int i=0; i<label.length(); i++){
    		 int c = label.charAt(i) - mFont.mFirstChar;
    		 
    		 
    		 try{
    			 TextureCoordinate src = mFont.mLetterCoords[c];
    			 
    			 int charWidth = (int) (src.x2 - src.x1);
    			 int letterOffset = 0;
    			 int leftChoice = 0;
    			     			 
    			 for(int k=0; k<kernBoxes; k++) {
    				  int dist = kernDist[k] - mFont.mKerningInfos[c][k][0] + virtOff;
    				  
    				  if(dist > letterOffset) {
    					  leftChoice = k;
    					  letterOffset = dist;
    				  }
    			 }
    			 letterOffset -= virtOff;
    			 
    			 X = letterOffset;
    			 System.out.println("Left choice: "+leftChoice);
    			 System.out.println("KernDist:");
    			 for(int k=0; k<kernBoxes; k++) {
    				 kernDist[k] = Math.max(letterOffset + mFont.mKerningInfos[c][k][1] + space, kernDist[k]);
    				 System.out.println("\t "+kernDist[k]);
    			 }
    			 
    			 g.drawImage(mTexture, X, Y, X+charWidth, Y+mFont.mHeight, (int)src.x1, (int)src.y1, (int)src.x2, (int)src.y2, null);
    			 //g.drawRect(X, Y, charWidth, mFont.mHeight);
    			 //X += charWidth;
    			 
    		 
    		 } catch (IndexOutOfBoundsException e) {}
    		 
    	 }
    	 
     }



	@SuppressWarnings("serial")
     class MyPanel extends JPanel {
    	 public void paintComponent(Graphics g) {
    		 super.paintComponent(g); 
    		 g.drawImage(mCanvas, 10,50,this);
         }
     }
}
