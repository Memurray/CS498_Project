import static org.junit.Assert.*;
import org.junit.*;

public class MyPixelTest {
	pixel p1;	

	@Test 
	public void testSet1() //ensure constructor 1 works as intended
	   {
	      p1 = new pixel();
	      assertEquals (0, p1.r);
	      assertEquals (0, p1.g);
	      assertEquals (0, p1.b);
	   }
	
	
	@Test 
	public void testSet2() //ensure constructor 2 works as intended
	   {
	      p1 = new pixel(45,32,16);
	      pixel p2 = new pixel(p1.build());
	      assertEquals (45, p2.r);
	      assertEquals (32, p2.g);
	      assertEquals (16, p2.b);
	   }
	
	@Test 
	public void testSet3() //ensure constructor 3 works as intended
	   {
	      p1 = new pixel(0,0,0);
	      assertEquals (0, p1.r);
	      assertEquals (0, p1.g);
	      assertEquals (0, p1.b);
	   }
	
	@Test 
	public void testAllThreshold_Down() //ensure thresholding operation properly handles threshold to black
	   {
	      p1 = new pixel(60,20,20);
	      p1.allThreshold(70);
	      assertEquals (0, p1.r);
	      assertEquals (0, p1.g);
	      assertEquals (0, p1.b);
	   }
	
	@Test 
	public void testAllThreshold_Up() //ensure thresholding operation properly handles threshold to white
	   {
	      p1 = new pixel(60,20,20);
	      p1.allThreshold(40);
	      assertEquals (255, p1.r);
	      assertEquals (255, p1.g);
	      assertEquals (255, p1.b);
	   }
	
	
	@Test 
	public void testBuild()  //Ensures that pixel channel decode then re-encode produces the same value as the input
	   {
	      int  rawPixelValue = 658234;
		  p1 = new pixel(rawPixelValue);	      
	      assertEquals (rawPixelValue, p1.build());	      
	   }
	
}
