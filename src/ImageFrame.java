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
    private BufferedImage image;   
    private MyImageObj view;      
    private JLabel infoLabel;      
    private JButton EdgeDetectButton, FilterButton, ResetButton;
    private boolean isLDragging = false;
    private Point dragStart;
    private JLabel toleranceLabel;
    private JSlider toleranceSlider;
    JCheckBoxMenuItem vInterpolateToggle,textToggle;

    // Constructor for the frame
    public ImageFrame () {
        super("Text Replace");		
        this.buildMenus();			// Helper method to build menus
        this.buildComponents();		// Helper method to set up components
        this.buildDisplay();		// Helper method to configure GUI
        this.buildMouseSettings();	// Helper method to configure mouse click responses
    }

    //Put together the top bar menu system
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

    //Define GUI elements and their event handlers
    private void buildComponents() {
        view = new MyImageObj(readImage("textimage.png"));
        infoLabel = new JLabel("Original Image");
        ResetButton = new JButton("Reset");
        EdgeDetectButton = new JButton("Edge Detect");
        FilterButton = new JButton("Filter Image");
        toleranceLabel = new JLabel("  55% Boundary Tolerance");

        ResetButton.addActionListener( //If Reset button clicked
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

        FilterButton.addActionListener(  //If Filter button clicked
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        view.filterImage();
                        view.showFiltered();
                        infoLabel.setText("Filtered Image");
                    }
                }
        );
        
        
        EdgeDetectButton.addActionListener( //If Edge Detect button is clicked
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        view.filterImage();
                        view.showEdge();
                        infoLabel.setText("Edges Detected");
                    }
                }
        );       
        
        // Set up Slider bar
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
    
    // Set up specific responses to different mouse events
    private void buildMouseSettings(){
        view.addMouseListener(new MouseListener(){ 
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
            	isLDragging = true; 
                view.setSelection(dragStart,new Point(e.getPoint()));                
            }
            public void mouseMoved(MouseEvent e) {}
        });
    } 
    
 // Set up GUI layout
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
    
    // Handles processing image file into the usable BufferedImage format
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