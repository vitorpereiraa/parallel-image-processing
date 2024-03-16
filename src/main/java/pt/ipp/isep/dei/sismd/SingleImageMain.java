package pt.ipp.isep.dei.sismd;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.BluerFilter;
import pt.ipp.isep.dei.sismd.filter.BrighterFilter;
import pt.ipp.isep.dei.sismd.multithreaded.MultithreadedBlurFilter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SingleImageMain {

    private static final int BRIGHTNESS = 128;

    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);

        File resourceFile = new File(SingleImageMain.class.getClassLoader().getResource("imgs/resource.txt").getPath());
        File imgsDir = resourceFile.getParentFile();

        Queue<File> dirs = new LinkedList<>();
        dirs.add(imgsDir);
        List<File> images = new LinkedList<>();
        while (!dirs.isEmpty()) {
            File f = dirs.poll();
            if (f.getAbsolutePath().equals(resourceFile.getAbsolutePath())) continue;
            if (f.isDirectory()) {
                Collections.addAll(dirs, Objects.requireNonNull(f.listFiles()));
                continue;
            }
            images.add(f);
        }


        int key = 0;
        System.out.println("Choose an image to apply filter:\n");
        for (File image : images) {
            System.out.printf("%d - %s%n", key + 1, image.getName());
            key++;
        }
        int num = -1;
        do {
            num = readFileKey(input);
            if (num <= 0 || num > images.size()) {
                System.out.println("Invalid option!");
                num = -1;
            }
        } while (num == -1);


        Image image = Utils.loadImage(images.get(num - 1));


        System.out.println("Filter options: ");
        System.out.println("1 - Brighter Filter");
        System.out.println("2 - Gray Scale Filter");
        System.out.println("3 - Swirl Filter");
        System.out.println("4 - Glass Filter");
        System.out.println("5 - Blur Filter");
        System.out.println("6 - Conditional Blur Filter");


        do {
            num = readFileKey(input);
            if (num <= 0 || num > 6) {
                System.out.println("Invalid option!");
                num=-1;
            }
        } while (num <= 0);

        long startTime = System.nanoTime();
        Image transformedImage = switch (num) {
            case 1 -> applyBrighterFilter(image);
            case 2 -> applyGrayScaleFilter(image);
            case 3 -> applySwirlFilter(image);
            case 4 -> applyGlassFilter(image);
            case 5 -> applyBlurFilter(image);
            case 6 -> applyConditionalBlurFilter(image);
            default -> null;
        };

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  // Time in nanoseconds
        double seconds = (double) duration / 1_000_000_000.0;
        System.out.printf("Filter Applied in %.3f\n", seconds);


        File outputDir = new File("./out");
        outputDir.mkdirs();
        File outputFile = new File("./out/" + new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss_SSS").format(new Date()) + ".jpg");
        Utils.writeImage(transformedImage, outputFile);
        System.out.println("Saved transformed image in: " + outputFile.getAbsolutePath());
    }

    private static Image applyConditionalBlurFilter(Image image) {
        return null;
    }

    private static Image applyBlurFilter(Image image) {
        System.out.println("Applying Blur Filter...");
        return new MultithreadedBlurFilter(8).apply(image);
    }

    private static Image applyGlassFilter(Image image) {
        return null;
    }

    private static Image applySwirlFilter(Image image) {
        return null;
    }

    private static Image applyGrayScaleFilter(Image image) {
        return null;
    }

    private static Image applyBrighterFilter(Image image) {
        System.out.println("Applying Brightness Filter with " + BRIGHTNESS + " of brightness...");
        return new BrighterFilter(BRIGHTNESS).apply(image);
    }

    private static int readFileKey(Scanner input) {
        System.out.println();
        System.out.print("> ");

        String res = input.nextLine();
        if (res.matches("\\d+")) {
            return Integer.parseInt(res);
        }
        return -1;
    }

}
