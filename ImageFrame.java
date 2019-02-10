import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;

public class ImageFrame extends JFrame {
    // Instance variables
    private BufferedImage image;   // the image
    private MyImageObj view;       // a component in which to display an image
    private JLabel infoLabel;      // an informative label for the simple GUI
    private JButton EdgeDetectButton;
    private JButton ResetButton;
    private boolean isLDragging = false;
    private Point dragStart;

    // Constructor for the frame
    public ImageFrame () {
        super();				// call JFrame constructor
        this.buildMenus();		// helper method to build menus
        this.buildComponents();		// helper method to set up components
        this.buildDisplay();		// Lay out the components on the display
        this.buildMouseSettings();
    }

    private void buildMenus () {

        final JFileChooser fc = new JFileChooser(".");
        JMenuBar bar = new JMenuBar();
        this.setJMenuBar (bar);
        JMenu fileMenu = new JMenu ("File");
        JMenuItem fileopen = new JMenuItem ("Open");
        JMenuItem fileexit = new JMenuItem ("Exit");

        fileopen.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        int returnVal = fc.showOpenDialog(ImageFrame.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image = ImageIO.read(file);
                            } catch (IOException e1){};

                            view.setImage(image);
                            view.showImage();
                            ImageFrame.super.pack(); //updates window size based on new image loaded
                        }
                    }
                }
        );
        fileexit.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        System.exit(0);
                    }
                }
        );
        fileMenu.add(fileopen);
        fileMenu.add(fileexit);
        bar.add(fileMenu);
    }

    private void buildComponents() {
        view = new MyImageObj(readImage("textimage.png"));
        infoLabel = new JLabel("Original Image");
        ResetButton = new JButton("Reset");
        EdgeDetectButton = new JButton("Edge Detect");

        ResetButton.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        view.showImage();
                        infoLabel.setText("Original Image");
                    }
                }
        );

        EdgeDetectButton.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        view.filterImage();
                        infoLabel.setText("Edge Detect");
                    }
                }
        );
    }

    // This helper method adds all components to the content pane of the
    // JFrame object.  Specific layout of components is controlled here

    private void buildDisplay () {

        // Build first JPanel
        JPanel controlPanel = new JPanel();
        GridLayout grid = new GridLayout (1, 3);
        controlPanel.setLayout(grid);
        controlPanel.add(EdgeDetectButton);
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        controlPanel.add(infoLabel);
        controlPanel.add(ResetButton);

        Container c = this.getContentPane();
        c.add(view, BorderLayout.EAST);
        c.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void buildMouseSettings(){
        view.addMouseListener(new MouseListener(){  //track mouse interactions with left render panel
            public void mouseExited(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseReleased(MouseEvent e){
                isLDragging = false;  
                view.endSelection(); 
            } 
            public void mousePressed(MouseEvent e){
                	isLDragging = true;  //now dragging
                	dragStart = e.getPoint();               
            }
            public void mouseClicked(MouseEvent e){}
        });
        view.addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent e) {
                view.setSelection(dragStart,new Point(e.getPoint()));                
            }
            public void mouseMoved(MouseEvent e) {}
        });
    }
 

    public BufferedImage readImage (String file) {

        Image image = Toolkit.getDefaultToolkit().getImage(file);
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = null;
        try {
            bim = ImageIO.read(getClass().getResource(file)); 
        } catch (IOException ex) { //if error do this
            System.err.println(ex);
            ex.printStackTrace();
        }
        Graphics2D big = bim.createGraphics();
        big.drawImage (image, 0, 0, this);
        return bim;
    }

    public static void main(String[] argv) {

        JFrame frame = new ImageFrame();
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener (
                new WindowAdapter () {
                    public void windowClosing ( WindowEvent e) {
                        System.exit(0);
                    }
                }
        );
    }
}
