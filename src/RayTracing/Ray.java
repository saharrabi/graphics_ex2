package RayTracing;

public class Ray {
	private RayTracing.Point origin;
	private Point direction;
	private float length;

	public Ray(Point eye,Point viewPlane) {
		this.origin=eye;
		this.direction=viewPlane;
		this.length=1;
	}

	public Point getOrigin() {
		return this.origin;
	}
	public Point getDirection() {
		return this.direction;
	}

	@Override
	public String toString() {
		return "Origin: "+this.origin+" Direction: "+this.direction+" Length: "+this.length;
	}
	public Point getPointOnRay(float t) {//Put t in origin+direction*t
		return new Point(this.origin.getX() + t*this.direction.getX(),
				this.origin.getY() + t*this.direction.getY(),
				this.origin.getZ() + t*this.direction.getZ());
	}
	//Sphere Intersection:
	//if returned null then there is no intersection
	public Point RaySphereIntersection(Sphere sphere) {
		Point c=sphere.getPosition();
		Point d=this.direction;
		Point o=this.origin;

		float A=d.dot_product(d);
		float B=2*( d.dot_product( o.subtract(c) ) );
		float C= c.dot_product(c)+o.dot_product(o)-2*c.dot_product(o);
		C= (float) (C-sphere.getRadius()*sphere.getRadius());
		//solve At^2+Bt+C=0 for t
		float discr=B*B-4*A*C;
		if(discr<0) {
			return null;
		}
		if(discr==0) {
			return this.getPointOnRay((-B)/(2*A));
		}
		float x1= (float) (((-B)+Math.sqrt(discr))/(2*A));
		float x2= (float) (((-B)-Math.sqrt(discr))/(2*A));
		if(x1<x2 && x1>=0) {
			return this.getPointOnRay(x1);//1
		}
		if(x2<x1 && x2>=0) {
			return this.getPointOnRay(x2);//2
		}
		return null;
	}
	//Plane Intersection:
	//if returned null then there is no intersection
	public Point RayPlaneIntersection(InfinitePlane infinitePlane) {
		float a= infinitePlane.getNormal().dot_product(this.direction);
		float b= (float) (infinitePlane.getNormal().dot_product(this.origin)- infinitePlane.getOffset());
		if(a==0 && b==0) {
			//Plane is on starting point of ray
			return this.getPointOnRay(0);
		}
		if(a==0 && b!=0) {
			//Plane and ray are makbilim
			return null;
		}
		float t=(-b)/a;
		if(t>0) {
			//System.out.println(this.getPointOnRay(t));
			return this.getPointOnRay(t);
		}
		return null;
	}

	// Inetrsection with a triangle:
	public boolean pointInTriangle(Triangle tri,Point p) {
		//triangle vertexes are A B C
		//Point P on plane: P=A+u*(C-A)+v*(B-A)
		//if 0<u,v<1 and u+1<1 then the point is inside the triangle
		//calculate u and v from point:

		Point vectorAB=tri.getver2().subtract(tri.getver1());
		Point vectorAC=tri.getver3().subtract(tri.getver1());
		Point vectorAP=p.subtract(tri.getver1());

		double devideBy=(vectorAC.dot_product(vectorAC) * vectorAB.dot_product(vectorAB) - vectorAC.dot_product(vectorAB) * vectorAC.dot_product(vectorAB));
		double u=(vectorAB.dot_product(vectorAB) * vectorAC.dot_product(vectorAP) - vectorAC.dot_product(vectorAB) * vectorAB.dot_product(vectorAP))
				/devideBy;
		double v=(vectorAC.dot_product(vectorAC) * vectorAB.dot_product(vectorAP) - vectorAC.dot_product(vectorAB) * vectorAC.dot_product(vectorAP))
				/devideBy;
		return (u >= 0) && (v >= 0) && (u + v < 1);
	}

	public Point RayTriangleIntersection(Triangle triangle) {
		/* 1)Find plane of triangle
		 * 2)Find intersection with that plane
		 * 3)Check if it's inside the triangle*/
		//1)Done in parsing funciton	
		//2)
		Point intersectionWithPlane=this.RayPlaneIntersection(triangle.getPlaneOf()/*plane*/);
		//3)
		if(intersectionWithPlane!=null && pointInTriangle(triangle,intersectionWithPlane)) {
			return intersectionWithPlane;
		}
		return null;
	}
}
