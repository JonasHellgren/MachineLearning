package black_jack.main_runner;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.Learner;
import black_jack.models.*;
import black_jack.policies.HitBelow20Policy;
import black_jack.policies.PolicyInterface;
import black_jack.result_drawer.GridPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/***
 *      Candidate parameter setting:
 *     private static final double ALPHA = 0.9;  //critical parameter setting
 *     private static final boolean NOF_VISITS_FLAG = true;  //critical parameter setting
 */

public class PolicyEvaluation {

    public static final int NOF_EPISODES = 1_00_000;
    private static final int LOWER_HANDS_SUM_PLAYER = 10;
    static final int MAX_HANDS_SUM_PLAYER = 21;
    private static final int MIN_DEALER_CARD = 1;
    static final int MAX_DEALER_CARD = 10;
    private static final double MIN_VALUE = -1d;
    private static final double MAX_VALUE = 1.5d;

    private static final double ALPHA = 0.01;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;  //critical parameter setting
    private static final String X_LABEL = "Dealer card";
    private static final String Y_LABEL = "Player sum";


    public static void main(String[] args) {
        ValueMemory valueMemory = new ValueMemory();
        playBlackJackManyTimesAndSetValueMemory(valueMemory);

        GridPanel panelNoUsableAce = createNoUsableAceFrameAndPanel();
        GridPanel panelUsableAce = createUsableAceFrameAndPanel();
        showValueMemory(panelNoUsableAce, panelUsableAce, valueMemory);
    }

    @NotNull
    private static List<Integer> getYset() {
        List<Integer> ySet = IntStream.rangeClosed(LOWER_HANDS_SUM_PLAYER, MAX_HANDS_SUM_PLAYER).boxed().collect(Collectors.toList());
        return ySet;
    }

    @NotNull
    private static List<Integer> getXset() {
        return IntStream.rangeClosed(MIN_DEALER_CARD, MAX_DEALER_CARD).boxed().collect(Collectors.toList());
    }

    private static void showValueMemory(GridPanel panelNoUsableAce, GridPanel panelUsableAce, ValueMemory valueMemory) {
        boolean usableAce = false;
        setPanel(panelNoUsableAce, valueMemory, usableAce);
        usableAce = true;
        setPanel(panelUsableAce, valueMemory, usableAce);

        panelNoUsableAce.repaint();
        panelUsableAce.repaint();
    }

    private static void playBlackJackManyTimesAndSetValueMemory(ValueMemory valueMemory) {
        EnvironmentInterface environment = new BlackJackEnvironment();
        PolicyInterface policy = new HitBelow20Policy();
        EpisodeRunner episodeRunner = new EpisodeRunner(environment, policy);
        ReturnsForEpisode returnsForEpisode = new ReturnsForEpisode();
        NumberOfVisitsMemory numberOfVisitsMemory = new NumberOfVisitsMemory();
        Learner learner = new Learner(valueMemory, numberOfVisitsMemory, ALPHA, NOF_VISITS_FLAG);
        for (int episodeNumber = 0; episodeNumber < NOF_EPISODES; episodeNumber++) {
            if (episodeNumber % 1_00_000 == 0) {
                System.out.println("i = " + episodeNumber);
            }

            StateCards cards = StateCards.newRandomPairs();
            Episode episode = episodeRunner.play(cards);
            returnsForEpisode.clear();
            returnsForEpisode.appendReturns(episode);
            learner.updateMemory(returnsForEpisode);
        }
    }

    @NotNull
    private static GridPanel createUsableAceFrameAndPanel() {
        List<Integer> xSet = getXset();
        List<Integer> ySet = getYset();
        JFrame frameUsableAce = new JFrame("Usable ace");  // Create a window with "Grid" in the title bar.
        GridPanel panelUsableAce = new GridPanel(xSet, ySet, X_LABEL, Y_LABEL);  // Create an object of type Grid.
        frameUsableAce.setContentPane(panelUsableAce);  // Add the Grid panel to the window.
        fixFrame(frameUsableAce);
        return panelUsableAce;
    }

    @NotNull
    private static GridPanel createNoUsableAceFrameAndPanel() {
        List<Integer> xSet = getXset();
        List<Integer> ySet = getYset();
        JFrame frameNoUsableAce = new JFrame("No usable ace");  // Create a window with "Grid" in the title bar.
        GridPanel panelNoUsableAce = new GridPanel(xSet, ySet, X_LABEL, Y_LABEL);  // Create an object of type Grid.
        frameNoUsableAce.setContentPane(panelNoUsableAce);  // Add the Grid panel to the window.
        fixFrame(frameNoUsableAce);
        return panelNoUsableAce;
    }

    private static void setPanel(GridPanel panel, ValueMemory valueMemory, boolean usableAce) {
        List<Integer> xSet = getXset();
        List<Integer> ySet = getYset();
        for (int y : ySet) {
            for (int x : xSet) {
                double value = valueMemory.read(new StateObserved(y, usableAce, x));
                panel.setNumbersAtCell(x,y, value);
            }
        }
        panel.setColorsAtCells();
        panel.setTextCellValues(false);
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
