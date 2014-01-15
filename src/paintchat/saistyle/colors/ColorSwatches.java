package paintchat.saistyle.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import paintchat.saistyle.Tools;
import paintchat.saistyle.components.SaiPanel;

public class ColorSwatches extends SaiPanel {	

	private static final Dimension RECT_SIZE = new Dimension(15,20);
	private static final int ROWS = 3;
	
	private ColorToolManager parent;
	private ColorRectangle[][] swatches;
	private Rectangle rGrid;
	
	public ColorSwatches() {
	}
	
	public void init(ColorToolManager parent, int width) {
		this.parent = parent;		
		this.setSizes(new Dimension(width, RECT_SIZE.height * (ROWS + 1)));		
		buildRectangles();
		addMouseListener(mouseListener);
	}
	
	private void buildRectangles() {				
		int width = getWidth();
		int height = getHeight();
		
		// get exact width for rect size, then remove 1 unit and multiply out
		int gridWidth = ((width / RECT_SIZE.width) - 1) * RECT_SIZE.width;
		int gridHeight = (getHeight() / RECT_SIZE.height - 1) * RECT_SIZE.height;
		int gridWGap = (width - gridWidth) / 2;
		int gridHGap = (height - gridHeight) / 2; 
		rGrid = new Rectangle(gridWGap, gridHGap, gridWidth, gridHeight);		
		swatches = new ColorRectangle[ROWS][gridWidth / RECT_SIZE.width];
		
		for(int rowIndex = 0; rowIndex < ROWS; rowIndex++) {
			for(int columnIndex = 0; (columnIndex) * RECT_SIZE.width < gridWidth; columnIndex++) {
				ColorRectangle cr = new ColorRectangle(columnIndex * RECT_SIZE.width + rGrid.x, rowIndex * RECT_SIZE.height + rGrid.y, 
						RECT_SIZE.width, RECT_SIZE.height); 
				swatches[rowIndex][columnIndex] = cr;
			}
		}				
	}
	
	/**
	 * Return the retangle being clicked on, or null if nothing
	 * @param me
	 * @return
	 */
	private ColorRectangle inRect(MouseEvent me) {
		int x = me.getX();
		int y = me.getY();
		
		// determine closest rect
		int rowIndex = (y - rGrid.y) / RECT_SIZE.height;
		int colIndex = (x - rGrid.x) / RECT_SIZE.width;
		
		rowIndex = (rowIndex < 0)? 0 : (rowIndex >= ROWS)? ROWS - 1 : rowIndex;
		colIndex = (colIndex < 0)? 0 : (colIndex >= swatches[0].length)? swatches[0].length - 1 : colIndex;
		
		return (swatches[rowIndex][colIndex].contains(x,y)? swatches[rowIndex][colIndex] : null);
	}
	
	private MouseListener mouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			ColorRectangle cr = inRect(e);
			
			if(cr != null) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					parent.fireColorEvent(cr.rgbColor.getRGB(), null);
				}
				else {
					cr.rgbColor = new Color(parent.getRGB());
					fastPaint(cr);
				}
			}			
		}
	};		
	
	private void fastPaint(ColorRectangle cr) {
		Graphics g = getGraphics();
		cr.paint(g);
		g.dispose();
	}		
	
	@Override
	public void paint2(Graphics2D g) {
		for(int row=0; row < ROWS; row++) {
			for(int col=0; col < swatches[0].length; col++) {
				swatches[row][col].paint(g);
			}
		}
		g.setColor(Tools.C_BACKGROUND);

		int height = getHeight();
		g.fillRect(0, 0, rGrid.x, height);	// left
		g.fillRect((int)rGrid.getMaxX(), 0, rGrid.x, height); // right
		g.fillRect(rGrid.x, 0, rGrid.width, rGrid.y); // top
		g.fillRect(rGrid.x, (int)rGrid.getMaxY(), rGrid.width, rGrid.y); // top
		
		
		g.setColor(Tools.C_LINE);
		g.drawRect(rGrid.x, rGrid.y, rGrid.width, rGrid.height);
		
	}
	
	class ColorRectangle extends Rectangle {
		Color rgbColor;
		
		public ColorRectangle(int x, int y, int width, int height) {
			super(x,y,width,height);
			rgbColor = Color.WHITE; // white
		}
		
		public void paint(Graphics g) {
			g.setColor(Tools.C_LINE);
			g.drawRect(x, y, width, height);
			g.setColor(rgbColor);
			g.fillRect(x + 1, y + 1, width - 1, height - 1);		
		}
	}
}
