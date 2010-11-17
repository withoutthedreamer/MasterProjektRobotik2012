package robot;

public class BlobfinderBlob {
//taken from PlayerBlobfinderBlob class
	// Blob id.
	private int id;
	// A descriptive color for the blob (useful for gui's).  The color
	// is stored as packed 32-bit RGB, i.e., 0x00RRGGBB.
	private int color;
	// The blob area [pixels].
	private int area;
	// The blob centroid [pixels].
	private int x;
	// The blob centroid [pixels].
	private int y;
	// Bounding box for the blob [pixels].
	private int left;
	// Bounding box for the blob [pixels].
	private int right;
	// Bounding box for the blob [pixels].
	private int top;
	// Bounding box for the blob [pixels].
	private int bottom;
	// Range to the blob center [meters]
	private float range;

	public BlobfinderBlob(int color, int area, int x, int y, int left,
			int right, int top, int bottom, float range) {
		this.color = color;
		this.area = area;
		this.x = x;
		this.y = y;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.range = range;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getRight() {
		return right;
	}
	public void setRight(int right) {
		this.right = right;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getBottom() {
		return bottom;
	}
	public void setBottom(int bottom) {
		this.bottom = bottom;
	}
	public float getRange() {
		return range;
	}
	public void setRange(float range) {
		this.range = range;
	}

}
