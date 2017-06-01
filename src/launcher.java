import com.sun.org.glassfish.gmbal.ParameterNames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by DevWork on 5/23/17.
 */
public class launcher {

    public static EnergyData[][] energyMatrix;
    public static BufferedImage cachedImage;

    public static double maxEnergy = Double.MIN_VALUE;
    public static double minEnergy = Double.MAX_VALUE;

    public static double maxNum = Double.MIN_VALUE;
    public static double minNum = Double.MAX_VALUE;

    public static int maxColumn = Integer.MIN_VALUE;
    public static int minColumn = Integer.MAX_VALUE;

    public static float magicNumber = (float) (1.0 / Math.sqrt(6)); //dont mess with this.

    // possible IO exception when loading image from file...
    // see @JBackground.java constructor
    public static void main(String[] args) {
        final JFrame r = new JFrame();
        JBackground bg = null;
        try {
            bg = new JBackground("images/flower.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        bg.setSize(bg.getPreferredSize());
        JScrollPane pane = new JScrollPane(bg);
        r.setSize(bg.getBackgroundImageDimensions().x, bg.getBackgroundImageDimensions().y);

        energyMatrix = new EnergyData[bg.getBackgroundImageDimensions().y][bg.getBackgroundImageDimensions().x]; //2D array: row, column.
        cachedImage = bg.getImage();
        r.setMaximumSize(new Dimension(bg.getBackgroundImageDimensions().x, bg.getBackgroundImageDimensions().y));
        r.add(pane, BorderLayout.CENTER);
        r.setVisible(true);
        calculateEnergy(cachedImage);
        //drawEnergyFilter(bg);


        for (int i = 0; i < 300; i++) {
            energyMatrix = new EnergyData[bg.getBackgroundImageDimensions().y][bg.getBackgroundImageDimensions().x]; //2D array: row, column.
            calculateEnergy(cachedImage);
            preparePath();
            BufferedImage postProcessed = new BufferedImage(cachedImage.getWidth() - 1, cachedImage.getHeight(), 1); // type 1 = RGB

            // calculate lowest energy seam
            int col = getBestEnergyColumn();
            for (int row = energyMatrix.length - 2; row >= 0; row--) {
                bg.setPixelColor(col, row, Color.RED);
                bg.repaint();
                int[] leftOfSeam = cachedImage.getRGB(0, row, col, 1, null, 0, col);
                int[] rightOfSeam = cachedImage.getRGB(col + 1, row, cachedImage.getWidth() - (col + 1), 1, null, 0, cachedImage.getWidth() - (col + 1));
                postProcessed.setRGB(0, row, col, 1, leftOfSeam, 0, col);
                postProcessed.setRGB(col, row, cachedImage.getWidth() - (col + 1), 1, rightOfSeam, 0, cachedImage.getWidth() - (col + 1));
                // System.out.println(row + ", " + col + ", " + energyMatrix[row][col].direction);
                if (energyMatrix[row][col].direction != null) { // -1, 0, 1 (left, up, right)
                    col += energyMatrix[row][col].direction.delta;
                }

            }
            bg.setImg(postProcessed);
            cachedImage = postProcessed; // dont change. ever.
            bg.repaint();
            //energyMatrix = newEnergy;
        }


        //ATTEMPT AT OPTIMIZING DRAWING CALCULATIONS: TESTING PHASE!!

        /*
        energyMatrix = new EnergyData[bg.getBackgroundImageDimensions().y][bg.getBackgroundImageDimensions().x];
        calculateEnergy(cachedImage, null, 1);
        for (int i = 0; i < 500; i++) {
            EnergyData[][] tempEnergyMatrix = energyMatrix;
            preparePath();
            BufferedImage postProcessedIMG = new BufferedImage(cachedImage.getWidth() - 1, cachedImage.getHeight(), 1);
            int col = getBestEnergyColumn();
            int tempCol = col;
            for (int row = energyMatrix.length - 2; row >= 0; row--) {
                EnergyData[] postRemovalEnergyData = new EnergyData[energyMatrix[row].length - 1]; // because we want it to be 1 pixel smaller
                System.out.println(row + ", " + col);
                if (col >= 0) {
                    postRemovalEnergyData = combineEnergySeams(Arrays.copyOfRange(energyMatrix[row], 0, col), Arrays.copyOfRange(energyMatrix[row], col + 1, energyMatrix[row].length));
                    tempEnergyMatrix[row] = postRemovalEnergyData;
                    int[] leftOfSeam = cachedImage.getRGB(0, row, col, 1, null, 0, col);
                    int[] rightOfSeam = cachedImage.getRGB(col + 1, row, cachedImage.getWidth() - (col + 1), 1, null, 0, cachedImage.getWidth() - (col + 1));
                    postProcessedIMG.setRGB(0, row, col, 1, leftOfSeam, 0, col);
                    postProcessedIMG.setRGB(col, row, cachedImage.getWidth() - (col + 1), 1, rightOfSeam, 0, cachedImage.getWidth() - (col + 1));
                } else {
                    //postRemovalEnergyData = combineEnergySeams(Arrays.copyOfRange(energyMatrix[row], 0, col), Arrays.copyOfRange(energyMatrix[row], col + 1, energyMatrix[row].length));
                    tempEnergyMatrix[row] = Arrays.copyOfRange(energyMatrix[row], col + 1, energyMatrix[row].length);
                    int[] rightOfSeam = cachedImage.getRGB(col + 1, row, cachedImage.getWidth() - (col + 1), 1, null, 0, cachedImage.getWidth() - (col + 1));
                    postProcessedIMG.setRGB(col, row, cachedImage.getWidth() - (col + 1), 1, rightOfSeam, 0, cachedImage.getWidth() - (col + 1));
                }
               // int[] leftOfSeam = cachedImage.getRGB(0, row, col, 1, null, 0, col);
               // int[] rightOfSeam = cachedImage.getRGB(col + 1, row, cachedImage.getWidth() - (col + 1), 1, null, 0, cachedImage.getWidth() - (col + 1));
               // postProcessedIMG.setRGB(0, row, col, 1, leftOfSeam, 0, col);
               // postProcessedIMG.setRGB(col, row, cachedImage.getWidth() - (col + 1), 1, rightOfSeam, 0, cachedImage.getWidth() - (col + 1));
                if (energyMatrix[row][col].direction != null) { // -1, 0, 1 (left, up, right)
                    col += energyMatrix[row][col].direction.delta;
                }
            }
            bg.setImg(postProcessedIMG);
            cachedImage = postProcessedIMG; // dont change. ever.
            energyMatrix = tempEnergyMatrix;
            bg.repaint();

            for (int ra = energyMatrix.length - 2; ra >= 0; ra--) {
                energyMatrix[ra][tempCol - 1].rawValue = getEnergyAtPixel(cachedImage, ra, tempCol - 1);
                energyMatrix[ra][tempCol].rawValue = getEnergyAtPixel(cachedImage, ra, tempCol);
                if (energyMatrix[ra][tempCol].direction != null) { // -1, 0, 1 (left, up, right)
                    tempCol += energyMatrix[ra][tempCol].direction.delta;
                }
            }
        }
        */


    }

    public static EnergyData[] combineEnergySeams(EnergyData[] a, EnergyData[] b) {
        int aLen = a.length;
        int bLen = b.length;
        EnergyData[] c = new EnergyData[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    // draws the energy gradient
    public static void drawEnergyFilter(JBackground bg) {
        for (int i = 0; i < energyMatrix.length; i++) {
            for (int j = 0; j < energyMatrix[i].length; j++) {
                EnergyData energyValue = energyMatrix[i][j];
                double ExDiff = energyValue.rawValue / maxEnergy;
                Color diffxColor = getIndexedColor(Color.black, Color.white, (float) ExDiff);
                bg.setPixelColor(j, i, diffxColor);
                bg.repaint();
            }
        }
    }

    // @Define
    public static void drawSeamGradientFilter(JBackground bg) {
        for (int i = 0; i < energyMatrix.length; i++) {
            for (int j = 0; j < energyMatrix[i].length; j++) {
                EnergyData energyValue = energyMatrix[i][j];
                double ExDiff = energyValue.num / maxNum;
                Color diffxColor = getIndexedColor(Color.black, Color.white, (float) ExDiff);
                bg.setPixelColor(j, i, diffxColor);
                bg.repaint();
            }
        }
    }


    public static int getBestEnergyColumn() {
        int bestCol = 0;
        double bestNum = Double.MAX_VALUE;
        for (int col = 0; col < energyMatrix[energyMatrix.length - 1].length; col++) {
            EnergyData data = energyMatrix[energyMatrix.length - 1][col];
            if (data.num < bestNum) {
                bestNum = data.num;
                bestCol = col;
            }
        }
        return bestCol;
    }

    // source: https://stackoverflow.com/questions/22218140/calculate-the-color-at-a-given-point-on-a-gradient-between-two-colors?noredirect=1&lq=1
    // calculate the RGB color between color1 and color2
    public static Color getIndexedColor(Color color1, Color color2, float percent) {
        percent = (float) Math.pow(percent, magicNumber);
        double rRed = color1.getRed() + percent * (color2.getRed() - color1.getRed());
        double rGreen = color1.getGreen() + percent * (color2.getGreen() - color1.getGreen());
        double rBlue = color1.getBlue() + percent * (color2.getBlue() - color1.getBlue());
        //might lose some precision due casting to a int from a double
        return new Color((int) rRed, (int) rGreen, (int) rBlue);
    }

    // using dual gradient for calculating energy of each pixel
    // Adapted from: http://www.cs.princeton.edu/courses/archive/spr13/cos226/assignments/seamCarving.html
    public static void calculateEnergy(BufferedImage img) {
        for (int row = 0; row < energyMatrix.length; row++) {
            for (int col = 0; col < energyMatrix[row].length; col++) {
                double energy = getEnergyAtPixel(img, row, col);
                maxEnergy = (energy > maxEnergy) ? energy : maxEnergy;
                minEnergy = (energy < minEnergy) ? energy : minEnergy;
                energyMatrix[row][col] = new EnergyData(energy);
            }
        }
    }

    // calculates the energy of a pixel at (col, row) given an image
    public static double getEnergyAtPixel(BufferedImage img, int row, int col) {
        Pixel Rx1; // right pixel
        Pixel Rx2; // left pixel
        //----------
        Pixel Ry1; // top pixel
        Pixel Ry2; // bottom pixel
        //----------
        double RxDelta; // delta of x set pixels
        double RyDelta; // delta of y set pixels
        //----------
        double energy; // energy of pixel: RxDelta + RyDelta @Unused

        // grab X coordinate Pixels
        if (col == 0) {
            Rx1 = new Pixel(new Color(img.getRGB(col + 1, row)));
            Rx2 = new Pixel(new Color(img.getRGB(energyMatrix[0].length - 1, row)));
        } else if (col == energyMatrix[0].length - 1) {
            Rx1 = new Pixel(new Color(img.getRGB(0, row)));
            Rx2 = new Pixel(new Color(img.getRGB(col - 1, row)));
        } else {
            Rx1 = new Pixel(new Color(img.getRGB(col + 1, row)));
            Rx2 = new Pixel(new Color(img.getRGB(col - 1, row)));
        }

        // grab Y coordinate Pixels
        if (row == 0) {
            Ry1 = new Pixel(new Color(img.getRGB(col, energyMatrix.length - 1)));
            Ry2 = new Pixel(new Color(img.getRGB(col, row + 1)));
        } else if (row == energyMatrix.length - 1) {
            Ry1 = new Pixel(new Color(img.getRGB(col, row - 1)));
            Ry2 = new Pixel(new Color(img.getRGB(col, 0)));
        } else {
            Ry1 = new Pixel(new Color(img.getRGB(col, row - 1)));
            Ry2 = new Pixel(new Color(img.getRGB(col, row + 1)));
        }

        // color operations @see resource
        RxDelta = Math.pow(Math.abs(Rx1.R - Rx2.R), 2) +
                Math.pow(Math.abs((Rx1.G - Rx2.G)), 2) +
                Math.pow(Math.abs((Rx1.B - Rx2.B)), 2);

        RyDelta = Math.pow(Math.abs(Ry1.R - Ry2.R), 2) +
                Math.pow(Math.abs(Ry1.G - Ry2.G), 2) +
                Math.pow(Math.abs(Ry1.B - Ry2.B), 2);

        return RxDelta + RyDelta;
    }

    // magic...
    public static void preparePath() {
        // Initialize the first row
        for (int col = 0; col < energyMatrix[0].length; col++) {
            energyMatrix[0][col].num = energyMatrix[0][col].rawValue;
        }
        // Calculate the rest
        for (int row = 1; row < energyMatrix.length; row++) {
            for (int col = 0; col < energyMatrix[row].length; col++) {
                EnergyData current = energyMatrix[row][col];
                double lowestNum = Double.MAX_VALUE;
                EnergyData.Direction bestDirection = null;
                for (EnergyData.Direction direction : EnergyData.Direction.values()) {
                    int testCol = col + direction.delta;
                    if (testCol < 0 || testCol >= energyMatrix[row - 1].length) {
                        continue; // This direction is out of bounds; skip it and continue looking through the others
                    }
                    EnergyData test = energyMatrix[row - 1][testCol];
                    if (test.num < lowestNum) {
                        lowestNum = test.num;
                        bestDirection = direction;
                    }
                }
                double num = current.rawValue + lowestNum;
                current.num = num;
                current.direction = bestDirection;

                maxNum = (num > maxNum) ? num : maxNum;
                minNum = (num < minNum) ? num : minNum;
            }
        }
    }
}
