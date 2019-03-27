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
    private int showfiltered= 0;
    private int [] toFillArray,finalFillArray,rgbData, original_rgbData;
    private int height, width, pixelsSelected, startX, startY;
    private Point selectionStart, selectionEnd;
    private boolean selecting  = false;
    private boolean filteredFlag = false;
    private double sliderTol;
    private Stack<Integer> st;
    private int regionCount;
    private Point textXY,textBottomLeft;
    private int letterHeight=100;
    private boolean vIntBool = false;
    private boolean textBool = false;
    private String textFill = "Text";
    
    private final float[] EdgeDetect =
    {       -1,-1,-1,
            -1,8,-1,
            -1,-1,-1};

    private final float[] LowPass =
    {       1/9f,1/9f,1/9f,
    		1/9f,1/9f,1/9f,
    		1/9f,1/9f,1/9f};
      
    public MyImageObj(BufferedImage img) {
        bim = img;
        resetImage(); //Initializations in own method
    }
    
    //Getters and Setters Start
    //********************************************************
    public void setImage(BufferedImage img) {
    	bim = img;
        resetImage();
    }   

   public BufferedImage getImage() {return bim; }   
   
   public BufferedImage returnFinal() { return finalbim; }
    
    public void setSelection(Point start, Point end){
    	selecting=true;
        selectionStart = start;
        selectionEnd = end;
        repaint();
    }   
    
    public void setDisplayText(String in) {textFill = in;}
    
    public String getDisplayText() {return textFill;}
    
    public void setTol(int input) {
    	sliderTol = (double) input * 2.55;
    	finalbim = copyImage(bim);
    	if(!filteredFlag)
    		filterImage();
    	finalFillArray = new int [width*height];
    	mapBounds();
    	softenOut(2);
        catchHorizontal();
        showfiltered=2;
        this.repaint();
    }
    
    //Getters and Setters End
    //********************************************************
   
    
    //Toggle state variables based on GUI menu clicks
    //********************************************************
    public void toggleVint() {vIntBool = !vIntBool;} 
    
    public void toggleText() {textBool = !textBool;}    
    //********************************************************
    
   
    //Re-Initialize variables, also used for original construction
    public void resetImage() {
        if (bim == null) return;
        regionCount = 0;
        height = bim.getHeight();
        width = bim.getWidth();
        selectionStart = new Point(0,0);
        selectionEnd = new Point(width-1,height-1);
        sliderTol = 140.0;
        filteredbim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        finalbim = copyImage(bim);
        setPreferredSize(new Dimension(width, height));
        showfiltered=0;
        finalFillArray = new int [width*height];
        filteredFlag = false;
        this.repaint();
    }      
    
    //When mouse press is released, update rendering to reflect new boundaries 
    public void endSelection() {
    	regionCount = 0;
    	selecting=false;
    	filterImage();      	
    	fixSelection();
    	finalbim = copyImage(bim);
    	catchHorizontal();
        repaint();
    }

    //Core driving function, 
    //********	in need of cleanup **************
    public void filterImage() {
        if (bim == null) return;
        finalFillArray = new int [width*height];
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
        blackAndWhite(filteredbim,30);
        blackAndWhite(LPfilteredbim,60);
        mapBounds();
        softenOut(2);
        catchHorizontal();
        showfiltered=2;
        this.repaint();
    }
    
    //Enforce that start location is always less than end location for both x and y coordinates
    private void fixSelection() {
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
        selectionStart = new Point(x1,y1);
        selectionEnd = new Point(x2,y2);
    }
    
    //Check if current inspection x,y is inside user specific area
    public boolean inSelection() {
    	if(startX >= selectionStart.x && startX <= selectionEnd.x && startY >= selectionStart.y && startY <= selectionEnd.y)
    		return true;
    	return false;
    }    
    
    //Apply the low pass kernel to the entire image
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
    
    //Apply threshold to input BufferedImage
    private void blackAndWhite(BufferedImage chosen_bim, int threshold){
        int [] rgbim1 = new int [width];  //row of pixel data for inputBim
        for (int row = 0; row < height; row++){  //for each row
        	chosen_bim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
            for (int col = 0; col < width; col++){  //for each column (inside the current row)
            	pixel p1 = new pixel(rgbim1 [col]);
             	p1.allThreshold(threshold);                      
                rgbim1 [col] = p1.build();  //build rgb for that pixel
            }
            chosen_bim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
        }
    }
    
    //Run through each pixel in edge detect image, flood filling regions, regions visited tracked in toFillArray to prevent revisiting already catagorized pixels
    private void mapBounds(){
        rgbData = new int [width*height];  //row of pixel data for inputBim        
        toFillArray = new int [width*height];           
        filteredbim.getRGB (0, 0, width, height, rgbData, 0, width);
        for (int row = 0; row < height; row++){  //for each row
            for (int col = 0; col < width; col++){  //for each column
            	int pixel = row*width + col;
            	pixelsSelected = 0;
                if(toFillArray[pixel] == 0) {  //If current pixel has not been inspected
                	pixel p1 = new pixel(rgbData[pixel]);  //extract pixel data
	                if(p1.r==0 && p1.g==0 && p1.b==0) {  //If pixel is white
	                	toFillArray[pixel]=3;  //mark as visited, valid pixel
	                	startX = col+1;
	                	startY = row+1;
	                	expand(row,col,toFillArray,rgbData,new pixel(),0.0);  //run flood fill operation starting at this pixel
                		assignRegion();  //determine whether this flood region is text or background
	                }
	                else  //if not white, mark as visited
	                	toFillArray[pixel]=2;
                } 
            }   
        }   
    }
    
    //Categorize flood region
    private void assignRegion() {
    	int resultVal;
    	int max = 10;
		int min = 10000;
		if(pixelsSelected * max < height*width && pixelsSelected * min > height*width) {  //if flood region is not too big or too small
			resultVal = 1;  //New value will be 1, meaning considered text and wont be picked up during next run of this method
			finalBounds();	//Evaluate this area on the real image (not the edgeDetect)
    	}
    	else if(pixelsSelected * min <= height*width)
    		resultVal = 5;  //Region considered too small, not currently used for anything, but can be if needed in the future
		else
			resultVal = 4;  //region too big, considered the background
    	
    	//All pixels marked as valid(3) for this flood region will be tagged based on what category above they fell into
		for(int i=0; i < height; i++) {
			for(int j=0; j < width; j++) {
				int loc = retPixel(i,j);
				if(toFillArray[loc]==3)
					toFillArray[loc] = resultVal;
			}
		}
    }    
    
    //Perform flood fill region select on the unprocessed image
    private void finalBounds(){
    	if(inSelection())  
    		regionCount++;
    	if(regionCount == 1) {  //if first region found in user selection area, grab information required for text rendering placement
    		textXY = new Point(textBottomLeft);
    		letterHeight=textXY.y-startY;
    	}
        original_rgbData = new int [width*height];   
        bim.getRGB (0, 0, width, height, original_rgbData, 0, width);    	
    	pixel p1 = new pixel(original_rgbData[retPixel(startY,startX)]);
        finalFillArray[retPixel(startY,startX)]=3;
    	expand(startY,startX,finalFillArray,original_rgbData,p1,sliderTol);   //flood fill starting from current inspection pixel on original image    	
    }
       
    //Flood fill from an origin pixel based on input specs
    private void expand(int rowIn, int colIn, int[] thisArray, int[] this_rgbData, pixel p3, double tolerance) {
    	double bound = Math.max(5,sliderTol/30);
    	st = new Stack<Integer>();
    	int startPixel = retPixel(rowIn,colIn);
    	st.push(startPixel);
    	textBottomLeft = new Point(colIn,rowIn);
    	while(!st.empty()) {
    		int popped = st.pop();
    		int row = popped/width;
    		int col = popped - row*width;   
    		pixel p2 = new pixel(this_rgbData [retPixel(row,col)]);
            int sum3 = new pixel(rgbData[retPixel(row,col)]).sum();
                    	
        	if(row > 0 && thisArray[retPixel(row-1,col)] == 0) {
        		int pixelIndex = retPixel(row-1,col);
        		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p3,bound,sum3);
        	}        	
        	if(row < height - 1 && thisArray[retPixel(row+1,col)] == 0) {
        		int pixelIndex = retPixel(row+1,col);
        		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p3,bound,sum3);
        	}        	
          	if(col > 0 && thisArray[retPixel(row,col-1)] == 0) {
          		int pixelIndex = retPixel(row,col-1);
          		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p3,bound,sum3);
        	}        	
        	if(col < width - 1 && thisArray[retPixel(row,col+1)] == 0) {
        		int pixelIndex = retPixel(row,col+1);
        		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p3,bound,sum3);
        	} 
        	if(col < textBottomLeft.x)
        		textBottomLeft.x = col;
        	if(row > textBottomLeft.y)
        		textBottomLeft.y = row;
    	}
    }
    
    //Complicated, will attempt to refactor more later
    private void expandHelper(int pixelIndex,int[] thisArray,int[] this_rgbData,double tolerance, pixel p2, pixel p3, double bound, int sum3) {
    	double call_colorOffset, neighbor_colorOffset;
		pixel p1 = new pixel(this_rgbData [pixelIndex]);
		pixel p4 = new pixel(rgbData [pixelIndex]);
        int sum4 = p4.sum();
        call_colorOffset = Math.sqrt((Math.pow(p3.r-p1.r,2)+Math.pow(p3.g-p1.g,2)+Math.pow(p3.b-p1.b,2))/3);
        neighbor_colorOffset = Math.sqrt((Math.pow(p2.r-p1.r,2)+Math.pow(p2.g-p1.g,2)+Math.pow(p2.b-p1.b,2))/3);
        if(call_colorOffset <= tolerance && neighbor_colorOffset <= tolerance && toFillArray[pixelIndex] != 4 && (sum3 != 765 || sum4 == 765 || (call_colorOffset <= bound && neighbor_colorOffset <= bound))) {
        	thisArray[pixelIndex]= 3;
        	st.push(pixelIndex);
        	pixelsSelected++;
        }
        else
        	thisArray[pixelIndex] = 2;
    }

    //This method only exists because of a poor decision to make finalFillArray = 3, the pixel marker for assumed as text
    private void cleanup() {
    	for (int row = 0; row < height; row++){ 
            for (int col = 0; col < width; col++){
            	int loc  = retPixel(row,col);
            	if(finalFillArray[loc] == 3)
            		finalFillArray[loc] = 1;
            	else
            		finalFillArray[loc] = 0;
            }
        }
    }
      
    //Flagged pixels to be overwritten are in the finalFillArray
    //This method expand those flagged pixels to include neighbors of flagged pixels
    //loopCount effectively controls how many pixels in each direction are added to original flagged pixels
    private void softenOut(int loopCount) {
    	cleanup();
    	if(loopCount <1)
    		loopCount = 1;
    	else if(loopCount > 10)
    		loopCount = 10;
    	for (int i =0; i< loopCount; i++) {
	    	int tempFillArray[] = new int [width*height];
	    	for (int row = 1; row < height-1; row++){  //for each row
	            for (int col = 1; col < width-1; col++){
	            	int sum = finalFillArray[retPixel(row+1,col)] + finalFillArray[retPixel(row-1,col)] + finalFillArray[retPixel(row,col+1)] + finalFillArray[retPixel(row,col-1)] 
	            			+ finalFillArray[retPixel(row+1,col+1)] + finalFillArray[retPixel(row-1,col-1)] + finalFillArray[retPixel(row-1,col+1)] + finalFillArray[retPixel(row+1,col-1)];
	            	if (sum >= 1 || finalFillArray[retPixel(row,col)]==1)
	            		tempFillArray[retPixel(row,col)] = 3;
	            }
	    	}
	    	finalFillArray = tempFillArray;
    	}    	
    }
    
    //Find starting and ending x coordinate that will be overwritten for background replacement 
    private void catchHorizontal() {
    	int state = 0;
    	Point P1 = new Point();
    	Point P2 = new Point();
    	for (int row = selectionStart.y; row < Math.min(selectionEnd.y,height); row++){  //for each row
    		state = 0;
            for (int col = selectionStart.x; col < Math.min(selectionEnd.x,width); col++){
            	if (finalFillArray[retPixel(row,col)] == 3  && state == 0) {
            		state = 1;
            		P1 = new Point(col-1,row);
            	}
            	else if (finalFillArray[retPixel(row,col)] != 3  && state == 1) {
            		state = 0;
            		P2 = new Point(col,row);
            		hInterpolate(P1,P2);
            	}
            }
    	}
    	if(vIntBool) //If vertical interpolation is turned on
    		catchVertical();
    }
    
    //interpolate between the pixel color at P1 and P2 and write to output image
    private void hInterpolate(Point P1, Point P2) {
    	int [] rgbim1 = new int [width];  //row of pixel data for inputBim
    	int row = P1.y;
        finalbim.getRGB (0, row, width, 1, rgbim1, 0, width);  //save that row's pixel data to array
        pixel p1 = new pixel(rgbim1 [P1.x]);
        pixel p2 = new pixel(rgbim1 [P2.x]);       
        double gap = P2.x - P1.x + 0.0;
        for (int i = 1; i < gap; i++) {        	
        	int r3 = (int) ((p2.r-p1.r)*i/gap + p1.r) ;
        	int g3 = (int) ((p2.g-p1.g)*i/gap + p1.g) ;
        	int b3 = (int) ((p2.b-p1.b)*i/gap + p1.b) ;
        	rgbim1 [P1.x + i] = (r3 << 16) | (g3 << 8) | b3;  //build rgb for that pixel
        }         
        finalbim.setRGB (0, row, width, 1, rgbim1, 0, width);  //modify this row to new rules
    }
    
  //Find starting and ending y coordinate that will be overwritten for background replacement 
    private void catchVertical() {
    	int state = 0;
    	Point P1 = new Point();
    	Point P2 = new Point();
    	int [] ffaTranspose = new int [width*height];
    	int i = 0;
    	
    	//Transpose array to reduce cache inefficiency of seeking through array rows before columns
    	for (int col = 0; col < width; col++){ 
            for (int row = 0; row < height; row++){
            	ffaTranspose[i] = finalFillArray[retPixel(row,col)];   
            	i++;
            }            
        }
    	int [] rgbim1 = new int [width*height];  //row of pixel data for inputBim
        finalbim.getRGB (0, 0, width, height, rgbim1, 0, width); 
    	for (int row = selectionStart.x; row < Math.min(selectionEnd.x,width); row++){  //for each row
    		state = 0;
            for (int col = selectionStart.y; col < Math.min(selectionEnd.y,height); col++){
            	if (ffaTranspose[row *height + col] == 3  && state == 0) {
            		state = 1;
            		P1 = new Point(row,col-1);
            	}
            	else if (ffaTranspose[row *height + col] != 3  && state == 1) {
            		state = 0;
            		P2 = new Point(row,col);
            		vInterpolate(P1,P2, rgbim1);
            	}
            }
    	}
    	finalbim.setRGB (0, 0, width, height, rgbim1, 0, width);  //modify this row to new rules
    }
    
   //interpolate between the pixel color at P1 and P2 and write to output image
    private void vInterpolate(Point P1, Point P2, int[] rgbim1) {
        int col = P1.x;
        pixel p1 = new pixel(rgbim1 [retPixel(P1.y,col)]);
        pixel p2 = new pixel(rgbim1 [retPixel(P2.y,col)]);       
        double gap = P2.y - P1.y + 0.0;
        int startCoord = retPixel(P1.y,P1.x);
        for (int i = 1; i < gap; i++) {  
        	pixel p4 = new pixel(rgbim1 [startCoord+i*width]);
        	int r = (int) (((p2.r-p1.r)*i/gap + p1.r)+p4.r)/2 ;
        	int g = (int) (((p2.g-p1.g)*i/gap + p1.g)+p4.g)/2 ;
        	int b = (int) (((p2.b-p1.b)*i/gap + p1.b)+p4.b)/2 ;
        	pixel p3 = new pixel(r,g,b);
        	rgbim1 [startCoord+i*width] = p3.build();  //build rgb for that pixel
        }       
    }
    
    private int retPixel(int row, int col) {  //common operation for getting 1-d array location from 2-d input criteria
    	return row*width + col;
    }

    //Safer way to copy BufferedImage than default assignment
    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }     
    
    //Aliasing arbitrary display option numbers with more sensible method names.
    //***************************************************
    public void showEdge() 		{ 	showfiltered = 1;   }    
    public void showFiltered()  {  	showfiltered = 2;   }
    //***************************************************
    
    //Graphic output rendering rules
    public void paintComponent(Graphics g) {
        Graphics2D big = (Graphics2D) g;
        if (showfiltered == 0)
            big.drawImage(bim, 0, 0, this);
        else if (showfiltered == 1)
        	big.drawImage(filteredbim, 0, 0, this);
        else
            big.drawImage(finalbim, 0, 0, this);
        if(selecting){  //as long as both points are defined
            g.setColor(Color.RED);
            int x=Math.min(selectionStart.x, selectionEnd.x);
            int y=Math.min(selectionStart.y, selectionEnd.y);
            int width=Math.abs(selectionStart.x - selectionEnd.x);
            int height=Math.abs(selectionStart.y - selectionEnd.y);
            g.drawRect(x,y,width,height);  //draw a red rectangle around the area the user is right click and drag selecting
        }
        
        if(filteredFlag && textBool) {  //Text rendering rules
        	g.setColor(Color.BLUE);
        	float fontSize = (float) (letterHeight*1.4);
	        Font font = g.getFont().deriveFont(fontSize);
	        g.setFont( font );
	        g.drawString(textFill,textXY.x,textXY.y); 
        }
        g.setColor(Color.BLACK);
    }
}