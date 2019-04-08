import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class MyImageObj extends JLabel {
    private BufferedImageSP bim;
    private BufferedImageSP filteredbim;
    private int imageSelection= 0;
    private int [] toFillArray;
    private int height, width, pixelsSelected;
    private Point selectionStart, selectionEnd;
    private boolean selecting  = false;
    private boolean filteredFlag = false;
    private double sliderTol;
    private Stack<Integer> st;
    private boolean vIntBool = false;
    private boolean textBool = false;
    private String textFill = "Text";
    private List<FloodRegion> fr;
    
    
    public MyImageObj(BufferedImage img) {
        bim = new BufferedImageSP(img);
        resetImage(); //Initializations in own method
    }
    
    //********************************************************
    //Getters and Setters Start
    //********************************************************
    public void setImage(BufferedImage img) {
    	bim = new BufferedImageSP(img);
        resetImage();
    }   

   public BufferedImage getImage() {return bim.getBim(); }   
   
   public BufferedImage returnFinal() { 
	   BufferedImage img = buildComp();
	   Graphics2D g2d = img.createGraphics();
	   processText(g2d);
	   return img;    
   }
    
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
    	if(!filteredFlag)
    		filterImage();
    	mapBounds();
        imageSelection=2;
        this.repaint();
    }  
    //********************************************************
    //Getters and Setters End
    //********************************************************
    
   
    //********************************************************
    //Toggle state variables based on GUI menu clicks
    //********************************************************
    public void toggleVint() {vIntBool = !vIntBool;} 
    
    public void toggleText() {textBool = !textBool;}    
    //********************************************************
    
   
    //Re-Initialize variables, also used for original construction
    public void resetImage() {
        if (bim == null) return;
        fr = new ArrayList<FloodRegion> ();
        height = bim.getHeight();
        width = bim.getWidth();
        selectionStart = new Point(0,0);
        selectionEnd = new Point(width-1,height-1);
        sliderTol = 102.0;
        filteredbim = new BufferedImageSP(bim.getBim());
        setPreferredSize(new Dimension(width, height));
        imageSelection=0;
        filteredFlag = false;
        this.repaint();
    }      
    
    //When mouse press is released, update rendering to reflect new boundaries 
    public void endSelection() {
    	fr = new ArrayList<FloodRegion> ();
    	selecting=false;
    	fixSelection();
    	filterImage();   
        repaint();
    }

    //Core driving function, 
    public void filterImage() {
        if (bim == null) return;
        filteredbim.edgeDetect();
        filteredbim.blackAndWhite(30);
        filteredFlag = true;
        mapBounds();        
        showFinal();
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
    public boolean inSelection(int startX, int startY) {
    	if(startX >= selectionStart.x && startX <= selectionEnd.x && startY >= selectionStart.y && startY <= selectionEnd.y)
    		return true;
    	return false;
    }    
    
    //Run through each pixel in edge detect image, flood filling regions, regions visited tracked in toFillArray to prevent revisiting already catagorized pixels
    private void mapBounds(){
        int [] rgbData = new int [width*height];  //row of pixel data for inputBim        
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
	                	expand(row,col,toFillArray,rgbData,new pixel(),0.0);  //run flood fill operation starting at this pixel
                		assignRegion(col+1,row+1);  //determine whether this flood region is text or background
	                }
	                else  //if not white, mark as visited
	                	toFillArray[pixel]=2;
                } 
            }   
        }   
    }
    
    //Categorize flood region
    private void assignRegion(int startX, int startY) {
    	int resultVal;
    	int max = 10;
		int min = 10000;
		if(pixelsSelected * max < height*width && pixelsSelected * min > height*width) {  //if flood region is not too big or too small
			resultVal = 1;  //New value will be 1, meaning considered text and wont be picked up during next run of this method
			finalBounds(startX, startY);	//Evaluate this area on the real image (not the edgeDetect)
    	}
    	else if(pixelsSelected * min <= height*width)
    		resultVal = 5;  //Region considered too small, not currently used for anything, but can be if needed in the future
		else
			resultVal = 4;  //region too big, considered the background
    	
    	//All pixels marked as valid(3) for this flood region will be tagged based on what category above they fell into
		for(int i=0; i < height; i++) {
			for(int j=0; j < width; j++) {
				int loc = retPixel(j,i);
				if(toFillArray[loc]==3)
					toFillArray[loc] = resultVal;
			}
		}
    }    
    
    //Perform flood fill region select on the unprocessed image
    private void finalBounds(int startX, int startY){
    	if(inSelection(startX, startY))  {
	    	fr.add(new FloodRegion(toFillArray,bim,new Point(startX,startY),sliderTol));   
	    	if(vIntBool) //If vertical interpolation is turned on
	    		fr.get(fr.size()-1).catchVertical();
    	}
    }
       
    //Flood fill from an origin pixel based on input specs
    private void expand(int rowIn, int colIn, int[] thisArray, int[] this_rgbData, pixel p1, double tolerance) {
    	st = new Stack<Integer>();
    	int startPixel = retPixel(colIn,rowIn);
    	st.push(startPixel);
    	while(!st.empty()) {
    		int popped = st.pop();
    		int row = popped/width;
    		int col = popped - row*width;   
    		pixel p2 = new pixel(this_rgbData [retPixel(col,row)]);
                    	
        	if(row > 0 && thisArray[retPixel(col,row-1)] == 0) {
        		int pixelIndex = retPixel(col,row-1);
        		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p1);
        	}        	
        	if(row < height - 1 && thisArray[retPixel(col,row+1)] == 0) {
        		int pixelIndex = retPixel(col,row+1);
        		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p1);
        	}        	
          	if(col > 0 && thisArray[retPixel(col-1,row)] == 0) {
          		int pixelIndex = retPixel(col-1,row);
          		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p1);
        	}        	
        	if(col < width - 1 && thisArray[retPixel(col+1,row)] == 0) {
        		int pixelIndex = retPixel(col+1,row);
        		expandHelper(pixelIndex,thisArray,this_rgbData,tolerance,p2,p1);
        	} 
    	}
    }
    
    //Repeated code in expand function pushed off to own function
    private void expandHelper(int pixelIndex,int[] thisArray,int[] this_rgbData,double tolerance, pixel p2, pixel p3) {
    	double call_colorOffset, neighbor_colorOffset;
		pixel p1 = new pixel(this_rgbData [pixelIndex]);
        call_colorOffset = Math.sqrt((Math.pow(p3.r-p1.r,2)+Math.pow(p3.g-p1.g,2)+Math.pow(p3.b-p1.b,2))/3);
        neighbor_colorOffset = Math.sqrt((Math.pow(p2.r-p1.r,2)+Math.pow(p2.g-p1.g,2)+Math.pow(p2.b-p1.b,2))/3);
        if(call_colorOffset <= tolerance && neighbor_colorOffset <= tolerance && toFillArray[pixelIndex] != 4) {
        	thisArray[pixelIndex]= 3;
        	st.push(pixelIndex);
        	pixelsSelected++;
        }
        else
        	thisArray[pixelIndex] = 2;
    }
    
    private int retPixel(int col, int row) {  //common operation for getting 1-d array location from 2-d input criteria
    	return row*width + col;
    }
    
    private int retPixel(int col, int row, int width) {  //common operation for getting 1-d array location from 2-d input criteria
    	return row*width + col;
    }
    
    private BufferedImage buildComp() {
    	BufferedImage compbim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);	
    	int [] rgbim1 = new int [width*height];  //row of pixel data for inputBim
        bim.getRGB (0, 0, width, height, rgbim1, 0, width); 
    	for (int it = 0; it < fr.size(); it++) {
	    	FloodRegion f = fr.get(it);
	    	Point dims = f.getDim();
	    	BufferedImage bim2 = f.getfinalbim();	    	
	        int [] rgbim2 = new int [dims.x*dims.y];  //row of pixel data for inputBim
	        bim2.getRGB (0, 0, dims.x, dims.y, rgbim2, 0, dims.x);
	        int i_dest, i_source;
			for(int i = 0; i < dims.y; i++) {
				for(int j = 0; j < dims.x; j++) {
					i_source = retPixel(j,i,dims.x);
					i_dest = retPixel(f.getTopLeft().x + j,f.getTopLeft().y+i);			
					rgbim1[i_dest] = rgbim2[i_source];				
				}
			}
    	}   		
		compbim.setRGB (0, 0, width, height, rgbim1, 0, width); 
		return compbim;
    }
    
    private void processText(Graphics2D big) {
        if(filteredFlag && textBool && fr.size()>0) {  //Text rendering rules
        	big.setColor(Color.BLUE);
        	FloodRegion f = fr.get(0);
        	float fontSize = (float) (f.getTextHeight()*1.4);
	        big.setFont(new Font("default", Font.BOLD, (int)fontSize));
	        big.drawString(textFill,f.getTextAnchor().x,f.getTextAnchor().y); 
        }
    }
       
    
    //Aliasing arbitrary display option numbers with more sensible method names.
    //***************************************************
    public void showEdge() 		{ 	imageSelection = 1;   }    
    public void showFinal()  {  	imageSelection = 2;   }
    //***************************************************
    
    //Graphic output rendering rules
    public void paintComponent(Graphics g) {
        Graphics2D big = (Graphics2D) g;
        if (imageSelection == 0)
            big.drawImage(bim.getBim(), 0, 0, this);
        else if (imageSelection == 1)
        	big.drawImage(filteredbim.getBim(), 0, 0, this);
        else
            big.drawImage(buildComp(), 0, 0, this);
        if(selecting){  //as long as both points are defined
            big.setColor(Color.RED);
            int x=Math.min(selectionStart.x, selectionEnd.x);
            int y=Math.min(selectionStart.y, selectionEnd.y);
            int width=Math.abs(selectionStart.x - selectionEnd.x);
            int height=Math.abs(selectionStart.y - selectionEnd.y);
            big.drawRect(x,y,width,height);  //draw a red rectangle around the area the user is right click and drag selecting
        }        
        processText(big);
        big.setColor(Color.BLACK);        
    }
}
