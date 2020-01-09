package RayTracing;

public class Light {
	private Point position;
	private Color color;
	private double specularIntensity;
	private double shadowIntensity;
	private double radius;

	public Light() {
		this.position=null;
		this.color=null;
		this.specularIntensity=-1;
		this.shadowIntensity=-1;
		this.radius=-1;		
	}

	public Point getPosition() {
		return this.position;
	}
	public Color getColor() {
		return this.color;
	}
	public double getSpecular() {
		return this.specularIntensity;
	}
	public double getShadow() {
		return this.shadowIntensity;
	}
	public double getRadius() {
		return this.radius;
	}
	public void parseLight(String[] params) {
		this.position=new Point(params[0],params[1],params[2]);
		this.color=new Color(params[3],params[4],params[5]);
		this.specularIntensity=Double.parseDouble(params[6]);
		this.shadowIntensity=Double.parseDouble(params[7]);
		this.radius=Double.parseDouble(params[8]);
	}
	@Override
	public String toString() {
		return "Position: "+this.position+" Color: "+this.color+" Specular Intensity: "+this.specularIntensity+
				" Shadow Intensity: "+this.shadowIntensity+" Radius: "+this.radius;
	}
}
