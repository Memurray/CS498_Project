import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BufferedImageSP extends BufferedImage{
	private BufferedImage bim = null;
	private int width;
	private int height;
	
	private final float[] EdgeDetect =
	    {       -1,-1,-1,
	            -1,8,-1,
	            -1,-1,-1};
	
	public BufferedImageSP(int width, int height) {
		super(width, height, BufferedImage.TYPE_INT_RGB);	
		setAll(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));		
	}
	
	public BufferedImageSP(BufferedImage source){
		super(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        setAll(b);
    }  	 
	
	public void copyBim(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        setAll(b);
    } 
	
	private void setAll(BufferedImage b) {
		bim = b;
        width = bim.getWidth();
        height = bim.getHeight();
	}

	public void blackAndWhite(int threshold){
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
        	bim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
            for (int col = 0; col < width; col++){  //for each column (inside the current row)
            	pixel p1 = new pixel(rgbim1 [col]);
             	p1.allThreshold(threshold);                      
                rgbim1 [col] = p1.build();  //build rgb for that pixel
            }
            bim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
        }
    }
	
	public int[] getRGB(int a, int b, int c, int d, int[]array , int e, int f) {
		return 	bim.getRGB(a, b, c, d, array, e, f);
	}
	
	public void edgeDetect() {
		Kernel kernel = new Kernel (3, 3, EdgeDetect);
        ConvolveOp cop = new ConvolveOp (kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage newbim = new BufferedImage(bim.getWidth(), bim.getHeight(),BufferedImage.TYPE_INT_RGB);
        cop.filter(bim, newbim);
        bim = newbim;
	}
	
	public void renderLetter(String c, String font, int pad) {
		Graphics2D big = bim.createGraphics();		
		big.setColor(Color.WHITE); 
    	int fontSize = (int) ((height-pad*2)*1.4)+1;
        big.setFont(new Font(font, Font.BOLD, fontSize));
        big.drawString(c,-1,height-pad);
	}
	
	
	public int[] buildFillArray() {
		int [] rgbim1 = new int [width*height];
		int [] fillArray = new int [width*height];
    	bim.getRGB (0, 0, width, height, rgbim1, 0, width);  
    	for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++){ 
            	int loc = retPixel(col,row);
            	pixel p1 = new pixel(rgbim1 [loc]);
            	if (p1.isWhite())
            		fillArray[loc] = 1;
            	else
            		fillArray[loc] = -1;
            }
        }
    	return fillArray;
	}
	
	 private int retPixel(int col, int row) {  //common operation for getting 1-d array location from 2-d input criteria, using object width
	    	return row*width + col;
	    }	
	
	public BufferedImage getBim() {
		return bim;
	}		
}
