import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class FloodRegion {
	private static int[] source_fillArray;
	private static BufferedImage source_bim;
	private static int source_height, source_width;
	private static int[] source_rgbData;
	private Point topLeft, bottomRight;
	private int height, width;
	private int[] fillArray;
	private BufferedImage cleanbim, finalbim = null;
	private Stack<Integer> st;
	private int padding = 4;
	private Color fontColor;

	public FloodRegion(Point startCoord, double tolerance) {
		process(startCoord, tolerance);
	}

	public FloodRegion(int[] fillArray, BufferedImage bim) {
		setupStatic(fillArray, bim);
	}

	public FloodRegion(int[] fillArray, BufferedImage bim, Point startCoord, double tolerance) {
		setupStatic(fillArray, bim);
		process(startCoord, tolerance);
	}

	//repeated code pushed off to own method
	private void process(Point startCoord, double tolerance) {
		pixel p = new pixel(source_rgbData[retPixel(startCoord.x, startCoord.y, source_width)]);  //grab pixel data at start coord
		int[] temp_fillArray = expand(startCoord, tolerance);  //run flood fill, copying text location array to temp
		fontColor = new Color(p.r, p.g, p.b);  //font color auto set to color of this element
		addPadding();  
		height = bottomRight.y - topLeft.y;
		width = bottomRight.x - topLeft.x;
		fillArray = new int[height * width];
		buildFillArray(temp_fillArray);  //create the fillArray based on temp array from flood fill
		softenOut(3);  //expand borders of array to allow some background, helps with interpolation seeding
		buildBim();   //build the new image as the appropriate subset of the original full image
		catchHorizontal();  //initiate horizontal interpolation chain
	}

	//repeated code pushed to own method, initializes static variables
	private void setupStatic(int[] fillArray, BufferedImage bim) {
		source_fillArray = fillArray;
		source_bim = bim;
		source_height = source_bim.getHeight();
		source_width = source_bim.getWidth();
		source_rgbData = new int[source_width * source_height];
		source_bim.getRGB(0, 0, source_width, source_height, source_rgbData, 0, source_width);
	}

	// ********************************************************
	// Getters Start
	// ********************************************************
	public BufferedImage getfinalbim() {
		return finalbim;
	}

	public BufferedImage getcleanbim() {
		return cleanbim;
	}

	public Point getTopLeft() {
		return topLeft;
	}

	public Point getBottomRight() {
		return bottomRight;
	}

	public Point getDim() {
		return new Point(width, height);
	}

	public int getPad() {
		return padding;
	}

	public int[] getFillArray() {
		return fillArray;
	}

	public int getTextHeight() {
		return height - padding * 2;
	}

	public Point getTextAnchor() {
		return new Point(topLeft.x + padding - 6, bottomRight.y - padding + 1);
	}

	public Color getColor() {
		return fontColor;
	}
	// ********************************************************
	// Getters End
	// ********************************************************

	//copies relevant portion of full fillarray into the fillArray stored as a variable in this class
	private void buildFillArray(int[] temp_fillArray) {
		int it_fillArray, it_tempArray;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				it_fillArray = retPixel(j, i, width);
				it_tempArray = retPixel(topLeft.x + j, topLeft.y + i, source_width);
				fillArray[it_fillArray] = temp_fillArray[it_tempArray];
			}
		}
	}

	//builds buffered image as a subset of the full image
	private void buildBim() {
		int it_destRGB, it_sourceRGB;
		int[] dest_rgbData = new int[height * width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				it_destRGB = retPixel(j, i);
				it_sourceRGB = retPixel(topLeft.x + j, topLeft.y + i, source_width);
				dest_rgbData[it_destRGB] = source_rgbData[it_sourceRGB];
			}
		}
		cleanbim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		cleanbim.setRGB(0, 0, width, height, dest_rgbData, 0, width);
		finalbim = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		finalbim.setRGB(0, 0, width, height, dest_rgbData, 0, width);
	}

	//Slightly expands region of interest to allow room for interpolation seeding
	private void addPadding() {
		topLeft.x = Math.max(0, topLeft.x - padding);
		topLeft.y = Math.max(0, topLeft.y - padding);
		bottomRight.x = Math.min(source_width - 1, bottomRight.x + padding);
		bottomRight.y = Math.min(source_height - 1, bottomRight.y + padding);
	}

	//initiate flood fill from startCoordinate
	private int[] expand(Point startCoord, double tolerance) {
		int startPixel = retPixel(startCoord.x, startCoord.y, source_width);
		pixel p1 = new pixel(source_rgbData[startPixel]);
		int[] temp_fillArray = new int[source_width * source_height];
		temp_fillArray[startPixel] = 1;
		topLeft = new Point(startCoord);
		bottomRight = new Point(startCoord);
		st = new Stack<Integer>();
		st.push(startPixel);
		while (!st.empty()) {
			int popped = st.pop();
			int row = popped / source_width;
			int col = popped - row * source_width;
			pixel p2 = new pixel(source_rgbData[retPixel(col, row, source_width)]);

			if (row > 0 && temp_fillArray[retPixel(col, row - 1, source_width)] == 0) {
				int pixelIndex = retPixel(col, row - 1, source_width);
				temp_fillArray[pixelIndex] = expandHelper(pixelIndex, tolerance, p2, p1);
			}
			if (row < source_height - 1 && temp_fillArray[retPixel(col, row + 1, source_width)] == 0) {
				int pixelIndex = retPixel(col, row + 1, source_width);
				temp_fillArray[pixelIndex] = expandHelper(pixelIndex, tolerance, p2, p1);
			}
			if (col > 0 && temp_fillArray[retPixel(col - 1, row, source_width)] == 0) {
				int pixelIndex = retPixel(col - 1, row, source_width);
				temp_fillArray[pixelIndex] = expandHelper(pixelIndex, tolerance, p2, p1);
			}
			if (col < source_width - 1 && temp_fillArray[retPixel(col + 1, row, source_width)] == 0) {
				int pixelIndex = retPixel(col + 1, row, source_width);
				temp_fillArray[pixelIndex] = expandHelper(pixelIndex, tolerance, p2, p1);
			}
			if (col < topLeft.x)
				topLeft.x = col;
			if (row < topLeft.y)
				topLeft.y = row;
			if (col > bottomRight.x)
				bottomRight.x = col;
			if (row > bottomRight.y)
				bottomRight.y = row;
		}
		return temp_fillArray;
	}

	//repeated code from flood fill operation, refactors to own method
	private int expandHelper(int pixelIndex, double tolerance, pixel p2, pixel p3) {
		double call_colorOffset, neighbor_colorOffset;
		int returnVal = -1;
		pixel p1 = new pixel(source_rgbData[pixelIndex]);
		call_colorOffset = Math
				.sqrt((Math.pow(p3.r - p1.r, 2) + Math.pow(p3.g - p1.g, 2) + Math.pow(p3.b - p1.b, 2)) / 3);
		neighbor_colorOffset = Math
				.sqrt((Math.pow(p2.r - p1.r, 2) + Math.pow(p2.g - p1.g, 2) + Math.pow(p2.b - p1.b, 2)) / 3);
		if (call_colorOffset <= tolerance && neighbor_colorOffset <= tolerance && source_fillArray[pixelIndex] != 4) {
			returnVal = 1;
			st.push(pixelIndex);
		}
		return returnVal;
	}

	private int retPixel(int col, int row, int width) { // common operation for getting 1-d array location from 2-d
		// input criteria using arguement width
		return row * width + col;
	}

	private int retPixel(int col, int row) { // common operation for getting 1-d array location from 2-d input criteria,
		// using object width
		return row * width + col;
	}

	//expands in all directions the fillArray so as to ensure complete masking of original text
	private void softenOut(int loopCount) {
		if (loopCount < 1)
			loopCount = 1;
		else if (loopCount > 10)
			loopCount = 10;
		for (int i = 0; i < loopCount; i++) {
			int tempFillArray[] = new int[width * height];
			for (int row = 1; row < height - 1; row++) { // for each row
				for (int col = 1; col < width - 1; col++) {
					//This looks ugly but it's just the eight neighboring pixels to the current one
					int sum = fillArray[retPixel(col, row + 1)] + fillArray[retPixel(col, row - 1)]
							+ fillArray[retPixel(col + 1, row)] + fillArray[retPixel(col - 1, row)]
									+ fillArray[retPixel(col + 1, row + 1)] + fillArray[retPixel(col - 1, row - 1)]
											+ fillArray[retPixel(col + 1, row - 1)] + fillArray[retPixel(col - 1, row + 1)];
					if (sum > 0 || fillArray[retPixel(col, row)] == 1)
						tempFillArray[retPixel(col, row)] = 1;

				}
			}
			fillArray = tempFillArray;
		}
	}

	//find where interpolation is needed to perform background fill
	private void catchHorizontal() {
		int state = 0;
		Point P1 = new Point();
		Point P2 = new Point();
		for (int row = 0; row < height; row++) { // for each row
			state = 0;
			for (int col = 0; col < width; col++) {
				if (fillArray[retPixel(col, row, width)] == 1 && state == 0) {
					state = 1;
					P1 = new Point(col - 1, row);
				} else if (fillArray[retPixel(col, row, width)] != 1 && state == 1) {
					state = 0;
					P2 = new Point(col, row);
					hInterpolate(P1, P2);
				}
			}
		}
	}

	// interpolate between the pixel color at P1 and P2 and write to output image
	private void hInterpolate(Point P1, Point P2) {
		int[] rgbim1 = new int[width]; // row of pixel data for inputBim
		int row = P1.y;
		finalbim.getRGB(0, row, width, 1, rgbim1, 0, width); // save that row's pixel data to array
		pixel p1 = new pixel(rgbim1[P1.x]);
		pixel p2 = new pixel(rgbim1[P2.x]);
		double gap = P2.x - P1.x + 0.0;
		for (int i = 1; i < gap; i++) {
			int r3 = (int) ((p2.r - p1.r) * i / gap + p1.r);
			int g3 = (int) ((p2.g - p1.g) * i / gap + p1.g);
			int b3 = (int) ((p2.b - p1.b) * i / gap + p1.b);
			rgbim1[P1.x + i] = (r3 << 16) | (g3 << 8) | b3; // build rgb for that pixel
		}
		finalbim.setRGB(0, row, width, 1, rgbim1, 0, width); // modify this row to new rules
	}

	//find where vertical interpolation is needed to fill over background
	public void catchVertical() {
		int state = 0;
		Point P1 = new Point();
		Point P2 = new Point();
		int[] ffaTranspose = new int[width * height];
		int i = 0;
		// Transpose array to reduce cache inefficiency of seeking through array rows
		// before columns
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				ffaTranspose[i] = fillArray[retPixel(col, row)];
				i++;
			}
		}
		int[] rgbim1 = new int[width * height]; // row of pixel data for inputBim
		finalbim.getRGB(0, 0, width, height, rgbim1, 0, width);
		for (int row = 0; row < width; row++) { // for each row
			state = 0;
			for (int col = 0; col < height; col++) {
				if (ffaTranspose[row * height + col] == 1 && state == 0) {
					state = 1;
					P1 = new Point(row, col - 1);
				} else if (ffaTranspose[row * height + col] != 1 && state == 1) {
					state = 0;
					P2 = new Point(row, col);
					vInterpolate(P1, P2, rgbim1);
				}
			}
		}
		finalbim.setRGB(0, 0, width, height, rgbim1, 0, width); // modify this row to new rules
	}

	// interpolate between the pixel color at P1 and P2 and write to output image
	private void vInterpolate(Point P1, Point P2, int[] rgbim1) {
		int col = P1.x;
		pixel p1 = new pixel(rgbim1[retPixel(col, P1.y)]);
		pixel p2 = new pixel(rgbim1[retPixel(col, P2.y)]);
		double gap = P2.y - P1.y + 0.0;
		int startCoord = retPixel(P1.x, P1.y);
		for (int i = 1; i < gap; i++) {
			pixel p4 = new pixel(rgbim1[startCoord + i * width]);
			int r = (int) (((p2.r - p1.r) * i / gap + p1.r) + p4.r) / 2;
			int g = (int) (((p2.g - p1.g) * i / gap + p1.g) + p4.g) / 2;
			int b = (int) (((p2.b - p1.b) * i / gap + p1.b) + p4.b) / 2;
			pixel p3 = new pixel(r, g, b);
			rgbim1[startCoord + i * width] = p3.build(); // build rgb for that pixel
		}
	}
}