package RayTracing;

public interface Surfface {
	public Point findIntersection(Ray ray);

	public Material getMaterial();
	
	public Point normalOnPoint(Point hit);
	
	public boolean equalTo(Surfface surface);
}
