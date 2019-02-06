import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class MyImageObj extends JLabel {

    // instance variable to hold the buffered image
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;

    //  tell the paintcomponent method what to draw
    private boolean showfiltered=false;



    private final float[] EdgeDetect =
            {
                    -1,-1,-1,
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
        filteredbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
        this.repaint();
    }

    // This mutator changes the image by resetting what is stored
    // The input parameter img is the new image;  it gets stored as an
    //     instance variable
    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        filteredbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
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
        showfiltered=true;
        this.repaint();
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
