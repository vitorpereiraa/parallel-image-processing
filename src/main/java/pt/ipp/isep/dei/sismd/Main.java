package pt.ipp.isep.dei.sismd;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.GlassFilterExecutorMultithreaded;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);
        String filePath = "";
        System.out.println("Insert the file path you would like to use.");
        filePath = input.nextLine();
        input.close();

        Image image = Utils.loadImage(new File(filePath));
        Image transformedImage = new GlassFilterExecutorMultithreaded().apply(image);

        File outputDir = new File("./out");
        if (outputDir.isDirectory() && !outputDir.exists()) {
            outputDir.mkdirs();
        }
        Utils.writeImage(transformedImage, new File("./out/glass/"+new File(filePath).getName()));
    }

}
