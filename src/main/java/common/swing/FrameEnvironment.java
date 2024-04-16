package common.swing;

import javax.swing.*;

public class FrameEnvironment  extends JFrame {

    /**    * Creates new  Frame     */
    public FrameEnvironment(int W, int H, String title) {

        setSize(W, H);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(title);
    }

}
