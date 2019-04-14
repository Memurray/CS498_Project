
public class pixel {  
	//Variable are public because this is intended only as an augmented primative class
	public int r;
	public int g;
	public int b;
	
	public pixel() {
		r=0;
		g=0;
		b=0;
	}
	
	public pixel(int rgb) {		//if rgb saved all in same integer
		r = (rgb >> 16) & 255;  //split out red channel
		g = (rgb >> 8) & 255;	//split out green channel
		b = rgb & 255; 			//split out blue channel
	}
	
	public pixel(int r, int g, int b) {	
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public int sum() {
		return r+b+g;
	}
	
	//If any channel is above threshold, make pixel white
	public void allThreshold(int thres) {
        if(r > thres || g > thres || b >thres) {
        	r=255;	
        	g =255;
        	b=255;
        }
        else {
        	r=0;	
        	g =0;
        	b=0;
        }
	}
	
	public void makeRed() {
		r=255;	
    	g =0;
    	b=0;
	}
	
	public boolean isWhite() {
		if(r ==255 && g ==255 && b==255)
			return true;
		return false;
	}
	
	//Returns color channels back as standard 1 integer value used for Buffered Images
	public int build() {
		return (r << 16) | (g << 8) | b;
	}
}
