package RayTracing;
import java.util.List;

public class Sphere implements Surfface{
	Material mat;
	private Point position;
	private double radius;
	//Constructors
	public Sphere() {
		this.mat=null;
		this.position=null;
		this.radius=-1;
	}

	public void setMaterial(Material mat) {
		this.mat=mat;
	}
	public Point getPosition() {
		return this.position;
	}
	public double getRadius() {
		return this.radius;
	}
	public Material getMaterial() {
		return this.mat;
	}

	public void parseSphere(String[] params,List<Material> materials) {
		this.position=new Point(params[0],params[1],params[2]);
		this.radius=Double.parseDouble(params[3]);
		int index=Integer.parseInt(params[4]);
		this.setMaterial(materials.get(index-1));
	}
	public boolean equalTo(Surfface other) {
		if(other.getClass()!=Sphere.class) {
			return false;
		}
		if(this.position.isEqual(((Sphere)other).getPosition()) && this.radius==((Sphere)other).getRadius()) {
			return true;
		}
		return false;
	}
	@Override
	public String toString(){
		return "Sphere: Position: "+this.position+" Radius:"+this.radius+" Material: "+this.mat;
	}
	@Override
	public Point findIntersection(Ray ray) {
		return ray.RaySphereIntersection(this);
	}
	@Override
	public Point normalOnPoint(Point hit){
		return hit.subtract(this.position);
		
	}
}
