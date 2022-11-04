package black_jack.main_runner;

import black_jack.environment.BlackJackEnvironment;
import black_jack.environment.EnvironmentInterface;
import black_jack.helper.EpisodeRunner;
import black_jack.helper.Learner;
import black_jack.models.*;
import black_jack.policies.HitBelow20Policy;
import black_jack.policies.PolicyInterface;
import black_jack.result_drawer.GridPanel;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.bytedeco.librealsense.frame;

import javax.swing.*;
import java.awt.*;

public class PolicyEvaluation {

    public static final int NOF_EPISODES = 1_000_000;
    static final int MAX_HANDS_SUM_PLAYER=21;
    static final int MAX_DEALER_CARD=10;
    private static final int SQUARE_SIZE = 30;
    private static final double MIN_VALUE = -1d;
    private static final double MAX_VALUE = 1.5d;
    private static final int LOWER_HANDS_SUM_PLAYER = 10;
    private static final double ALPHA = 0.001;  //critical parameter setting
    private static final boolean NOF_VISITS_FLAG = false;

    public static void main(String[] args) {
        JFrame frameNoUsableAce = new JFrame("No usable ace");  // Create a window with "Grid" in the title bar.
        GridPanel panelNoUsableAce = new GridPanel(MAX_HANDS_SUM_PLAYER,MAX_DEALER_CARD,SQUARE_SIZE);  // Create an object of type Grid.
        frameNoUsableAce.setContentPane( panelNoUsableAce );  // Add the Grid panel to the window.
        fixFrame(frameNoUsableAce);

        JFrame frameUsableAce = new JFrame("Usable ace");  // Create a window with "Grid" in the title bar.
        GridPanel panelUsableAce = new GridPanel(MAX_HANDS_SUM_PLAYER,MAX_DEALER_CARD, SQUARE_SIZE);  // Create an object of type Grid.
        frameUsableAce.setContentPane( panelUsableAce );  // Add the Grid panel to the window.
        fixFrame(frameUsableAce);


        EnvironmentInterface environment=new BlackJackEnvironment();
        PolicyInterface policy= new HitBelow20Policy();
        EpisodeRunner episodeRunner=new EpisodeRunner(environment,policy);
        ReturnsForEpisode returnsForEpisode = new ReturnsForEpisode();
        ValueMemory valueMemory=new ValueMemory();
        NumberOfVisitsMemory numberOfVisitsMemory=new NumberOfVisitsMemory();
        Learner learner=new Learner(valueMemory,numberOfVisitsMemory, ALPHA, NOF_VISITS_FLAG);
        for (int episodeNumber = 0; episodeNumber < NOF_EPISODES; episodeNumber++) {

            if (episodeNumber % 1_00_000 == 0) {
                System.out.println("i = " + episodeNumber);
            }

            StateCards cards = StateCards.newRandomPairs();
            Episode episode=episodeRunner.play(cards);
            returnsForEpisode.clear();
            returnsForEpisode.appendReturns(episode);
            learner.updateMemory(returnsForEpisode);
        }

        boolean usableAce=false;
        setPanel(panelNoUsableAce, valueMemory, usableAce);
        usableAce=true;
        setPanel(panelUsableAce, valueMemory, usableAce);

        panelUsableAce.setColorAtCell(5,5,new Color(22,44,222));

        panelNoUsableAce.repaint();
        panelUsableAce.repaint();
    }

    private static void setPanel(GridPanel panel, ValueMemory valueMemory, boolean usableAce) {
        for (int i = LOWER_HANDS_SUM_PLAYER; i < MAX_HANDS_SUM_PLAYER; i++) {
            for (int j = 0; j < MAX_DEALER_CARD; j++) {
                double value= valueMemory.read(new StateObserved(i, usableAce,j));
                double  strength=(value-MIN_VALUE)/(MAX_VALUE-MIN_VALUE); //normalization
                int rgb=Math.min((int) (strength*255),255);
              //  System.out.println("i = " + i+",j  = " + j+", strength = " + strength+", value = " + value+", rgb = " + rgb);
                panel.setColorAtCell(i,j,new Color(rgb,rgb,rgb));
            }
        }
    }

    private static void fixFrame(JFrame frame) {
        frame.pack(); // Set the size of the window based on the panel's preferred size.
        Dimension screenSize; // A simple object containing the screen's width and height.
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int top, left;  // position for top left corner of the window
        left = ( screenSize.width - frame.getWidth() ) / 2;
        top = ( screenSize.height - frame.getHeight() ) / 2;
        frame.setLocation(left,top);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
