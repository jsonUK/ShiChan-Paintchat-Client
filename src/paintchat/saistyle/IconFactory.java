package paintchat.saistyle;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class IconFactory {

	private static final IconFactory classLoaderInstance = new IconFactory();	
	private static final String ICON_DIR = "icons";
	private static HashMap<String, BufferedImage> imageMap = new HashMap<String, BufferedImage>();
	
	
	private static BufferedImage createImage(String imgName) throws Exception {
				
		String imgURLPath = ICON_DIR + "/" + imgName + ".gif";		
		URL imgURL = classLoaderInstance.getClass().getClassLoader().getResource(imgURLPath);
		if(imgURL == null) {
			throw new FileNotFoundException("image not found: " + imgURLPath);
		}			
		
		return ImageIO.read(imgURL);					
	}
	
	/**
	 * Get image or create it if it doesnt exist
	 * 
	 * @param imgName
	 * @return
	 * @throws Exception
	 */
	private synchronized static BufferedImage getImage(String imgName) throws Exception {
		BufferedImage img = imageMap.get(imgName);
		if(img == null) {
			img = createImage(imgName);
			imageMap.put(imgName, img);//makeTransparent(img,0,0));	//disabled this because transparency seems to be working
		}
		return img;
	}
	
	
	private static BufferedImage makeTransparent(BufferedImage image, int x, int y) {
		ColorModel cm = image.getColorModel();
		if (!(cm instanceof IndexColorModel))
		return image; //sorry...
		IndexColorModel icm = (IndexColorModel) cm;
		WritableRaster raster = image.getRaster();
		int pixel = raster.getSample(x, y, 0); //pixel is offset in ICM's palette
		int size = icm.getMapSize();
		byte[] reds = new byte[size];
		byte[] greens = new byte[size];
		byte[] blues = new byte[size];
		icm.getReds(reds);
		icm.getGreens(greens);
		icm.getBlues(blues);
		IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
		return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
	}
	
	/**
	 * 
	 * @param imgName image name to load from jar
	 * @param text displayed text if any
	 * @param borderColor border color 
	 * @param backgroundColor background color (filled when made transparent)
	 * @param width 
	 * @param height
	 * @return
	 */
	public static BufferedImage createIcon(String imgName, String text, Color borderColor, boolean hardEdges, Color backgroundColor, int width, int height) {
		return createIcon(imgName,text,borderColor,hardEdges,backgroundColor,width,height, 2);
	}
	
	public static BufferedImage createIcon(String imgName, String text, Color borderColor, boolean hardEdges, Color backgroundColor, int width, int height, int padding) {
		BufferedImage tinyImg = null;
		
		try {
			if(imgName != null) {
				tinyImg = getImage(imgName);
			}
		}
		catch(Exception e) {
			// do nothing
		}
		
		if(tinyImg == null && imgName != null) {
			System.err.println("Image not found for: " + imgName);
		}
		return createIcon(tinyImg, text, borderColor, hardEdges, backgroundColor, width, height, padding);	
	}
	
	public static BufferedImage createIcon(BufferedImage tinyImg, String text, Color borderColor, boolean hardEdges, Color backgroundColor, int width, int height, int padding) {
		//BufferedImage
		BufferedImage im = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = im.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//g.setComposite(AlphaComposite.Src);
		
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		
		// draw border if it exists
		if(borderColor != null) {
			g.setColor(borderColor);
			if(hardEdges) {
				g.drawRect(0, 0, width-1, height-1);
			}
			else {
				g.drawRoundRect(0, 0, width-1, height-1, 2, 2);
			}
		}
		
		if(tinyImg != null) {	
			try {						
				// draw in bottom right corner					
				g.drawImage(tinyImg, null, width - (tinyImg.getWidth() + padding), height - (tinyImg.getHeight() + padding));				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}

				
		
		// draw background if it exists
		if(text != null) {
			g.setColor(Tools.C_FONT);
			g.setFont(Tools.F_TOOL);
			g.drawString(text, 1, 12);	
		}		
		
		g.dispose();
		return im;
	}
	
	/**
	 * Create a cursor, the size must be of 32x32
	 * @param cursorSize
	 * @return
	 */
	public static Cursor createBrushCursor(String resourceName, Point hotspot) {
		
		// get the mouse cursor
		BufferedImage cursorImg = null;
		try {
			cursorImg = getImage(resourceName);
		}
		catch(Exception e) {
			// do nothing
		}		
				
		BufferedImage im = new BufferedImage(32,32, BufferedImage.TYPE_INT_ARGB);		
		Graphics2D g = im.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
		try {						
			// draw in bottom right corner
			int transX = 0;
			int transY = 0;
			if(hotspot.x < 0) {
				transX = -hotspot.x;
				hotspot.x = 0;
			}
			if(hotspot.y < 0) {
				transY = - hotspot.y;
				hotspot.y = 0;
			}
			g.drawImage(cursorImg, (BufferedImageOp)null, transX, transY);				
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		g.dispose();
		
		return Toolkit.getDefaultToolkit().createCustomCursor(im, hotspot, "cursor_"+resourceName);
	}
	
	/**
	 * Just create the image and return it
	 * @param iconName
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage createSimpleIcon(String iconName) throws Exception {
		return createImage(iconName);
	}
	
	public static BufferedImage createPenSizeIcon(String sizeDesc, double penSize, double softness, int iconWidth, int iconHeight) {
		//BufferedImage
		BufferedImage im = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB);
		int padding = 2;
		Rectangle insets = new Rectangle(padding, padding, iconWidth - 2*padding, iconHeight - 2*padding);
		
		Graphics2D g = im.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//g.setComposite(AlphaComposite.Src);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, iconWidth, iconHeight);

		g.setFont(Tools.F_TOOL);				
		
		Rectangle2D fontRect = g.getFontMetrics().getStringBounds(sizeDesc, g);
		// calculate center of circle
		Point circleCenter = new Point(iconWidth / 2, (int)(insets.getCenterY() - fontRect.getHeight()/2));
		// draw circle in middle
		penSize = penSize > (double)insets.width? (double)insets.width : penSize < 1? 1D : penSize;
		
		double radius = penSize / 2;
		double rs1 = radius * radius;
		//double threshold = r2
		DataBuffer db = im.getRaster().getDataBuffer();
		// calculate pixels within pen square
		for(int y=circleCenter.y - (int)radius; y < circleCenter.y + radius; y++) {
			for(int x=circleCenter.x - (int) radius; x < circleCenter.x + radius; x++) {
				double rs2 = circleCenter.distanceSq(x, y);
				if(rs2 > rs1) {
					continue;
				}
				
			//	double softness = 1 - (Math.sqrt(rs2))
				
				//int pixelValue =  
				//db.setElem(i, val)
			}
		}
		
				
		g.setColor(Tools.C_FONT);		
		g.drawString(sizeDesc, (int)(circleCenter.x - fontRect.getWidth() / 2), (int)insets.getMaxY());
		
		g.dispose();
		return im;
	}
}
