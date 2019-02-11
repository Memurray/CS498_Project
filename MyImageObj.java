import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class MyImageObj extends JLabel {
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;
    private BufferedImage finalbim=null;
    private boolean showfiltered=false;
    int [] toFillArray;
    int [] rgbData;
    int height, width, pixelsSelected;
    private Point selectionStart, selectionEnd;
    private boolean selecting  = false;
    private boolean filteredFlag = false;
    
    public void setSelection(Point start, Point end){
    	selecting=true;
        selectionStart = start;
        selectionEnd = end;
        repaint();
    }    
    
    public void endSelection() {
    	selecting=false;
    	paintRegionRedWithBounds();
        repaint();
    }

    private final float[] EdgeDetect =
            {       -1,-1,-1,
                    -1,8,-1,
                    -1,-1,-1};

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
        Kernel kernel = new Kernel (3, 3, EdgeDetect);
        ConvolveOp cop = new ConvolveOp (kernel, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage newbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = newbim.createGraphics();
        big.drawImage (bim, 0, 0, null);
        cop.filter(newbim, filteredbim);
        filteredFlag = true;
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
                		assignRegion();
	                }
	                else
	                	toFillArray[pixel]=2;
                } 
            }   
        }   
    }
    
    private void assignRegion() {
    	for(int i=0; i < height; i++) {
			for(int j=0; j < width; j++) {
				int loc = retPixel(i,j);
				if(toFillArray[loc]==3) {
					int max = 10;
					int min = 1000;
					if(pixelsSelected * max < height*width && pixelsSelected * min > height*width) {
						toFillArray[loc] = 1;
					}
					else
						toFillArray[loc] = 2;
				}
			}
		}
    }
    
    private void paintRegionRed(){
    	if(!filteredFlag)
    		filterImage();
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
            finalbim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
        }
    }
    
    private void paintRegionRedWithBounds(){
    	filterImage();
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
            filteredbim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
            for (int col = 0; col < width; col++){  //for each column (inside the current row)
                int rgb1 = rgbim1 [col];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                int x1,x2,y1,y2;
                if(selectionStart.x > selectionEnd.x) {
                	x1 = selectionEnd.x;
                	x2 = selectionStart.x;          
                }
                else {
                	x2 = selectionEnd.x;
                	x1 = selectionStart.x;  
                }
                if(selectionStart.y > selectionEnd.y) {
                	y1 = selectionEnd.y;
                	y2 = selectionStart.y;          
                }
                else {
                	y2 = selectionEnd.y;
                	y1 = selectionStart.y;   
                }
                	
                if(row >= y1 && row <= y2 && col >= x1 && col <= x2) {
	                if(toFillArray[retPixel(row,col)] == 1) {
	                	r1=255;
	                	g1=0;
	                	b1=0;
	                }    
            	}
                rgbim1 [col] = (r1 << 16) | (g1 << 8) | b1;  //build rgb for that pixel
            }
            finalbim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
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
            big.drawImage(finalbim, 0, 0, this);
        else
            big.drawImage(bim, 0, 0, this);
        if(selecting){  //as long as both points are defined
            g.setColor(Color.RED);
            int x=Math.min(selectionStart.x, selectionEnd.x);
            int y=Math.min(selectionStart.y, selectionEnd.y);
            int width=Math.abs(selectionStart.x - selectionEnd.x);
            int height=Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x,y,width,height);  //draw a red rectangle around the area the user is right click and drag selecting
            g.setColor(Color.BLACK);
        }
    }
}
