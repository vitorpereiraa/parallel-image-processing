package pt.ipp.isep.dei.sismd;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Utils {

    Utils() {
    }


    /**
     * Loads image from filename into a Color (pixels decribed with rgb values) matrix.
     *
     * @param file the name of the imge in the filesystem.
     * @return Color matrix.
     */
    public static Image loadImage(File file) {
        BufferedImage buffImg = loadImageFile(file);
        return convertTo2DFromBuffered(buffImg);
    }

    /**
     * Converts image from a Color matrix to a .jpg file.
     *
     * @param image    the matrix of Color objects.
     * @param filename to the image.
     */
    public static void writeImage(Image image, File outputFile) {
        var bufferedImage = Utils.matrixToBuffered(image);
        try {
            ImageIO.write(bufferedImage, "jpg", outputFile);
        } catch (IOException e) {
            System.out.println("Could not write image " + outputFile.getName() + " !");
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Loads in a BufferedImage from the specified path to be processed.
     *
     * @param filename The path to the file to read.
     * @return a BufferedImage if able to be read, NULL otherwise.
     */
    private static BufferedImage loadImageFile(File image) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(image);
        } catch (IOException e) {
            System.out.println("Could not load image " + image.getName() + " !");
            e.printStackTrace();
            System.exit(1);
        }
        return img;
    }


    /**
     * Copy a Color matrix to another Color matrix.
     * Useful if one does not want to modify the original image.
     *
     * @param image the source matrix
     * @return a copy of the image
     */

    public static Color[][] copyImage(Color[][] image) {
        Color[][] copy = new Color[image.length][image[0].length];
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                copy[i][j] = image[i][j];
            }
        }
        return copy;
    }


    /**
     * Converts a matrix of Colors into a BufferedImage to
     * write on the filesystem.
     *
     * @param image the matrix of Colors
     * @return the image ready for writing to filesystem
     */
    private static BufferedImage matrixToBuffered(Image image) {
        int width = image.width();
        int height = image.height();
        BufferedImage bImg = new BufferedImage(height, width, 1);

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                bImg.setRGB(x, y, new java.awt.Color(image.obtainPixel(x, y).red(), image.obtainPixel(x, y).green(), image.obtainPixel(x, y).blue()).getRGB());
            }
        }
        return bImg;
    }

    /**
     * Converts a file loaded into a BufferedImage to a
     * matrix of Colors
     *
     * @param image the BufferedImage to convert
     * @return the matrix of Colors
     */

    private static Image convertTo2DFromBuffered(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the integer RGB, and separate it into individual components.
                // (BufferedImage saves RGB as a single integer value).
                int pixel = image.getRGB(x, y);
                //int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                result[x][y] = new Color(red, green, blue);
            }
        }
        return new Image(result);
    }

}