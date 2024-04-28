package pt.ipp.isep.dei.sismd;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.ExecutorsExecutorPerLine;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;
import pt.ipp.isep.dei.sismd.executors.ForkJoinExecutor;
import pt.ipp.isep.dei.sismd.filters.*;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;


public class MultipleImagesMain {

    private static final int BRIGHTNESS = 128;
    private static final int GLASS_DISTANCE = 20;
    private static final int BLUR_STRENGHT = 15;
    private static final int SWIRL_STRENGHT = -2;//%
    private static final Predicate<Color> BLUR_CONDITION;


    static {
        BLUR_CONDITION = color -> color.blue() > 10 && color.blue() > color.red() + color.green();
    }

    private record ImageNamePair(String name, Image image) {
    }

    public static void main(String[] args) {

        File resourceFile = new File(SingleImageMain.class.getClassLoader().getResource("imgs/resource.txt").getPath());
        File imgsDir = resourceFile.getParentFile();

        Queue<File> dirs = new LinkedList<>();
        dirs.add(imgsDir);
        List<File> files = new LinkedList<>();
        System.out.println("=========================================================================================================");
        System.out.println("Images that will be processed:\n");
        while (!dirs.isEmpty()) {
            File f = dirs.poll();
            if (f.getAbsolutePath().equals(resourceFile.getAbsolutePath())) continue;
            if (f.isDirectory()) {
                Collections.addAll(dirs, Objects.requireNonNull(f.listFiles()));
                continue;
            }
            System.out.println(f.getName());
            files.add(f);
        }
        System.out.println("=========================================================================================================");
        System.out.println();

        List<ImageNamePair> images = files.stream().map(f -> new ImageNamePair(f.getName(), Utils.loadImage(f))).toList();

        System.out.println("=========================================================================================================");
        System.out.println("Filters that will be applied:");
        System.out.println("Brighter Filter");
        System.out.println("Gray Scale Filter");
        System.out.println("Swirl Filter");
        System.out.println("Glass Filter");
        System.out.println("Blur Filter");
        System.out.println("Conditional Blur Filter");
        System.out.println("=========================================================================================================");


        applyBrighterFilter(images);
        applyGrayScaleFilter(images);
        applySwirlFilter(images);
        applyGlassFilter(images);
        applyBlurFilter(images);
        applyConditionalBlurFilter(images);
    }


    private static void persistImages(List<ImageNamePair> images, String filterName) {
        images.forEach(pair -> {
            File outputFile = new File("./out/" + filterName + "/" + pair.name());
            Utils.writeImage(pair.image(), outputFile);
        });
    }


    private static void applyConditionalBlurFilter(List<ImageNamePair> images) {
        apply(images, new ForkJoinExecutor(new ConditionalBlurFilter(BLUR_STRENGHT, BLUR_CONDITION)), "Conditional BLur Filter", "conditional");
    }

    private static void applyBlurFilter(List<ImageNamePair> images) {
        apply(images, new ForkJoinExecutor(new BlurFilter(BLUR_STRENGHT)), "Blur Filter", "blur");
    }

    private static void applyGlassFilter(List<ImageNamePair> images) {
        apply(images, new ForkJoinExecutor(new GlassFilter(GLASS_DISTANCE)), "Glass Filter", "glass");
    }

    private static void applySwirlFilter(List<ImageNamePair> images) {
        apply(images, new ForkJoinExecutor(new SwirlFilter(SWIRL_STRENGHT)), "Swirl Filter", "swirl");
    }

    private static void applyGrayScaleFilter(List<ImageNamePair> images) {
        apply(images, new ForkJoinExecutor(new GrayscaleFilter()), "Gray Scale Filter", "gray");
    }

    private static void applyBrighterFilter(List<ImageNamePair> images) {
        apply(images, new ForkJoinExecutor(new BrighterFilter(BRIGHTNESS)), "Brighter Filter", "brighter");
    }


    private static void apply(List<ImageNamePair> images, FilterExecutor filter, String filterName, String dirCode) {
        long startTime = System.nanoTime();
        List<ImageNamePair> result = new ArrayList<>(images.size());
        for (ImageNamePair pair : images) {
            result.add(new ImageNamePair(pair.name(), filter.apply(pair.image())));
        }
        long endTime = System.nanoTime();



        List<ImageNamePair> processedImages = result;
        long duration = (endTime - startTime);  // Time in nanoseconds
        double seconds = (double) duration / 1_000_000_000.0;
        System.out.printf("%s in %.3fs\n", filterName, seconds);
        File outputDir = new File("./out/" + dirCode);
        outputDir.mkdirs();
        persistImages(processedImages, dirCode);
    }


}

