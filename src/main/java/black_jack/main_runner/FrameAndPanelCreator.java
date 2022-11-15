package black_jack.main_runner;

import black_jack.models_cards.StateInterface;
import black_jack.result_drawer.GridPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FrameAndPanelCreator {

    @NotNull
    static GridPanel createUsableAceFrameAndPanel(String frameTitle, String xLabel, String yLabel) {
        List<Integer> xSet = StateInterface.getDealerCardList();
        List<Integer> ySet = StateInterface.getHandsSumList();
        JFrame frameUsableAce = new JFrame(frameTitle);  // Create a window with "Grid" in the title bar.
        GridPanel panelUsableAce = new GridPanel(xSet, ySet, xLabel, yLabel);  // Create an object of type Grid.
        frameUsableAce.setContentPane(panelUsableAce);  // Add the Grid panel to the window.
        fixFrame(frameUsableAce);
        return panelUsableAce;
    }

    @NotNull
    static GridPanel createNoUsableAceFrameAndPanel(String frameTitle, String xLabel, String yLabel) {
        List<Integer> xSet = StateInterface.getDealerCardList();
        List<Integer> ySet = StateInterface.getHandsSumList();
        JFrame frameNoUsableAce = new JFrame(frameTitle);  // Create a window with "Grid" in the title bar.
        GridPanel panelNoUsableAce = new GridPanel(xSet, ySet, xLabel, yLabel);  // Create an object of type Grid.
        frameNoUsableAce.setContentPane(panelNoUsableAce);  // Add the Grid panel to the window.
        fixFrame(frameNoUsableAce);
        return panelNoUsableAce;
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

}
