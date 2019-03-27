import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class TextSetWindow extends JFrame{
	private TextField textbox;
	private JButton okButton;
	private JLabel topMessage; 
	
	public TextSetWindow (final MyImageObj MIO) {  								//Main logic object is passed in to allow variable manipulation
        super("Set Text");	
        topMessage = new JLabel("Type what you want the text to read.");
        textbox = new TextField(MIO.getDisplayText());							//Textbox defaults to the current text being rendered
        okButton = new JButton("OK");    
        
        okButton.addActionListener(												//If ok button is clicked
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	MIO.setDisplayText(textbox.getText());					//Update display text to value user typed
                    	MIO.repaint();
                        setVisible(false);
                    	dispose();
                    }
                }
        );        
        
        // General formatting of display   //
        Container c = this.getContentPane();	
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridLayout grid = new GridLayout (3,1,5,5);
        panel.setLayout(grid);
        panel.add(topMessage);
        panel.add(textbox); 
        panel.add(okButton); 
        c.add(panel);
    }
}