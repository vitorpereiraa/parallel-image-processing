package pt.ipp.isep.dei.sismd;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.BrighterFilter;
import pt.ipp.isep.dei.sismd.filter.GlassFilter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);
        String filePath = "";
        System.out.println("Insert the name of the file path you would like to use.");
        filePath = input.nextLine();
        input.close();

        Image image = Utils.loadImage(filePath);
        Image transformedImage = new GlassFilter().apply(image);

        File outputDir = new File("./out");
        if (outputDir.isDirectory() && !outputDir.exists()) {
            outputDir.mkdirs();
        }
        Utils.writeImage(transformedImage, "brighter.jpg");
    }

}
