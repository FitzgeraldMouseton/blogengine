package blogengine.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageResizer {

    private BufferedImage resizeImage(final BufferedImage sourceImage, final int width,
                                      final int height, final Object renderHint) {
        BufferedImage resizedImage = new BufferedImage(width, height, sourceImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, renderHint);
        graphics2D.drawImage(sourceImage, 0, 0, width, height, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
