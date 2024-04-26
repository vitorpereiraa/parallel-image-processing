package pt.ipp.isep.dei.sismd;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.ExecutorServiceExecutor;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;
import pt.ipp.isep.dei.sismd.filter.bright.BrighterFilter;
import pt.ipp.isep.dei.sismd.multithreaded.ExecutorBlurFilterExecutor;

import java.io.File;
import java.util.*;

public class MultipleProcessMain {

    private static final int BRIGHTNESS = 128;

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

        long startTime = System.nanoTime();
        List<ImageNamePair> processedImages = applyBrighterFilter(images);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  // Time in nanoseconds
        double seconds = (double) duration / 1_000_000_000.0;
        System.out.printf("Brighter Filter Applied in %.3f\n", seconds);
        File outputDir = new File("./out/brighter");
        outputDir.mkdirs();
        persistImages(processedImages, "brighter");


        startTime = System.nanoTime();
        processedImages = applyBlurFilter(images);
        endTime = System.nanoTime();
        duration = (endTime - startTime);  // Time in nanoseconds
        seconds = (double) duration / 1_000_000_000.0;
        System.out.printf("Blur Filter Applied in %.3f\n", seconds);
        outputDir = new File("./out/blur");
        outputDir.mkdirs();
        persistImages(processedImages, "blur");

    }


    private static void persistImages(List<ImageNamePair> images, String filterName) {
        images.forEach(pair -> {
            File outputFile = new File("./out/" + filterName + "/" + pair.name());
            Utils.writeImage(pair.image(), outputFile);
        });
    }


    private static List<ImageNamePair> applyConditionalBlurFilter(List<ImageNamePair> image) {
        return null;
    }

    private static List<ImageNamePair> applyBlurFilter(List<ImageNamePair> images) {
        return apply(images, new ExecutorBlurFilterExecutor(8));
    }

    private static List<ImageNamePair> applyGlassFilter(List<ImageNamePair> images) {
        return null;
    }

    private static List<ImageNamePair> applySwirlFilter(List<ImageNamePair> images) {
        return null;
    }

    private static List<ImageNamePair> applyGrayScaleFilter(List<ImageNamePair> images) {
        return null;
    }

    private static List<ImageNamePair> applyBrighterFilter(List<ImageNamePair> images) {
        return apply(images, new ExecutorServiceExecutor(new BrighterFilter(BRIGHTNESS)));
    }


    private static List<ImageNamePair> apply(List<ImageNamePair> images, FilterExecutor filter) {
        List<ImageNamePair> result = new ArrayList<>(images.size());
        for (ImageNamePair pair : images) {
            result.add(new ImageNamePair(pair.name(), filter.apply(pair.image())));
        }
        return result;
    }


}

