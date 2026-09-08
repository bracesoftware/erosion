package co.bracesoftware.erosion.data;

import javax.imageio.ImageIO;

import co.bracesoftware.erosion.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TextureProvider
{
    public static void combine(File baseFile, List<File> layerFiles, File outputFile)
    {
        if(baseFile == null || !baseFile.exists())
        {
            ErosionUtils.Log("Base file does not exist: " + (baseFile != null ? baseFile.getPath() : "null"));
            return;
        }

        try
        {
            BufferedImage baseImage = ImageIO.read(baseFile);
            int w = baseImage.getWidth();
            int h = baseImage.getHeight();

            BufferedImage combinedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = combinedImage.createGraphics();

            g.drawImage(baseImage, 0, 0, null);

            if(layerFiles != null)
            {
                for(File layerFile : layerFiles)
                {
                    if(layerFile != null && layerFile.exists())
                    {
                        BufferedImage layerImage = ImageIO.read(layerFile);
                        layerImage = removeWhiteBackground(layerImage);
                        g.drawImage(layerImage, 0, 0, null);
                    }
                }
            }

            g.dispose();

            if(outputFile.getParentFile() != null)
            {
                outputFile.getParentFile().mkdirs();
            }
            ImageIO.write(combinedImage, "PNG", outputFile);
            ErosionUtils.Log("Sucessfully created: " + outputFile.getName());

        }
        catch (IOException e)
        {
            System.err.println("Could not generate: " + outputFile.getName());
            e.printStackTrace();
        }
    }

    private static BufferedImage removeWhiteBackground(BufferedImage img)
    {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage t = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                int rgb = img.getRGB(x, y);
                Color color = new Color(rgb, true);

                if(color.getRed() > 220 && color.getGreen() > 220 && color.getBlue() > 220)
                {
                    t.setRGB(x, y, 0x00000000);
                }
                else
                {
                    t.setRGB(x, y, rgb);
                }
            }
        }
        return t;
    }

    public static void combine(File baseFile, File outputFile, File... layers)
    {
        combine(baseFile, List.of(layers), outputFile);
    }

    public static void generateHeatedTexture(File baseFile, File outputFile, int step, int maxSteps)
    {
        if(baseFile == null || !baseFile.exists()) return;

        try
        {
            BufferedImage baseImage = ImageIO.read(baseFile);
            BufferedImage heatedImage = applyHeatTint(baseImage, (float) step / maxSteps);

            if(outputFile.getParentFile() != null)
            {
                outputFile.getParentFile().mkdirs();
            }
            ImageIO.write(heatedImage, "PNG", outputFile);
            ErosionUtils.Log("Successfully created heated variant (" + step + "/" + maxSteps + "): " + outputFile.getName());
        }
        catch (IOException e)
        {
            System.err.println("Could not generate heated texture: " + outputFile.getName());
            e.printStackTrace();
        }
    }

    private static BufferedImage applyHeatTint(BufferedImage img, float progress)
    {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage tinted = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                int rgb = img.getRGB(x, y);
                Color color = new Color(rgb, true);

                if(color.getAlpha() == 0)
                {
                    tinted.setRGB(x, y, 0x00000000);
                    continue;
                }

                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int a = color.getAlpha();

                int targetR = 255;
                int targetG = (int) (100 + (55 * progress));
                int targetB = (int) (30 * (1 - progress));

                float intensity = progress * 0.55f;

                int newR = Math.min(255, (int) (r * (1 - intensity) + targetR * intensity));
                int newG = Math.min(255, (int) (g * (1 - intensity) + targetG * intensity));
                int newB = Math.min(255, (int) (b * (1 - intensity) + targetB * intensity));

                Color newColor = new Color(newR, newG, newB, a);
                tinted.setRGB(x, y, newColor.getRGB());
            }
        }
        return tinted;
    }

}