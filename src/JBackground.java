import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by DevWork on 5/23/17.
 */
public class JBackground extends JPanel {
    //private Image backgroundImage;
    private BufferedImage img;

    public JBackground(String fileName) throws IOException {
        //backgroundImage = ImageIO.read(new File(fileName));
        img = ImageIO.read(new File(fileName));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image.
        g.drawImage(img, 0, 0, this);
        //g.drawImage(img, g.getClipBounds().x, g.getClipBounds().y, g.getClipBounds().width, g.getClipBounds().height,this);
    }

    // return a Point object that contains a width and height coordinate pair
    public Point getBackgroundImageDimensions() {
        return new Point(img.getWidth(), img.getHeight());
    }

    public BufferedImage getImage() {
        return img;
    }

    // given a (x,y) coordinate, replace the color with color c
    public void setPixelColor(int x, int y, Color c) {
        img.setRGB(x, y, c.getRGB());
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(img.getWidth(), img.getHeight());
    }
}
