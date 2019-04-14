import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class FontGuessWindow extends JFrame{
	private TextField textbox;
	private String text = "";
	private JButton okButton;
	private String fontName = "default";
	
	
	public FontGuessWindow (final MyImageObj MIO) {  						
        super("Letter Help");	
        JLabel topMessage = new JLabel("What is the first letter?");
        textbox = new TextField(text);							
        okButton = new JButton("OK"); 

        okButton.addActionListener(			//If ok button is clicked
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {                   	
                    	text = textbox.getText();
                    	if (text.length() > 0)
                    		fontName = MIO.genLetter(text.substring(0, 1));
                        setVisible(false); 
                    }
                }
        );  
        
        
        Container c = this.getContentPane();        
        JPanel panel = new JPanel(new BorderLayout());
        JPanel generalPanel = new JPanel(new GridLayout (2,1,5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        generalPanel.add(topMessage);
        generalPanel.add(textbox);  
        panel.add(generalPanel, BorderLayout.NORTH);
        panel.add(okButton, BorderLayout.PAGE_END);
        c.add(panel);
    }
		
	public String getFontName() { return fontName;}	
}