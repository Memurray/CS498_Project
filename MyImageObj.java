import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class MyImageObj extends JLabel {
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;
    private BufferedImage LPfilteredbim=null;
    private BufferedImage finalbim=null;
    private boolean showfiltered=false;
    private int [] toFillArray;
    private int [] finalFillArray;
    private int [] rgbData, original_rgbData;
    private int height, width, pixelsSelected, startX, startY;
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
    
    private final float[] LowPass =
        {       1/9f,1/9f,1/9f,
        	1/9f,3/9f,1/9f,
        	1/9f,1/9f,1/9f};

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
        finalFillArray = new int [width*height];
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
        finalFillArray = new int [width*height];
        filteredFlag = false;
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
        LPfilteredbim = lowPassImage();
        filteredFlag = true;
        blackAndWhite();
        blackAndWhite2();
        mapBounds();
        paintRegionRed();
        showfiltered=true;
        this.repaint();
    }
    
    public BufferedImage lowPassImage() {
        Kernel kernel = new Kernel (3, 3, LowPass);
        ConvolveOp cop = new ConvolveOp (kernel, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage newbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = newbim.createGraphics();
        big.drawImage (bim, 0, 0, null);
        cop.filter(filteredbim, newbim);
        return newbim;
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
    
    private void blackAndWhite2(){
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
            LPfilteredbim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
            for (int col = 0; col < width; col++){  //for each column (inside the current row)
                int rgb1 = rgbim1 [col];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                int thres = 60;
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
            LPfilteredbim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
        }
    }
    
    private void mapBounds(){
        rgbData = new int [width*height];  //row of pixel data for inputBim        
        toFillArray = new int [width*height];   
        
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
	                	startX = col+1;
	                	startY = row+1;
	                	expand(row,col,toFillArray,rgbData,0,0,0,0.0);
                		assignRegion();
	                }
	                else
	                	toFillArray[pixel]=2;
                } 
            }   
        }   
    }
    
    private int testIntersection(int row, int col) {
    		int lastVal=0;
    		int count =0;
    		int [] rgbim1 = new int [width];  //row of pixel data for inputBim
    		LPfilteredbim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
			for(int j=0; j < col+1; j+=2) {
				int rgb1 = rgbim1 [j];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                if (r1 != lastVal) {
                	lastVal = r1;
                	count++;
                }				
			}
			//System.out.print(count + "\n");
			return count%2;

    }
    
    private void assignRegion() {
    	int resultVal;
    	int max = 10;
		int min = 1000;
    	if(pixelsSelected * max < height*width && pixelsSelected * min > height*width) { 
			resultVal = 1;
			//testIntersection(startY,startX);
			finalBounds();	
    	}
		else
			resultVal = 4;
    	
    	for(int i=0; i < height; i++) {
			for(int j=0; j < width; j++) {
				int loc = retPixel(i,j);
				if(toFillArray[loc]==3)
					toFillArray[loc] = resultVal;
			}
		}
    }
    
    
    private void finalBounds(){
        original_rgbData = new int [width*height];  //row of pixel data for inputBim        

        bim.getRGB (0, 0, width, height, original_rgbData, 0, width);

    	int rgb1 = original_rgbData[retPixel(startY,startX)];  //grab pixel
        int r1 = (rgb1 >> 16) & 255;  //split out red
        int g1 = (rgb1 >> 8) & 255; //split out green
        int b1 = rgb1 & 255; //split out blue
        
        finalFillArray[retPixel(startY,startX)]=3;
        //System.out.print(r1 + " " + g1 + " " + b1 + "\n");
    	expand(startY,startX,finalFillArray,original_rgbData,r1,g1,b1,140.0);   
    	softenOut();
    }
    
    private void paintRegionRed(){
    	if(!filteredFlag)
    		filterImage();
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
            bim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
            for (int col = 0; col < width; col++){  //for each column (inside the current row)
                int rgb1 = rgbim1 [col];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                
                if(finalFillArray[retPixel(row,col)] == 3) {
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
    	if(!filteredFlag)
    		filterImage();
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
            bim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
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
	                if(finalFillArray[retPixel(row,col)] == 3) {
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
    
    private void expand(int rowIn, int colIn, int[] thisArray, int[] this_rgbData, int r, int g, int b, double tolerance) {
    	int bound = 5;
    	Stack<Integer> st = new Stack<Integer>();
    	int pixel = retPixel(rowIn,colIn);
    	st.push(pixel);
    	double call_colorOffset, neighbor_colorOffset;
    	while(!st.empty()) {
    		int popped = st.pop();
    		int row = popped/width;
    		int col = popped - row*width;   
    		
    		int rgb2 = this_rgbData [retPixel(row,col)];  //grab pixel
            int r2 = (rgb2 >> 16) & 255;  //split out red
            int g2 = (rgb2 >> 8) & 255; //split out green
            int b2 = rgb2 & 255; //split out blue
            
            int rgb3 = rgbData[retPixel(row,col)];  //grab pixel
            int sum3 = (((rgb3 >> 16) & 255) + ((rgb3 >> 8) & 255) + (rgb3 & 255));
                    	
        	if(row > 0 && thisArray[retPixel(row-1,col)] == 0) {
        		int rgb1 = this_rgbData [retPixel(row-1,col)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                int rgb4 = rgbData [retPixel(row-1,col)];  //grab pixel
                int sum4 = ((rgb4 >> 16) & 255) + ((rgb4 >> 8) & 255) + (rgb4 & 255);
               
                call_colorOffset = Math.sqrt((Math.pow(r-r1,2)+Math.pow(g-g1,2)+Math.pow(b-b1,2))/3);
                neighbor_colorOffset = Math.sqrt((Math.pow(r2-r1,2)+Math.pow(g2-g1,2)+Math.pow(b2-b1,2))/3);
                if(call_colorOffset <= tolerance && neighbor_colorOffset <= tolerance && toFillArray[retPixel(row-1,col)] != 4 && (sum3 != 765 || sum4 == 765 || (call_colorOffset <= bound && neighbor_colorOffset <= bound))) {
                	thisArray[retPixel(row-1,col)]= 3;
                	st.push(retPixel(row-1,col));
                	pixelsSelected++;
                }
                else
                	thisArray[retPixel(row-1,col)] = 2;
        	}
        	
        	if(row < height - 1 && thisArray[retPixel(row+1,col)] == 0) {
        		int rgb1 = this_rgbData [retPixel(row+1,col)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                int rgb4 = rgbData [retPixel(row+1,col)];  //grab pixel
                int sum4 = ((rgb4 >> 16) & 255) + ((rgb4 >> 8) & 255) + (rgb4 & 255);
                
                call_colorOffset = Math.sqrt((Math.pow(r-r1,2)+Math.pow(g-g1,2)+Math.pow(b-b1,2))/3);
                neighbor_colorOffset = Math.sqrt((Math.pow(r2-r1,2)+Math.pow(g2-g1,2)+Math.pow(b2-b1,2))/3);
                if(call_colorOffset <= tolerance && neighbor_colorOffset <= tolerance && toFillArray[retPixel(row+1,col)] != 4 && (sum3 != 765 || sum4 == 765 || (call_colorOffset <= bound && neighbor_colorOffset <= bound))) {
                	thisArray[retPixel(row+1,col)]= 3;
                	st.push(retPixel(row+1,col));
                	pixelsSelected++;
                }
                else
                	thisArray[retPixel(row+1,col)] = 2;
        	}
        	
          	if(col > 0 && thisArray[retPixel(row,col-1)] == 0) {
        		int rgb1 = this_rgbData [retPixel(row,col-1)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                int rgb4 = rgbData [retPixel(row,col-1)];  //grab pixel
                int sum4 = ((rgb4 >> 16) & 255) + ((rgb4 >> 8) & 255) + (rgb4 & 255);
                
                call_colorOffset = Math.sqrt((Math.pow(r-r1,2)+Math.pow(g-g1,2)+Math.pow(b-b1,2))/3);
                neighbor_colorOffset = Math.sqrt((Math.pow(r2-r1,2)+Math.pow(g2-g1,2)+Math.pow(b2-b1,2))/3);
                if(call_colorOffset <= tolerance && neighbor_colorOffset <= tolerance && toFillArray[retPixel(row,col-1)] != 4 && (sum3 != 765 || sum4 == 765 || (call_colorOffset <= bound && neighbor_colorOffset <= bound))) {
                	thisArray[retPixel(row,col-1)]= 3;
                	st.push(retPixel(row,col-1));
                	pixelsSelected++;
                }
                else
                	thisArray[retPixel(row,col-1)] = 2;
        	}
        	
        	if(col < width - 1 && thisArray[retPixel(row,col+1)] == 0) {
        		int rgb1 = this_rgbData [retPixel(row,col+1)];  //grab pixel
                int r1 = (rgb1 >> 16) & 255;  //split out red
                int g1 = (rgb1 >> 8) & 255; //split out green
                int b1 = rgb1 & 255; //split out blue
                int rgb4 = rgbData [retPixel(row,col+1)];  //grab pixel
                int sum4 = ((rgb4 >> 16) & 255) + ((rgb4 >> 8) & 255) + (rgb4 & 255);
                
                call_colorOffset = Math.sqrt((Math.pow(r-r1,2)+Math.pow(g-g1,2)+Math.pow(b-b1,2))/3);
                neighbor_colorOffset = Math.sqrt((Math.pow(r2-r1,2)+Math.pow(g2-g1,2)+Math.pow(b2-b1,2))/3);
                if(call_colorOffset <= tolerance && neighbor_colorOffset <= tolerance && toFillArray[retPixel(row,col+1)] != 4 && (sum3 != 765 || sum4 == 765 || (call_colorOffset <= bound && neighbor_colorOffset <= bound))) {
                	thisArray[retPixel(row,col+1)]= 3;
                	st.push(retPixel(row,col+1));
                	pixelsSelected++;
                }
                else
                	thisArray[retPixel(row,col+1)] = 2;
        	}         	
    	}	
    }
    
    private void softenOut() {
    	for (int row = 1; row < height-1; row++){  //for each row
            for (int col = 1; col < width-1; col++){
            	int sum = finalFillArray[retPixel(row+1,col)] + finalFillArray[retPixel(row-1,col)] + finalFillArray[retPixel(row,col+1)] + finalFillArray[retPixel(row,col-1)];
            	if (sum >= 9 )
            		finalFillArray[retPixel(row,col)] = 3;
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
        filteredFlag = false;
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
