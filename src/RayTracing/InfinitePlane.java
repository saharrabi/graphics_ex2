package RayTracing;
import java.util.List;

public class InfinitePlane implements Surfface {
	Material material;
	private Point normal;
	private double offset;

	public InfinitePlane() {
		this.normal=null;
		this.offset=-1;
		this.material =null;
	}
	public InfinitePlane(Point normal, double off, Material material) {
		this.normal=normal;
		this.offset=off;
		this.material = material;
	}

	public Point getNormal() {
		return this.normal;
	}
	public double getOffset() {
		return this.offset;
	}
	public Material getMaterial() {
		return this.material;
	}

	public void parsePlane(String[] params,List<Material> materials) {
		this.normal=new Point(params[0],params[1],params[2]);
		this.offset=Double.parseDouble(params[3]);
		int index=Integer.parseInt(params[4]);
		this.material =materials.get(index-1);
	}
	public boolean equalTo(Surfface other) {
		if(other.getClass()!= InfinitePlane.class) {
			return false;
		}
		if(this.normal.isEqual(((InfinitePlane)other).getNormal()) && this.offset==((InfinitePlane)other).getOffset()) {
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return "Plane: Normal: "+this.normal+" Offset: "+this.offset+" Material: "+this.material.toString();
	}
	@Override
	public Point findIntersection(Ray ray) {
		return ray.RayPlaneIntersection(this);
	}
	@Override
	public Point normalOnPoint(Point hit){
		return this.normal;	
	}
}
