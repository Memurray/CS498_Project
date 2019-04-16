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
	private JButton GuessFontButton, FilterButton, ResetButton;
	private boolean isLDragging = false;
	private Point dragStart;
	private JLabel toleranceLabel;
	private JSlider toleranceSlider;
	JCheckBoxMenuItem vInterpolateToggle, textToggle;

	// Constructor for the frame
	public ImageFrame() {
		super("Text Replace");
		this.buildMenus(); // Helper method to build menus
		this.buildComponents(); // Helper method to set up components
		this.buildDisplay(); // Helper method to configure GUI
		this.buildMouseSettings(); // Helper method to configure mouse click responses
	}

	public BufferedImage getImage() {
		return image;
	}

	// Put together the top bar menu system
	private void buildMenus() {
		final JFileChooser fc = new JFileChooser(".");
		JMenuBar bar = new JMenuBar();
		this.setJMenuBar(bar);
		JMenu fileMenu = new JMenu("File");
		JMenuItem fileopen = new JMenuItem("Open");
		JMenuItem saveFile = new JMenuItem("Save to File");
		JMenuItem fileexit = new JMenuItem("Exit");
		JMenu optionsMenu = new JMenu("Options");
		vInterpolateToggle = new JCheckBoxMenuItem("Vertical Interpolation");
		textToggle = new JCheckBoxMenuItem("Show Text");
		JMenuItem setText = new JMenuItem("Set Text");

		//If setText option is clicked
		setText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.showTextWindow();
				if (!textToggle.isSelected()) {
					textToggle.setSelected(true);
					view.toggleText();
				}
			}
		});

		//If vertical interpolation option is clicked
		vInterpolateToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.toggleVint();
			}
		});

		//If show text option is clicked
		textToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.toggleText();
				view.repaint();
			}
		});

		//If open new file option is clicked
		fileopen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.resetChoosableFileFilters(); //clear old filters
				fc.setFileFilter(   //define what filters are considered images
						new FileNameExtensionFilter("Image files", new String[] { "png", "jpg", "jpeg", "gif" }));
				int returnVal = fc.showOpenDialog(ImageFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						image = ImageIO.read(file);  //read file to bufferedImage
					} catch (IOException e1) {
					}
					;
					view.setImage(image); //reset GUI configurations from last image
					toleranceLabel.setText("  " + 40 + "% Boundary Tolerance");  
					toleranceSlider.setValue(40);
					if (textToggle.isSelected()) {  //turn off text show option if it's on
						textToggle.setSelected(false);
						view.toggleText();
					}
					ImageFrame.super.pack(); // updates window size based on new image loaded
				}
			}
		});

		//If save to file option is clicked
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.resetChoosableFileFilters();  //reset filter options
				FileNameExtensionFilter jpg = new FileNameExtensionFilter(".jpg", "jpg");  //define acceptable file extensions
				FileNameExtensionFilter gif = new FileNameExtensionFilter(".gif", "gif");
				fc.addChoosableFileFilter(jpg);
				fc.addChoosableFileFilter(gif);
				fc.setFileFilter(new FileNameExtensionFilter(".png", "png"));  //default image type is .png
				int retVal = fc.showSaveDialog(ImageFrame.this);
				if (retVal == JFileChooser.APPROVE_OPTION) {
					try {
						String filetype = "png";
						if (fc.getFileFilter() == jpg)
							filetype = "jpg";
						else if (fc.getFileFilter() == gif)
							filetype = "gif";
						File file = new File(fc.getSelectedFile() + "." + filetype);  //append the correct filetype suffix to save file
						ImageIO.write(view.returnFinal(), filetype, file);
					} catch (IOException e2) {
					}
				}
			}
		});

		fileexit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(fileopen);
		fileMenu.add(saveFile);
		fileMenu.add(fileexit);
		optionsMenu.add(vInterpolateToggle);
		optionsMenu.add(textToggle);
		optionsMenu.add(setText);
		bar.add(fileMenu);
		bar.add(optionsMenu);
	}

	// Define GUI elements and their event handlers
	private void buildComponents() {
		image = readImage("textimage.png");
		view = new MyImageObj(image);
		infoLabel = new JLabel("Original Image");
		ResetButton = new JButton("Reset");
		GuessFontButton = new JButton("Guess Font");
		FilterButton = new JButton("Filter Image");
		toleranceLabel = new JLabel("  40% Boundary Tolerance");

		ResetButton.addActionListener( // If Reset button clicked
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (textToggle.isSelected()) {
							textToggle.setSelected(false);
							view.toggleText();
						}
						if (vInterpolateToggle.isSelected()) {
							vInterpolateToggle.setSelected(false);
							view.toggleVint();
						}
						toleranceSlider.setValue(40);
						view.resetImage();
						infoLabel.setText("Original Image");
					}
				});

		FilterButton.addActionListener( // If Filter button clicked
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						view.filterImage();
						view.showFinal();
						infoLabel.setText("Filtered Image");
					}
				});

		GuessFontButton.addActionListener( // If Edge Detect button is clicked
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						view.filterImage();
						view.openGuessWindow();
						view.showOriginal();
						infoLabel.setText("Original Image");
					}
				});

		// Set up Slider bar
		toleranceSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 40);
		toleranceSlider.setMajorTickSpacing(25);
		toleranceSlider.setPaintTicks(true);
		toleranceSlider.setPaintLabels(true);
		toleranceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) { // when slider changed, update tick time
				toleranceLabel.setText("  " + toleranceSlider.getValue() + "% Boundary Tolerance");
				view.setTol(toleranceSlider.getValue());
			}
		});

	}

	// Set up specific responses to different mouse events
	private void buildMouseSettings() {
		view.addMouseListener(new MouseListener() {
			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
				if (isLDragging) {
					isLDragging = false;
					view.endSelection();
				}
			}

			public void mousePressed(MouseEvent e) {
				dragStart = e.getPoint();
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
		view.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				isLDragging = true;
				view.setSelection(dragStart, new Point(e.getPoint()));
			}

			public void mouseMoved(MouseEvent e) {
			}
		});
	}

	// Set up GUI layout
	private void buildDisplay() {
		JPanel controlPanel = new JPanel();
		GridLayout grid = new GridLayout(2, 3);
		controlPanel.setLayout(grid);
		controlPanel.add(FilterButton);
		toleranceLabel.setHorizontalAlignment(JLabel.CENTER);
		controlPanel.add(toleranceLabel);
		controlPanel.add(ResetButton);
		controlPanel.add(GuessFontButton);
		controlPanel.add(toleranceSlider);
		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		controlPanel.add(infoLabel);
		Container c = this.getContentPane();
		c.add(view, BorderLayout.EAST);
		c.add(controlPanel, BorderLayout.SOUTH);
	}

	// Handles processing image file into the usable BufferedImage format
	public BufferedImage readImage(String file) {
		Image image = Toolkit.getDefaultToolkit().getImage(file);
		MediaTracker tracker = new MediaTracker(new Component() {
		});
		tracker.addImage(image, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
		}
		BufferedImage bim = null;
		try {
			bim = ImageIO.read(getClass().getResource(file));
		} catch (IOException ex) { // if error do this
			System.err.println(ex);
			ex.printStackTrace();
		}
		Graphics2D big = bim.createGraphics();
		big.drawImage(image, 0, 0, this);
		return bim;
	}

	public static void main(String[] argv) {
		JFrame frame = new ImageFrame();
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
