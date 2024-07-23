package common.plotters.table_shower;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TableFrameSaver {


    public void saveTableFrame(JFrame frame, String pathname, String fileName) throws IOException {
        try {
            frame.setVisible(true);
            var image = new BufferedImage(
                    frame.getWidth(),
                    frame.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            var g2d = image.createGraphics();
            frame.paint(g2d);
            g2d.dispose();
            ImageIO.write(image, "png", new File(pathname + fileName));
        } catch (IOException ex) {
            throw new IOException("IO exception");
        }
    }

}
