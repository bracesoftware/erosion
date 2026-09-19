package co.bracesoftware.erosion;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = Erosion.MODID, value = Dist.CLIENT)
public class ErosionSplash extends JWindow
{
    @SubscribeEvent 
    public static void fml(FMLClientSetupEvent e)
    {
        ErosionUtils.Log("Loading...");
        return;
    }

    static
    {
        var s = new ErosionSplash(Erosion.MODNAME + "." + ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT);
        s.setVisible(true);
        new Thread(() -> {
            try
            {
                Thread.sleep(5000);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            s.dispose();
        }).start();
    }

    private BufferedImage logo;

    public ErosionSplash(String i)
    {
        try
        {
            String rp = "/" + i;
            var is = ErosionSplash.class.getResourceAsStream(rp);
            
            if(is != null)
            {
                this.logo = ImageIO.read(is);
                is.close();
            }
            else
            {
                ErosionUtils.Log("Error while loading splash; resource not found: " + rp);
            }
        }
        catch(IOException e)
        {
            ErosionUtils.Log("Error while loading splash -> " + e.getMessage());
        }

        if(this.logo != null) setSize(this.logo.getWidth(), this.logo.getHeight());
        else setSize(100,100);

        setLocationRelativeTo(null);

        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if(this.logo != null)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2d.drawImage(this.logo, 0,0,null);
        }
        return;
    }
}