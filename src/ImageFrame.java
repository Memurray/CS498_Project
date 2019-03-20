import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageFrame extends JFrame {
    // Instance variables
    private BufferedImage image;   // the image
    private MyImageObj view;       // a component in which to display an image
    private JLabel infoLabel;      // an informative label for the simple GUI
    private JButton EdgeDetectButton, FilterButton;
    private JButton ResetButton;
    private boolean isLDragging = false;
    private Point dragStart;
    private JLabel toleranceLabel;
    private JSlider toleranceSlider;
    JCheckBoxMenuItem vInterpolateToggle;
    JCheckBoxMenuItem textToggle;

    // Constructor for the frame
    public ImageFrame () {
        super("Text Replace");				// call JFrame constructor
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
        JMenuItem saveFile = new JMenuItem ("Save to File");
        JMenuItem fileexit = new JMenuItem ("Exit");
        JMenu optionsMenu = new JMenu ("Options");
        vInterpolateToggle = new JCheckBoxMenuItem("Vertical Interpolation");
        textToggle = new JCheckBoxMenuItem("Show Text");
        JMenuItem setText = new JMenuItem ("Set Text");        
        
        setText.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	TextSetWindow tWindow = new TextSetWindow(view);
                        tWindow.pack();
                        tWindow.setVisible(true);    
                        if(!textToggle.isSelected()) {
                    		textToggle.setSelected(true);
                    		view.toggleText(); 
                    	} 
                    }
                }
          );        
        
        vInterpolateToggle.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	view.toggleVint();                    	
                    }
                }
          );
        
        textToggle.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	view.toggleText();  
                    	view.repaint();
                    }
                }
          );
        
        fileopen.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	fc.setFileFilter(new FileNameExtensionFilter("Image files",new String[] { "png", "jpg", "jpeg", "gif" }));
                        int returnVal = fc.showOpenDialog(ImageFrame.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image = ImageIO.read(file);
                            } catch (IOException e1){};
                            view.setImage(image);
                            toleranceLabel.setText("  " + 55 + "% Boundary Tolerance");
                            toleranceSlider.setValue(55);
                            view.resetImage();
                            ImageFrame.super.pack(); //updates window size based on new image loaded
                        }
                    }
                }
        );
        
        saveFile.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	int retVal = fc.showSaveDialog(ImageFrame.this);
                    	File file = new File(fc.getSelectedFile() + ".png");
                    	if(retVal==JFileChooser.APPROVE_OPTION){
                    	    try {
								ImageIO.write(view.returnFinal(), "png", file);
							} catch (IOException e2) {}    
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
        fileMenu.add(saveFile);
        fileMenu.add(fileexit);
        optionsMenu.add(vInterpolateToggle);        
        optionsMenu.add(textToggle);        
        optionsMenu.add(setText);
        bar.add(fileMenu);
        bar.add(optionsMenu);
    }

    private void buildComponents() {
        view = new MyImageObj(readImage("textimage.png"));
        infoLabel = new JLabel("Original Image");
        ResetButton = new JButton("Reset");
        EdgeDetectButton = new JButton("Edge Detect");
        FilterButton = new JButton("Filter Image");
        toleranceLabel = new JLabel("  55% Boundary Tolerance");

        ResetButton.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {  
                    	if(textToggle.isSelected()) {
                    		textToggle.setSelected(false);
                    		view.toggleText(); 
                    	}                    	
                    	if(vInterpolateToggle.isSelected()) {
                    		vInterpolateToggle.setSelected(false);
                    		view.toggleVint();   
                    	}
                        toleranceSlider.setValue(55);
                        view.resetImage();
                        infoLabel.setText("Original Image");
                    }
                }
        );

        FilterButton.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        view.filterImage();
                        view.showFiltered();
                        infoLabel.setText("Filtered Image");
                    }
                }
        );
        
        
        EdgeDetectButton.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        view.filterImage();
                        view.showEdge();
                        infoLabel.setText("Edges Detected");
                    }
                }
        );       
        
        toleranceSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 55 );
        toleranceSlider.setMajorTickSpacing(25);
        toleranceSlider.setPaintTicks(true);
        toleranceSlider.setPaintLabels(true);
        toleranceSlider.addChangeListener(new ChangeListener(){
              public void stateChanged( ChangeEvent e ){  //when slider changed, update tick time
                  toleranceLabel.setText("  " + toleranceSlider.getValue() + "% Boundary Tolerance");
                  view.setTol(toleranceSlider.getValue());
              }
          }
        );
        
    }

    // This helper method adds all components to the content pane of the
    // JFrame object.  Specific layout of components is controlled here
    private void buildDisplay () {
        JPanel controlPanel = new JPanel();
        GridLayout grid = new GridLayout (2, 3);
        controlPanel.setLayout(grid);
        controlPanel.add(FilterButton);
        toleranceLabel.setHorizontalAlignment(JLabel.CENTER);
        controlPanel.add(toleranceLabel);
        controlPanel.add(ResetButton);
        controlPanel.add(EdgeDetectButton);
        controlPanel.add(toleranceSlider);
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        controlPanel.add(infoLabel);
        Container c = this.getContentPane();
        c.add(view, BorderLayout.EAST);
        c.add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void buildMouseSettings(){
        view.addMouseListener(new MouseListener(){  //track mouse interactions with left render panel
            public void mouseExited(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseReleased(MouseEvent e){
                if(isLDragging) {
	            	isLDragging = false;  
	                view.endSelection();
                }                
            } 
            public void mousePressed(MouseEvent e){                	
                	dragStart = e.getPoint();               
            }
            public void mouseClicked(MouseEvent e){}
        });
        view.addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent e) {
            	isLDragging = true;  //now dragging
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
