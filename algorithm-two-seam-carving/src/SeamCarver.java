import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

	private Picture picture;
	// RGB storage
	// Energy storage
	private double[][] energyMat;

	private boolean transposed; // Is the current picture in the state of already been transposed?

	public SeamCarver(Picture picture) {
		if (picture == null) {
			throw new IllegalArgumentException("Invalid picture");
		}
		this.picture = new Picture(picture);
		this.energyMat = null;
	}

	private void resetTranspose() {
		if (transposed) {
			// Flip back before returning picture.
			transpose();
		}
	}

	// current picture
	public Picture picture() {
		resetTranspose();
		return new Picture(picture);
	}

	private void recomputeEnergy() {
		energyMat = new double[picture.height()][picture.width()];
		for (int y = 0; y < picture.height(); y++) {
			for (int x = 0; x < picture.width(); x++) {
				energyMat[y][x] = energyHelper(x, y);
			}
		}
	}

	public int width() {
		resetTranspose();
		return picture.width();
	}

	public int height() {
		resetTranspose();
		return picture.height();
	}

	private double energyX_2(int x, int y) {
		int rgb1 = picture.getRGB(x - 1, y);
		int rgb2 = picture.getRGB(x + 1, y);
		int r1 = (rgb1 >> 16) & 0xFF;
		int g1 = (rgb1 >> 8) & 0xFF;
		int b1 = (rgb1 >> 0) & 0xFF;
		int r2 = (rgb2 >> 16) & 0xFF;
		int g2 = (rgb2 >> 8) & 0xFF;
		int b2 = (rgb2 >> 0) & 0xFF;
		return (r1 - r2) * (r1 - r2) + (b1 - b2) * (b1 - b2) + (g1 - g2) * (g1 - g2);
	}

	private double energyY_2(int x, int y) {
		int rgb1 = picture.getRGB(x, y - 1);
		int rgb2 = picture.getRGB(x, y + 1);
		int r1 = (rgb1 >> 16) & 0xFF;
		int g1 = (rgb1 >> 8) & 0xFF;
		int b1 = (rgb1 >> 0) & 0xFF;
		int r2 = (rgb2 >> 16) & 0xFF;
		int g2 = (rgb2 >> 8) & 0xFF;
		int b2 = (rgb2 >> 0) & 0xFF;
		return (r1 - r2) * (r1 - r2) + (b1 - b2) * (b1 - b2) + (g1 - g2) * (g1 - g2);
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y) {
		resetTranspose();
		return energyHelper(x, y);
	}

	// The helper energy method that expects transposed images.
	private double energyHelper(int x, int y) {
		if (x < 0 || y < 0 || x >= picture.width() || y >= picture.height()) {
			throw new IllegalArgumentException("Invalid cell");
		}
		return x == 0 || y == 0 || x == picture.width() - 1 || y == picture.height() - 1 ? 1000.0
				: Math.sqrt(energyX_2(x, y) + energyY_2(x, y));
	}

	private int computeHelper(int[][] pathFrom) {
		double[] curline = new double[picture.width()];

		// Init 1st line.
		for (int x = 0; x < picture.width(); x++) {
			curline[x] = energyMat[0][x]; // 1000.0
		}

		double minTotalFound = Double.MAX_VALUE;
		int minRow = -1;
		for (int y = 1; y < picture.height(); y++) {
			double[] nextline = new double[picture.width()];
			nextline[0] = energyMat[y][0] + curline[1];
			nextline[picture.width() - 1] = energyMat[y][picture.width() - 1] + curline[picture.width() - 2];
			for (int x = 1; x < picture.width() - 1; x++) {
				double minTotal = Double.MAX_VALUE;
				int minD = -2;
				for (int d = -1; d <= 1; d++) {
					if (minTotal > curline[x + d]) {
						minTotal = curline[x + d];
						minD = d;
					}
				}
				nextline[x] = minTotal + energyMat[y][x];
				pathFrom[y][x] = x + minD;

				if (y == picture.height() - 1) {
					if (minTotalFound > nextline[x]) {
						minTotalFound = nextline[x];
						minRow = x + minD;
					}
				}
			}
			curline = nextline;
		}
		return minRow;
	}

	// Change version of the picture to its transposition.
	private void transpose() {
		Picture p2 = new Picture(picture.height(), picture.width());
		for (int y0 = 0; y0 < picture.height(); y0++) {
			for (int x0 = 0; x0 < picture.width(); x0++) {
				p2.setRGB(y0, x0, picture.getRGB(x0, y0));
			}
		}
		picture = p2;
		transposed = !transposed;
	}

	private int[] operateFindSeam() {
		recomputeEnergy();
		int[] path = new int[picture.height()];
		int[][] pathFrom = new int[picture.height()][picture.width()];
		int x = computeHelper(pathFrom);
		path[picture.height() - 1] = x;
		for (int y = picture.height() - 2; y >= 1; y--) {
			path[y] = x;
			x = pathFrom[y][x];
		}
		path[0] = x;
		return path;
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		if (!transposed) {
			transpose();
		}
		return operateFindSeam();
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		if (transposed) {
			transpose();
		}
		return operateFindSeam();
	}

	// Update the stored picture to be a new one
	// with the seam removed.
	private void operateRemoveSeam(int[] seam) {
		Picture p2 = new Picture(picture.width() - 1, picture.height());
		for (int y = 0; y < p2.height(); y++) {
			for (int x = 0; x < p2.width(); x++) {
				p2.setRGB(x, y, picture.getRGB(x >= seam[y] ? x + 1 : x, y));
			}
		}
		picture = p2;
	}

	private void validateSeam(int[] seam, boolean horizontal) {
		if (seam == null) {
			throw new IllegalArgumentException("Invalid seam");
		}
		if (horizontal && !transposed || !horizontal && transposed) {
			if (seam.length != picture.width()) {
				throw new IllegalArgumentException("Invalid seam");
			}
			int s_1 = seam[0];
			for (int s : seam) {
				if (s < 0 || s >= picture.height() || Math.abs(s - s_1) > 1) {
					throw new IllegalArgumentException("Invalid seam");
				}
				s_1 = s;
			}
		} else {
			if (seam.length != picture.height()) {
				throw new IllegalArgumentException("Invalid seam");
			}
			int s_1 = seam[0];
			for (int s : seam) {
				if (s < 0 || s >= picture.width() || Math.abs(s - s_1) > 1) {
					throw new IllegalArgumentException("Invalid seam");
				}
				s_1 = s;
			}
		}
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		validateSeam(seam, true);
		if (!transposed) {
			transpose();
		}
		operateRemoveSeam(seam);
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		validateSeam(seam, false);
		if (transposed) {
			transpose();
		}
		operateRemoveSeam(seam);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
