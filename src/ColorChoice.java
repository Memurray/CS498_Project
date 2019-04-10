import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


//Basic object wrapper for JColorChooser tool part of the java library


@SuppressWarnings("serial")
public class ColorChoice extends JFrame { 
    protected JColorChooser tcc;
    private Color color;
    JFrame colorChooser;
    
    public ColorChoice(final TextSetWindow TSW) {
    	super("Font Color Chooser");
    	colorChooser =  new JFrame();        
        colorChooser.setLayout(new BorderLayout()); 
        tcc = new JColorChooser();
        tcc.setBorder(BorderFactory.createTitledBorder("Choose Text Color"));         
        JButton okButton = new JButton("OK"); 
        okButton.addActionListener(												
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                    	color = tcc.getColor();
                    	TSW.setColor(color);
                    	setVisible(false);
                    }
                }
        );          
        add(tcc, BorderLayout.CENTER);
        add(okButton, BorderLayout.PAGE_END);        
    }
    
    public Color getColor() {
    	return color;
    }     
}