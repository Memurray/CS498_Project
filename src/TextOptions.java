import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextOptions extends JDialog {
    // Panel to hold GUI elements
    private JPanel panel;

    // Combo-boxes to choose options
    private JComboBox typefaceSelector, styleSelector, sizeSelector, alignmentSelector;

    // Submit button
    private JButton submit;

    public TextOptions(ImageFrame frame, String str, int s, int b) {
        // Pass ImageFrame and title str to JDialog constructor, set JDialog to modal so ImageFrame is disabled while in settings
        super(frame, str, true);

        // Instantiate panel
        panel = new JPanel();

        // Instantiate typeface combo-box
        typefaceSelector = new JComboBox();
        typefaceSelector.addItem("Arial");
        typefaceSelector.addItem("Courier");
        typefaceSelector.addItem("Times New Roman");

        // Define behavior for typeface selection
        typefaceSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();

                // Get the value of the user's selection
                String str = (String)cb.getSelectedItem();
            }
        });
        
        // Instantiate style combo-box
        styleSelector = new JComboBox();
        styleSelector.addItem("Normal");
        styleSelector.addItem("Bold");
        styleSelector.addItem("Italics");

        // Define behavior for style selection
        styleSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();

                // Get the value of the user's selection
                String str = (String)cb.getSelectedItem();
            }
        });

        // Instantiate size combo-box
        sizeSelector = new JComboBox();
        sizeSelector.addItem(12);
        sizeSelector.addItem(14);
        sizeSelector.addItem(16);

        // Define behavior for size selection
        sizeSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();

                // Get size selected
                int s = Integer.parseInt((String) cb.getSelectedItem());
            }
        });
        
        // Instantiate alignment combo-box
        alignmentSelector = new JComboBox();
        alignmentSelector.addItem("Left");
        alignmentSelector.addItem("Center");
        alignmentSelector.addItem("Right");

        // Define behavior for alignment selection
        alignmentSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();

                // Get the value of the user's selection
                String str = (String)cb.getSelectedItem();
            }
        });

        // Instantiate submit button
        submit = new JButton("Submit");

        // Define submit behavior
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close settings window
                dispose();
            }
        });

        Container c = getContentPane();

        // Instantiate labels

        // Set grid layout for panel
        panel.setLayout(new GridLayout(5, 1, 4, 2));

        // Add GUI elements to panel
        panel.add(typefaceSelector);
        panel.add(styleSelector);
        panel.add(sizeSelector);
        panel.add(alignmentSelector);
        panel.add(submit);

        // Display window
        c.add(panel);
        pack();
        setVisible(true);
    }
}
