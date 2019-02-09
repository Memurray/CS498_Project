import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class MyImageObj extends JLabel {

    // instance variable to hold the buffered image
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;
    private BufferedImage finalbim=null;
    private boolean showfiltered=false;
    int [] toFillArray;
    int [] rgbData;
    int height, width, pixelsSelected;
    

    private final float[] EdgeDetect =
            {       -1,-1,-1,
                    -1,8,-1,
                    -1,-1,-1};

    private final float[] BasicBlur =//Custom kernel for gaussian blur sigma 3.0
            {
                    0.1111111f,0.1111111f,0.1111111f,
                    0.1111111f,0.1111111f,0.1111111f,
                    0.1111111f,0.1111111f,0.1111111f};

    private final float[] GAUSS5x5SD1_0 =   //Custom kernel for gaussian blur sigma 1.0
            {
                    0.003765f, 0.015019f, 0.023792f, 0.015019f, 0.003765f,
                    0.015019f, 0.059912f, 0.094907f, 0.059912f, 0.015019f,
                    0.023792f, 0.094907f, 0.150342f, 0.094907f, 0.023792f,
                    0.015019f, 0.059912f, 0.094907f, 0.059912f, 0.015019f,
                    0.003765f, 0.015019f, 0.023792f, 0.015019f, 0.003765f};

    // Default constructor
    public MyImageObj() {
    }

    // This constructor stores a buffered image passed in as a parameter
    public MyImageObj(BufferedImage img) {
        bim = img;
        height = bim.getHeight();
        width = bim.getWidth();
        filteredbim = new BufferedImage
                (width, height, BufferedImage.TYPE_INT_RGB);
        finalbim = new BufferedImage
                (width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));
        this.repaint();
    }

    // This mutator changes the image by resetting what is stored
    // The input parameter img is the new image;  it gets stored as an
    //     instance variable
    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        height = bim.getHeight();
        width = bim.getWidth();
        filteredbim = new BufferedImage
                (width, height, BufferedImage.TYPE_INT_RGB);
        finalbim = new BufferedImage
                (width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));
        showfiltered=false;
        this.repaint();
    }

    // accessor to get a handle to the bufferedimage object stored here
    public BufferedImage getImage() {
        return bim;
    }


    //  apply the blur operator
    public void filterImage() {
        if (bim == null) return;
        Kernel kernel;


        kernel = new Kernel (3, 3, EdgeDetect);
        ConvolveOp cop = new ConvolveOp (kernel, ConvolveOp.EDGE_NO_OP, null);

        //kernel = new Kernel (5, 5, GAUSS5x5SD1_0);
        //ConvolveOp cop2 = new ConvolveOp (kernel, ConvolveOp.EDGE_NO_OP, null);

        // make a copy of the buffered image
        BufferedImage newbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
//        BufferedImage newbim2 = new BufferedImage
//                (bim.getWidth(), bim.getHeight(),
//                        BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D big = newbim.createGraphics();
        big.drawImage (bim, 0, 0, null);

        cop.filter(newbim, filteredbim);

        //cop.filter(newbim, newbim2);
        //cop2.filter(newbim2, filteredbim);
        blackAndWhite();
        mapBounds();
        paintRegionRed();
        showfiltered=true;
        this.repaint();
    }
    
    private void blackAndWhite(){
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
            filteredbim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
            for (int col = 0; col < width; col++){  //for each column (inside the current row)
                int rgb1 = rgbim1 [col];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                int thres = 30;
                if(r1 > thres || b1 > thres || g1 >thres) {
                	r1=255;	
                	b1 =255;
                	g1=255;
                }
                else {
                	r1=0;	
                	b1 =0;
                	g1=0;
                }
         
                rgbim1 [col] = (r1 << 16) | (g1 << 8) | b1;  //build rgb for that pixel
            }
            filteredbim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
        }
    }
    
    private void mapBounds(){
        rgbData = new int [width*height];  //row of pixel data for inputBim        
        toFillArray = new int [width*height];   
        int counter = 0;
        
        filteredbim.getRGB (0, 0, width, height, rgbData, 0, width);
        for (int row = 0; row < height; row++){  //for each row
            for (int col = 0; col < width; col++){  //for each column
            	int pixel = row*width + col;
            	pixelsSelected = 0;
            	
                if(toFillArray[pixel] == 0) {
	            	int rgb1 = rgbData[pixel];  //grab pixel
	                int r1 = (rgb1 >> 16) & 255;  //split out red
	                int g1 = (rgb1 >> 8) & 255; //split out green
	                int b1 = rgb1 & 255; //split out blue
	                if(r1==0 && g1==0 && b1==0) {
	                	toFillArray[pixel]=3;
	                	expand(row,col);
                		for(int i=0; i < height; i++) {
                			for(int j=0; j < width; j++) {
                				int loc = retPixel(i,j);
                				if(toFillArray[loc]==3) {
                					if(pixelsSelected * 10 < height*width) {
                						toFillArray[loc] = 1;
                					}
                					else
                						toFillArray[loc] = 2;
                				}
                			}
                		}
	                }
	                else
	                	toFillArray[pixel]=2;
                } 
            }   
        }   
    }
    
    private void paintRegionRed(){
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
            filteredbim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
            for (int col = 0; col < width; col++){  //for each column (inside the current row)
                int rgb1 = rgbim1 [col];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                
                if(toFillArray[retPixel(row,col)] == 1) {
                	r1=255;
                	g1=0;
                	b1=0;
                }         
                rgbim1 [col] = (r1 << 16) | (g1 << 8) | b1;  //build rgb for that pixel
            }
            filteredbim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
        }
    }
    
    private void expand(int rowIn, int colIn) {
    	Stack<Integer> st = new Stack<Integer>();
    	int pixel = retPixel(rowIn,colIn);
    	st.push(pixel);
    	while(!st.empty()) {
    		int popped = st.pop();
    		int row = popped/width;
    		int col = popped - row*width;
        	
        	if(row > 0 && toFillArray[retPixel(row-1,col)] == 0) {
        		int rgb1 = rgbData [retPixel(row-1,col)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                if(r1==0 && g1==0 && b1==0) {
                	toFillArray[retPixel(row-1,col)]= 3;
                	st.push(retPixel(row-1,col));
                	pixelsSelected++;
                }
                else
                	toFillArray[retPixel(row-1,col)] = 2;
        	}
        	
        	if(row < height - 1 && toFillArray[retPixel(row+1,col)] == 0) {
        		int rgb1 = rgbData [retPixel(row+1,col)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                if(r1==0 && g1==0 && b1==0) {
                	toFillArray[retPixel(row+1,col)]= 3;
                	st.push(retPixel(row+1,col));
                	pixelsSelected++;
                }
                else
                	toFillArray[retPixel(row+1,col)] = 2;
        	}
        	
          	if(col > 0 && toFillArray[retPixel(row,col-1)] == 0) {
        		int rgb1 = rgbData [retPixel(row,col-1)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                if(r1==0 && g1==0 && b1==0) {
                	toFillArray[retPixel(row,col-1)]= 3;
                	st.push(retPixel(row,col-1));
                	pixelsSelected++;
                }
                else
                	toFillArray[retPixel(row,col-1)] = 2;
        	}
        	
        	if(col < width - 1 && toFillArray[retPixel(row,col+1)] == 0) {
        		int rgb1 = rgbData [retPixel(row,col+1)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                if(r1==0 && g1==0 && b1==0) {
                	toFillArray[retPixel(row,col+1)]= 3;
                	st.push(retPixel(row,col+1));
                	pixelsSelected++;
                }
                else
                	toFillArray[retPixel(row,col+1)] = 2;
        	}         	
    	}	
    }
    
    private int retPixel(int row, int col) {
    	return row*width + col;
    }

    //  show current image by a scheduled call to paint()
    public void showImage() {
        if (bim == null) return;
        showfiltered=false;
        this.repaint();
    }

    //  get a graphics context and show either filtered image or
    //  regular image
    public void paintComponent(Graphics g) {
        Graphics2D big = (Graphics2D) g;
        if (showfiltered)
            big.drawImage(filteredbim, 0, 0, this);
        else
            big.drawImage(bim, 0, 0, this);
    }
}
