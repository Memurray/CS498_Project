import static org.junit.Assert.*;
import java.awt.image.BufferedImage;
import org.junit.*;

public class TestFontDetect {
	ImageFrame f = new ImageFrame();
	BufferedImage bim = f.readImage("Test_Images/Arial.png");
	MyImageObj MIO = new MyImageObj(bim);


	@Test
	public void testArialDetect() {  //Test if correctly predict font is Arial
		bim = f.readImage("Test_Images/Arial.png");
		MIO.setImage(bim);
		MIO.filterImage();
		assertEquals("Arial", MIO.genLetter("T", false));
	}
	
	@Test
	public void testArialNarrowDetect() {  //Test if correctly predict font is Arial narrow
		bim = f.readImage("Test_Images/Arial_Narrow.png");
		MIO.setImage(bim);
		MIO.filterImage();
		assertEquals("Arial Narrow", MIO.genLetter("T", false));
	}
	
	@Test
	public void testComicSansDetect() {  //Test if correctly predict font is comic Sans
		bim = f.readImage("Test_Images/Comic_Sans.png");
		MIO.setImage(bim);
		MIO.filterImage();
		assertEquals("Comic Sans MS", MIO.genLetter("T", false));
	}
	
	@Test
	public void testGeorgiaDetect() {  //Test if correctly predict font is Georgia
		bim = f.readImage("Test_Images/Georgia.png");
		MIO.setImage(bim);
		MIO.filterImage();
		assertEquals("Georgia", MIO.genLetter("T", false));
	}
	
	@Test
	public void testRockwellDetect() {  //Test if correctly predict font is Rockwell extra bold
		bim = f.readImage("Test_Images/Rockwell.png");
		MIO.setImage(bim);
		MIO.filterImage();
		assertEquals("Rockwell Extra Bold", MIO.genLetter("T", false));
	}
}