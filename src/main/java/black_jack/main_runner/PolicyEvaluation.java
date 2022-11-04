package black_jack.main_runner;

import black_jack.result_drawer.Grid;
import black_jack.result_drawer.GridPanel;

import javax.swing.*;
import java.awt.*;

public class PolicyEvaluation {

    public static void main(String[] args) {
        JFrame window; // The object that represents the window.
        window = new JFrame("Grid");  // Create a window with "Grid" in the title bar.
        GridPanel panel = new GridPanel(20,20,30);  // Create an object of type Grid.
        window.setContentPane( panel );  // Add the Grid panel to the window.
        window.pack(); // Set the size of the window based on the panel's preferred size.
        Dimension screenSize; // A simple object containing the screen's width and height.
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int top, left;  // position for top left corner of the window
        left = ( screenSize.width - window.getWidth() ) / 2;
        top = ( screenSize.height - window.getHeight() ) / 2;
        window.setLocation(left,top);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
