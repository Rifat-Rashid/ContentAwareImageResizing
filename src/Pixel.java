import java.awt.*;

/**
 * Created by DevWork on 5/24/17.
 */
public class Pixel {
    int R;
    int G;
    int B;

    // default constructor
    public Pixel() {
    }

    public Pixel(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }

    public Pixel(Color c) {
        this.R = c.getRed();
        this.G = c.getGreen();
        this.B = c.getBlue();
    }
}
