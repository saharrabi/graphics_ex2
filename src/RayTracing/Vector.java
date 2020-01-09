package RayTracing;

public class Vector {
    private float x;
    private float y;
    private float z;


    public Vector(float x, float y, float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public Vector(String x,String y,String z) {
        this.x=Float.parseFloat(x);
        this.y=Float.parseFloat(y);
        this.z=Float.parseFloat(z);
    }

    public float getX() {
        return this.x;
    }
    public float getY() {
        return this.y;
    }
    public float getZ() {
        return this.z;
    }

    public float getLength() {
        float res= (float) Math.sqrt(Math.pow(this.x,2)+Math.pow(this.y,2)+Math.pow(this.z,2));
        return res;
    }
    public float dot_product(Point vec) {
        //A � B = a1b1   +   a2b2   +   a3b3
        return this.x*vec.getX()+this.y*vec.getY()+this.z*vec.getZ();
    }
    public Point cross_product(Point other) {
        //A x B = (a2b3  -   a3b2,     a3b1   -   a1b3,     a1b2   -   a2b1)
        return new Point(this.y*other.getZ()-this.z*other.getY(),this.z*other.getX()-this.x*other.getZ(),this.x*other.getY()-this.y*other.getX());
    }
    public Point add(Point vector) {
        return new Point(this.x+vector.getX(),this.y+vector.getY(),this.z+vector.getZ());
    }
    public Point subtract(Point vector) {
        return new Point(this.x-vector.getX(),this.y-vector.getY(),this.z-vector.getZ());
    }

    public Point multiplyByScalar(float scalar) {
        return new Point(this.x*scalar,this.y*scalar,this.z*scalar);
    }
    public Point normalVector() {
        float magnitude=this.getLength();
        return this.multiplyByScalar(1/magnitude);
    }


    @Override
    public String toString() {
        return "("+this.getX()+","+this.getY()+","+this.getZ()+")";
    }
    public  boolean isEqual(Point other) {
        if(this.x==other.getX() && this.y==other.getY() && this.z==other.getZ()) {
            return true;
        }
        return false;
    }
}
