import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class ImageConverter
{
    /**
     * Converts an image to another format
     *
     * @param inputImagePath Path of the source image
     * @param outputImagePath Path of the destination image
     * @param formatName the format to be converted to, one of: jpeg, png,
     * bmp, wbmp, and gif
     * @return true if successful, false otherwise
     * @throws IOException if errors occur during writing
     */
    public static boolean convertFormat(String inputImagePath,
            String outputImagePath, String formatName) throws IOException
    {
        FileInputStream inputStream = new FileInputStream(inputImagePath);
        FileOutputStream outputStream = new FileOutputStream(outputImagePath);

        // reads input image from file
        BufferedImage inputImage = ImageIO.read(inputStream);

        // writes to the output image in specified format
        boolean result = ImageIO.write(inputImage, formatName, outputStream);

        // needs to close the streams
        outputStream.close();
        inputStream.close();

        return result;
    }


    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.print("Convert image to bmp?: ");
        String answer = in.next();
        while (answer.equals("y"))
        {
        System.out.println("Name of the image file: ");
        String inputImage = in.next();
        System.out.println("Name of the output bmp file: ");
        String oututImage = in.next();
        String formatName = "BMP";
           try
           {
               boolean result = ImageConverter.convertFormat(inputImage,
                       oututImage, formatName);
               if (result)
                  System.out.println("Image converted successfully.");
               else
                  System.out.println("Could not convert image.");

               System.out.print("Convert image to bmp?: ");
               answer = in.next();

           }
           catch (IOException ex)
           {
               System.out.println("Error during converting image.");
               ex.printStackTrace();
           }
        }
    }
}
