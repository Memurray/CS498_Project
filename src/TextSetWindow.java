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
	public TextSetWindow (MyImageObj MIO) {
        super("Set Text");	
        topMessage = new JLabel("Type what you want the text to read.");
        textbox = new TextField(MIO.getDisplayText());
        okButton = new JButton("OK");    
        
        okButton.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	MIO.setDisplayText(textbox.getText());
                    	MIO.repaint();
                        setVisible(false);
                    	dispose();
                    }
                }
        );
        
        Container c = this.getContentPane();
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridLayout grid = new GridLayout (3, 1,5,5);
        panel.setLayout(grid);
        panel.add(topMessage);
        panel.add(textbox); 
        panel.add(okButton); 
        c.add(panel);
    }
}