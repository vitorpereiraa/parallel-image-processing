package pt.ipp.isep.dei.sismd;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.*;
import pt.ipp.isep.dei.sismd.filters.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class SingleImageMain {

    private static final int BRIGHTNESS = 128;
    private static final int GLASS_DISTANCE = 100;
    private static final int BLUR_STRENGTH = 5;
    private static final Predicate<Color> BLUR_CONDITIONAL;
    private static final int SWIRL_INTENSITY = 1;//%

    static {

        BLUR_CONDITIONAL = color -> color.red() > 125 && color.red() > color.green() && color.red() > color.blue();
    }

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
                num = -1;
            }
        } while (num <= 0);


        Filter filter = switch (num) {
            case 1 -> new BrighterFilter(BRIGHTNESS);
            case 3 -> new SwirlFilter(SWIRL_INTENSITY);
            case 4 -> new GlassFilter(GLASS_DISTANCE);
            case 5 -> new BlurFilter(BLUR_STRENGTH);
            case 6 -> new ConditionalBlurFilter(BLUR_STRENGTH, BLUR_CONDITIONAL);
            default -> new GrayscaleFilter();
        };

        System.out.println("Executor options: ");
        System.out.println("1 - Sequential");
        System.out.println("2 - Multithreaded");
        System.out.println("3 - Thread Pool");
        System.out.println("4 - Fork Join");
        System.out.println("5 - Completable Futures");

        num = -1;

        do {
            num = readFileKey(input);
            if (num <= 0 || num > 5) {
                System.out.println("Invalid option!");
                num = -1;
            }
        } while (num <= 0);

        long startTime = System.nanoTime();
        Image transformedImage = switch (num) {
            case 1 -> apply(image, new SequentialExecutor(filter));
            case 2 -> apply(image, new MultithreadedExecutor(filter));
            case 4 -> apply(image, new ForkJoinExecutor(filter));
            case 5 -> apply(image, new CompletableFutureExecutorPerSlice(filter));
            default -> apply(image, new ExecutorsExecutorPerSlice(filter));
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

    private static Image apply(Image image, FilterExecutor executor) {
        return executor.apply(image);
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
