package paintchat.saistyle;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class BrushFactory {
	
	PixelMatrix [] matrices;
	static final double SPECIAL_DIAMETER_MAX = 5.5;
	
	public BrushFactory() {
		matrices = new PixelMatrix[5];
		
		matrices[0] = new PixelMatrix(0, 1, 1, 
				new PixelWeight[] { new PixelWeight(0.2, 0.8, 0.05)});				
		
		matrices[1] = new PixelMatrix(1, 2.5, 3, 
				new PixelWeight[] { 
				new PixelWeight(-0.2, 0.25, 0.25), new PixelWeight(-0.1, 0.68, 0.15), 
				new PixelWeight(-0.1, 0.68, 0.15), new PixelWeight(0.8, 1.2, 0.05) 				
		});
		
		matrices[2] = new PixelMatrix(2.5, 3.5, 4, 
				new PixelWeight[] { 
				new PixelWeight(-0.04, 0.09, 0.25), new PixelWeight(0.14, 0.66, 0.15), 
				new PixelWeight(0.14, 0.66, 0.15), new PixelWeight(0.96, 1.09, 0.05) 				
		});
		
		matrices[3] = new PixelMatrix(3.5, 4.5, 5, 
				new PixelWeight[] { 
				new PixelWeight(-0.16, 0.01, 0.55), new PixelWeight(0.03, 0.46, 0.35), new PixelWeight(0.2, 0.71, 0.25), 
				new PixelWeight(0.03, 0.46, 0.35), new PixelWeight(0.83, 1.01, 0.15), new PixelWeight(1, 1, 0.05),
				new PixelWeight(0.2, 0.71, 0.25), new PixelWeight(1, 1, 0.05), new PixelWeight(1, 1, 0.02)
		});
		
		matrices[4] = new PixelMatrix(4.5, 5.5, 6, 
				new PixelWeight[] { 
				new PixelWeight(0, 0, 0.75), new PixelWeight(-0.10, 0.19, 0.55), new PixelWeight(0.1, 0.60, 0.35), 
				new PixelWeight(-0.10, 0.19, 0.55), new PixelWeight(0.52, 1.01, 0.25), new PixelWeight(0.99, 1.2, 0.15),
				new PixelWeight(0.1, 0.60, 0.35), new PixelWeight(0.99, 1.2, 0.15), new PixelWeight(1, 1, 0.05)
		});
		
	}

	// Create a brush of pixels to be used by the Mg
    public int [] createBrush(double diameter, double softness) {	
		if(diameter <= SPECIAL_DIAMETER_MAX) {
			return createSmallBrush(diameter, softness);
		}
		
		int thresholdCount = 0; //TEMP, this was originally passed as the function, this will do nothing 
		
		double radius = diameter / 2D;
		int intDiameter = (int)Math.ceil(diameter) + 1;	// we want to make the number of pixels 1 larger than it should hold
		int [] pixels = new int[intDiameter*intDiameter];
		
		Rectangle rect = new Rectangle(intDiameter, intDiameter);		
		Point2D center = new Point2D.Double((int)rect.getCenterX(), (int)rect.getCenterY());
								
		double rs1 = radius * radius;
		
		double thresholdMax = rs1 + 2 * radius + 1;
		for(int i=0; i < thresholdCount; i++) {
			thresholdMax = Math.sqrt(thresholdMax);
		}
		
		// cap softness for nice soft antialiased edges
 		double threshold = thresholdMax * (1 - (softness == 0? 0.01D : softness)); 		
		threshold = threshold <= 0? 0.001D : threshold;
		
		for(int y=0; y < rect.height; y++ ){
			for(int x=0; x < rect.width; x++ ){
				double rs2 = center.distanceSq(x, y);
				double pixelWeight = 1;
				if(rs2 > rs1) {
					double r3 = Math.sqrt(rs2) - 1;
					// this is checked without square rooting for performance
					if(r3*r3 >= rs1) {
						continue;
					}
					// if we get here now square root the previous and calculate difference					
					pixelWeight = radius - r3;	// this should be less than between 0 and 1
					if(pixelWeight >= 1 || pixelWeight <= 0) {
						System.err.println("invalid pixelweight, fix me");
					}
				}								
				
				// calculate threshold
				for(int i=0; i < thresholdCount; i++) {
					rs2 = Math.sqrt(rs2);
				}
				
				int pixelValue;
				if(rs2 < threshold) {
					pixelValue = (int) (255D * pixelWeight);	// black pixel					
				}
				else {				// dark pixel 					
					pixelValue = (int) (255D * pixelWeight * (1- (((rs2 - threshold) / (thresholdMax - threshold)))));					
				}
				pixels[intDiameter * y + x] =  pixelValue;
			}
		}
		
		return pixels;
	}
    
    private int [] createSmallBrush(double diameter, double softness) {
    	for(int i=0; i < matrices.length; i++) {
    		if(matrices[i].contains(diameter)) {
    			return matrices[i].computeMatrix(diameter, softness);
    		}
    	}
    	System.err.println("Could not find diameter for: " + diameter);
    	return null;
    }
    
    private class PixelMatrix {
    	double minRadius;
    	double maxRadius;
    	
    	PixelWeight [] weights;
    	
    	public PixelMatrix(double min, double max, int dimension, PixelWeight[] quadWeights) {
    		this.minRadius = min;
    		this.maxRadius = max;
    		this.weights = new PixelWeight[dimension * dimension];
    		
    		if(dimension == 1) {
    			this.weights[0] = quadWeights[0];
    		}
    		
    		int quadDim = (int)Math.sqrt(quadWeights.length);
    		boolean isOdd = dimension % 2 == 1;
    		int reverseShift = isOdd? 2 : 1;
    		// copy in quad
    		for(int i = 0; i < dimension; i++ ) {
    			for(int j=0; j < dimension; j++) {
    				int quadIIndex = (i < quadDim)? i : ((2*quadDim) - (i + reverseShift));
    				int quadJIndex = (j < quadDim)? j : ((2*quadDim) - (j + reverseShift));
    				
    				int wIndex = i*dimension + j;
    				int qIndex = quadIIndex * quadDim + quadJIndex;
    				
    				weights[wIndex] = quadWeights[qIndex]; 
    			}
    		}    			    		
    	}
    	
    	public boolean contains(double rad) {
    		return rad > minRadius && rad <= maxRadius; 
    	}
    	
    	public int [] computeMatrix(double diameter, double softness) {    		    		
    		int [] brushPixels = new int[weights.length];
    		double cur = diameter - minRadius;
    		double length = maxRadius - minRadius;
    		double strength = cur / length;
    		
    		for(int i=0; i < brushPixels.length; i++) {
    			brushPixels[i] = weights[i].calc(strength, softness);
    		}
    		
    		return brushPixels;
    	}
    }
    
    private class PixelWeight {
    	double a;	// (ax + b) * (1 - (s * softness))
    	double b;	// ax + b
    	double s;	// how much does the softness effect this pixel
    	    	
    	PixelWeight(double min, double max, double s) {
    		this.a = (max-min);	// difference over the length, b can only be 0 - 1, mostly 0
    		this.b = min;
    		this.s = s;
    	}
    	
    	/**
    	 * 
    	 * @param strength between 0 and 1 for function
    	 * @param softness
    	 * @return
    	 */
    	final int calc(double strength, double softness) {
    		//double ax = a * strength;
    		return (int) (Math.min(255D, Math.max(0D, (255D * (a * strength + b)))) * (1 - (s * softness)));
    	}
    }
}
