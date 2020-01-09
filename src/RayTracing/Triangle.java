package RayTracing;

import java.util.List;

public class Triangle implements Surfface{
	Material mat;
	private Point vertex1;
	private Point vertex2;
	private Point vertex3;
	private Point normal;
	private InfinitePlane planeof;
	
	public Triangle() {
		this.vertex1=null;
		this.vertex1=null;
		this.vertex1=null;
		this.mat=null;
		this.normal=null;
		this.planeof=null;
	}
	public Triangle(Point ver1,Point ver2, Point ver3,Material mat) {
		this.vertex1=ver1;
		this.vertex1=ver2;
		this.vertex1=ver3;
	}

	public void setMaterial(Material mat) {
		this.mat=mat;
	}
	//gets
	public Point getver1() {
		return this.vertex1;
	}
	public Point getver2() {
		return this.vertex2;
	}
	public Point getver3() {
		return this.vertex3;
	}

	public InfinitePlane getPlaneOf() {
		return this.planeof;
	}
	//operations
	public void parseTriangle(String[] params, List<Material> materials) {
		this.vertex1=new Point(params[0],params[1],params[2]);
		this.vertex2=new Point(params[3],params[4],params[5]);
		this.vertex3=new Point(params[6],params[7],params[8]);
		int index=Integer.parseInt(params[9]);
		this.setMaterial(materials.get(index-1));
		
		Point A=this.vertex1;
		Point B=this.vertex2;
		Point C=this.vertex3;
		Point AB=B.subtract(A);
		Point AC=C.subtract(A);
		Point N=AB.cross_product(AC);
		double offset=A.dot_product(N);
		this.normal=N;
		this.planeof=new InfinitePlane(N, offset, this.mat);
	}
	public boolean equalTo(Surfface other) {
		if(other.getClass()!=Triangle.class) {
			return false;
		}
		if(this.vertex1.isEqual(((Triangle)other).getver1()) &&
				this.vertex2.isEqual(((Triangle)other).getver2())&&
				this.vertex3.isEqual(((Triangle)other).getver3())) {
			return true;
		}
		return false;
	}
	@Override
	public String toString(){
		return "Triangle: V1: "+this.vertex1+" V2: "+this.vertex2+" V3: "+this.vertex3+" Material: "+this.mat;
	}
	@Override
	public Point findIntersection(Ray ray) {
		return ray.RayTriangleIntersection(this);
	}

	@Override
	public Material getMaterial() {
		return null;
	}

	@Override
	public Point normalOnPoint(Point hit){
		return this.normal;

	}
}
