package monte_carlo_tree_search.domains.elevator;

import black_jack.models_cards.StateInterface;
import black_jack.result_drawer.GridPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FrameAndPanelCreatorElevator {

  public static GridPanel createPanel(String frameTitle, String xLabel, String yLabel) {
        List<Integer> xSet = getRange(1);
        List<Integer> ySet = getRange(EnvironmentElevator.MAX_POS);


      JFrame frame = new JFrame(frameTitle);  // Create a window with "Grid" in the title bar.
        GridPanel panel = new GridPanel(xSet, ySet, xLabel, yLabel);  // Create an object of type Grid.
        frame.setContentPane(panel);  // Add the Grid panel to the window.
        fixFrame(frame);
        return panel;
    }


    private static void fixFrame(JFrame frame) {
        frame.pack(); // Set the size of the window based on the panel's preferred size.
        Dimension screenSize; // A simple object containing the screen's width and height.
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int top, left;  // position for top left corner of the window
        left = (screenSize.width - frame.getWidth()) / 2;
        top = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(left, top);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    static List<Integer> getRange(int maxValue) {
        return IntStream.rangeClosed(0, maxValue).boxed().collect(Collectors.toList());
    }

}
