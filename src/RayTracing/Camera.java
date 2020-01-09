package RayTracing;

//import java.util.Arrays;

public class Camera {
	private Point position;
	private Point lookAtPoint;
	private Point upVector;
	private float screenDistance;
	private float screenWidth;
	//constructors
	public Camera() {
		this.position=null;
		this.lookAtPoint =null;
		this.upVector =null;
		this.screenDistance =-1;
		this.screenWidth =-1;
	}

	public Point getPosition() {
		return this.position;
	}

	public Point getUpVector() {
		return this.upVector;
	}
	public float getScreenDistance() {
		return this.screenDistance;
	}
	public float getScreenWidth() {
		return this.screenWidth;
	}
	public Point getDirection() {
		return this.lookAtPoint.subtract(this.position);
	}
	public double getScreenHeight(double imageHeight,double imageWidth) {	
		return (imageHeight*this.screenWidth)/imageWidth;
	}
	public void parseCamera(String[] params) {
		this.position=new Point(params[0],params[1],params[2]);
		this.lookAtPoint =new Point(params[3],params[4],params[5]);
		this.upVector =new Point(params[6],params[7],params[8]);
		this.screenDistance =Float.parseFloat(params[9]);
		this.screenWidth =Float.parseFloat(params[10]);

	}
	@Override
	public String toString() {
		return "Position: "+this.position.toString()+" Lookat Point: "+this.lookAtPoint +" Up vector: "+this.upVector +" Screen distance: "+this.screenDistance +" Screen Width: "+this.screenWidth;
	}
}
