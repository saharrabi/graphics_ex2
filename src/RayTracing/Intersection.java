package RayTracing;

public class Intersection {
	private Point pointOfIntersection;
	private Surfface surface;

	public Intersection(Point p,Surfface sur) {
		this.surface=sur;
		this.pointOfIntersection=p;
	}

	public void setPoint(Point p) {
		this.pointOfIntersection=p;
	}

	public Surfface getSurface() {
		return this.surface;
	}
	public Point getIntersectionPoint() {
		return this.pointOfIntersection;
	}

}
