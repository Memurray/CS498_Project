
public class pixel {
	public int r;
	public int g;
	public int b;
	
	public pixel() {
		r=0;
		g=0;
		b=0;
	}
	
	public pixel(int rgb) {	
		r = (rgb >> 16) & 255;  //split out red
		g = (rgb >> 8) & 255; //split out green
		b = rgb & 255; //split out blue
	}
	
	public pixel(int r, int g, int b) {	
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public int sum() {
		return r+b+g;
	}
	
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
	
	public int build() {
		return (r << 16) | (g << 8) | b;
	}
}
