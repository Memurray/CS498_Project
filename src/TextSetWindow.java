import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.awt.GraphicsEnvironment;

public class TextSetWindow extends JFrame {
	private TextField textbox;
	private String text = "TEXT";
	private int fontSize = 0;
	private String fontName = "Auto";
	private Color color;
	private JLabel colorLabel;
	private JRadioButton pickerRadioButton, autoRadioButton;
	private ColorChoice colorWindow = new ColorChoice(this);
	private Boolean lastSelect = false;
	private int copy_fontSize;
	private String copy_fontName, copy_text;
	private JComboBox<String> fontNameList, fontSizeList;

	//Configre the large number of GUI elements and variables contained
	public TextSetWindow(final MyImageObj MIO) {
		super("Set Text");
		color = new Color(0, 0, 0);
		colorWindow.pack();
		JLabel topMessage = new JLabel("Type what you want the text to read.");
		JLabel fontNameMessage = new JLabel("Font:");
		JLabel fontSizeMessage = new JLabel("Font Size:");
		JLabel colorMessage = new JLabel("Color Selection:");
		textbox = new TextField(text); // Textbox defaults to the current text being rendered
		JButton okButton = new JButton("OK");
		JButton previewButton = new JButton("Preview");
		JButton openColorChooserButton = new JButton("Open Color Chooser");
		colorLabel = new JLabel();
		colorLabel.setBackground(color);
		colorLabel.setOpaque(true);
		String fontSizes[] = new String[] { "Auto", "8", "10", "12", "16", "24", "32", "48", "72", "100", "120", "150",
				"256" };
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fonts[0] = "Auto";
		autoRadioButton = new JRadioButton("Auto");
		autoRadioButton.setSelected(true);
		pickerRadioButton = new JRadioButton("Color Picker");
		ButtonGroup group = new ButtonGroup();
		group.add(autoRadioButton);
		group.add(pickerRadioButton);
		fontNameList = new JComboBox<String>(fonts);
		fontNameList.setSelectedIndex(0);
		fontSizeList = new JComboBox<String>(fontSizes);
		fontSizeList.setSelectedIndex(0);
		makeCopies();

		okButton.addActionListener( // If ok button is clicked
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						lastSelect = pickerRadioButton.isSelected();
						if ((String) fontSizeList.getSelectedItem() == "Auto")
							fontSize = 0;
						else
							fontSize = Integer.parseInt((String) fontSizeList.getSelectedItem());
						fontName = (String) fontNameList.getSelectedItem();
						text = textbox.getText();
						MIO.repaint();
						setVisible(false);
						makeCopies();  //overwrite variable copies, this exists so that preview variable are only finalized when hitting ok
					}
				});

		previewButton.addActionListener( // If preview button is clicked
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if ((String) fontSizeList.getSelectedItem() == "Auto")
							fontSize = 0;
						else
							fontSize = Integer.parseInt((String) fontSizeList.getSelectedItem());
						fontName = (String) fontNameList.getSelectedItem();
						text = textbox.getText();
						MIO.repaint();
					}
				});

		openColorChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorWindow.setVisible(true);
			}
		});

		pickerRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorWindow.setVisible(true);
			}
		});

		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(autoRadioButton);
		radioPanel.add(pickerRadioButton);
		// General formatting of display //
		Container c = this.getContentPane();

		JPanel panel = new JPanel(new BorderLayout());
		JPanel generalPanel = new JPanel(new GridLayout(3, 2, 5, 5));
		JPanel colorPanel = new JPanel(new GridLayout(1, 4, 5, 5));
		JPanel finalizePanel = new JPanel(new GridLayout(1, 2, 5, 20));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		generalPanel.add(topMessage);
		generalPanel.add(textbox);
		generalPanel.add(fontNameMessage);
		generalPanel.add(fontNameList);
		generalPanel.add(fontSizeMessage);
		generalPanel.add(fontSizeList);
		colorPanel.add(colorMessage);
		colorPanel.add(radioPanel);
		colorPanel.add(openColorChooserButton);
		colorPanel.add(colorLabel);
		colorPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		finalizePanel.add(previewButton);
		finalizePanel.add(okButton);
		panel.add(generalPanel, BorderLayout.NORTH);
		panel.add(colorPanel, BorderLayout.CENTER);
		panel.add(finalizePanel, BorderLayout.PAGE_END);
		c.add(panel);
	}

	//******************************************************
	//Getters and Setters Start 
	//******************************************************

	public void setColor(Color input_color) {
		color = input_color;
		colorLabel.setBackground(color);
		pickerRadioButton.setSelected(true);
		repaint();
	}

	public String getText() {
		return text;
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getFontName() {
		return fontName;
	}

	public Color getColor() {
		return color;
	}
	
	//******************************************************
	//Getters and Setters End
	//******************************************************
	
	private void makeCopies() {
		copy_fontSize = fontSize;
		copy_fontName = fontName;
		copy_text = text;
	}

	//If changes aren't finalized, reset final variable assignments to their previous configuration
	public void setback() {
		fontSize = copy_fontSize;
		fontName = copy_fontName;
		text = copy_text;
		fontNameList.getModel().setSelectedItem(fontName);
		String size = Integer.toString(fontSize);
		if (fontSize == 0) {
			size = "Auto";
		}
		fontSizeList.getModel().setSelectedItem(size);
		textbox.setText(text);
		if (lastSelect)
			pickerRadioButton.setSelected(true);
		else
			autoRadioButton.setSelected(true);
	}

	public Boolean isManualColor() {
		return pickerRadioButton.isSelected();
	}
}