import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.junit.*;

public class TestMyImageObj {
	
		BufferedImage bim = readImage("./textimage.png");
		MyImageObj MIO = new MyImageObj(new BufferedImage(400,400, BufferedImage.TYPE_INT_RGB));	
		
		public BufferedImage readImage (String file) {
	        Image image = Toolkit.getDefaultToolkit().getImage(file);
	        MediaTracker tracker = new MediaTracker (new Component () {});
	        tracker.addImage(image, 0);
	        try { tracker.waitForID (0); }
	        catch (InterruptedException e) {}
	        BufferedImage bim = null;
	        try {
	            bim = ImageIO.read(getClass().getResource(file)); 
	        } catch (IOException ex) { //if error do this
	            System.err.println(ex);
	            ex.printStackTrace();
	        }
	        return bim;
	    }

		@Test 
		public void testInSelectionTrue() //ensure constructor 1 works as intended
		   {
		    MIO.setSelection(new Point(1,1), new Point(100,100));
		    assertTrue (MIO.inSelection(10, 10));
		    
		   }
		
		@Test 
		public void testInSelectionFalse()
		   {
		    MIO.setSelection(new Point(1,1), new Point(100,100));
		    assertFalse (MIO.inSelection(10, 101));
		    
		   }
		
		@Test 
		public void testInSelectionOrderFalse()
		   {
		    MIO.setSelection(new Point(200,200), new Point(100,100));
		    assertFalse (MIO.inSelection(150, 150));
		    
		   }
		
		@Test 
		public void testInSelectionOrderTrue() 
		   {
			 MIO.setSelection(new Point(200,200), new Point(100,100));
			 MIO.fixSelection();
			 assertTrue (MIO.inSelection(150, 150));
		    
		   }
		
		@Test 
		public void testNoRegions() 
		   {
			 MIO.setTol(50);
			 assertEquals(0, MIO.getFloodRegions().size());		    
		   }
		
		@Test 
		public void testSomeRegions() 
		   {
			 MIO.setImage(bim);
			 MIO.filterImage();
			 assertEquals(4, MIO.getFloodRegions().size());		    
		   }
		
	
		
	}


