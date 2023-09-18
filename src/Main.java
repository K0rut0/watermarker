import com.sun.tools.jconsole.JConsoleContext;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Watermark;
import net.coobird.thumbnailator.geometry.Positions;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Main {
    public static BufferedImage rotateImage(BufferedImage src, int rotationAngle) {
        double theta = (Math.PI * 2) / 360 * rotationAngle;
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dest;
        if (rotationAngle == 90 || rotationAngle == 270) {
            dest = new BufferedImage(src.getHeight(), src.getWidth(), src.getType());
        } else {
            dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        }

        Graphics2D graphics2D = dest.createGraphics();

        if (rotationAngle == 90) {
            graphics2D.translate((height - width) / 2, (height - width) / 2);
            graphics2D.rotate(theta, height / 2, width / 2);
        } else if (rotationAngle == 270) {
            graphics2D.translate((width - height) / 2, (width - height) / 2);
            graphics2D.rotate(theta, height / 2, width / 2);
        } else {
            graphics2D.translate(0, 0);
            graphics2D.rotate(theta, width / 2, height / 2);
        }
        graphics2D.drawRenderedImage(src, null);
        return dest;
    }

    public static void applyBorders() throws IOException {
        File path = new File("./Images/pending/");
        File[] all = path.listFiles();
        BufferedImage watermark = ImageIO.read(new File("./Images/newBor.png"));
        //resize image

        BufferedImage transpa = ImageIO.read(new File("./Images/transpa.png"));
        Watermark applied = new Watermark(Positions.CENTER, watermark, 1f);
        Watermark bg = new Watermark(Positions.CENTER, transpa, 1f);
        for(int i = 0; i<all.length; i++) {
            BufferedImage img = ImageIO.read(all[i]);
            System.out.println(i);
            System.out.println(img.getHeight());
            System.out.println(img.getWidth());
            /*if(i == 3 || i == 26 || i == 28){
                BufferedImage rot = rotateImage(img, 90);
                img = rot;
            }*/
            int w = img.getWidth();
            int h = img.getHeight();
            BufferedImage after;
            if(w == 1080){
                after = Thumbnails.of(img).size((int)(w-135), (int)(h-108)).asBufferedImage();
            } else {
                after = Thumbnails.of(img).size((int)(w*((1920/w))), (int)(h*(1080/h))).asBufferedImage();
                after = Thumbnails.of(after).size((int)(w-135), (int)(h-108)).asBufferedImage();
            }
            Watermark test = new Watermark(Positions.CENTER, after, 1f);
            BufferedImage layered = test.apply(transpa);
            BufferedImage output = applied.apply(layered);
            ImageIO.write(output, "PNG", new File("./Images/bordered/" + i + ".jpg"));
            output.flush();
        }
        System.out.println("Hello world!");

    }
    public static void applyWatermark() throws IOException {
        File path = new File("./Images/pending/");
        File[] all = path.listFiles();
        BufferedImage watermark = ImageIO.read(new File("./Images/600x338.png"));
        Watermark applied = new Watermark(Positions.BOTTOM_RIGHT, watermark, 0.5f, 50);

        List<Integer> horiz = List.of();
        for(int i = 0; i<all.length; i++){
            BufferedImage img = ImageIO.read(all[i]);
            System.out.println(i);

            /*if(i == 3 || i == 26 || i == 28){
                BufferedImage rot = rotateImage(img, 90);
                img = rot;
            }*/
            int w = img.getWidth();
            int h = img.getHeight();
            if(h <= 2912 || w <= 5184 || h >= 2912 || w >= 5184){
                img = Thumbnails.of(img).size((int)(5184), (int)(2912)).asBufferedImage();
            }
            if(horiz.contains(i)){
                img = rotateImage(img, 270);
                //ImageIO.write(img, "JPG", new File("./Images/watermarked/test" + i + ".jpg"));
            }
            System.out.println(img.getHeight());
            System.out.println(img.getWidth());
            BufferedImage output = applied.apply(img);
            ImageIO.write(output, "JPG", new File("./Images/watermarked/" + i + ".jpg"));
            output.flush();
        }

    }
    public static void main(String[] args) throws IOException {
        //applyBorders();
        applyWatermark();
    }
}