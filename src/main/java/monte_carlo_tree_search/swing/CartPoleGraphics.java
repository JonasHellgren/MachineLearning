package monte_carlo_tree_search.swing;

import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.EnvironmentCartPole;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import swing.FrameEnvironment;
import swing.ScaleLinear;

public class CartPoleGraphics {

    private PanelCartPoleAnimation animationPanel;

    public CartPoleGraphics() {
        this.setupFrameAndPanel();
    }

    private void setupFrameAndPanel() {
        final int FRAME_WEIGHT = 600;
        final int FRAME_HEIGHT = 300;
        final int FRAME_MARGIN = 50;
        FrameEnvironment animationFrame =new FrameEnvironment(FRAME_WEIGHT, FRAME_HEIGHT,"CartPole animation");
        double xMax= EnvironmentCartPole.X_TRESHOLD;
        double yMin=EnvironmentCartPole.Y_MIN;
        double yMax=EnvironmentCartPole.Y_MAX;
        ScaleLinear xScaler=new ScaleLinear(-xMax,xMax,
                FRAME_MARGIN, FRAME_WEIGHT - FRAME_MARGIN,
                false, FRAME_MARGIN);
        ScaleLinear yScaler=new ScaleLinear(yMin,yMax, FRAME_MARGIN,
                FRAME_HEIGHT - FRAME_MARGIN,true, FRAME_MARGIN);

        animationPanel =new PanelCartPoleAnimation(xScaler,yScaler);
        animationPanel.setLayout(null);  //to enable tailor made position
        animationFrame.add(animationPanel);
        animationFrame.setVisible(true);
    }

    public  void render(StateInterface<CartPoleVariables> state, double maxQ, int actionValue) {
        animationPanel.setCartPoleStates(state,actionValue,maxQ);
        animationPanel.repaint();
    }

}
