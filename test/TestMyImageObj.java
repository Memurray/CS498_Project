import static org.junit.Assert.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import org.junit.*;

public class TestMyImageObj {
		MyImageObj MIO = new MyImageObj(new BufferedImage(400,400, BufferedImage.TYPE_INT_RGB));	

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
	}
