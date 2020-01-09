package RayTracing;

import java.util.Objects;

public class Color {
    private int r;
    private int g;
    private int b;

public Color() {
this.r=0;
this.g=0;
this.b=0;
}
    public Color(int r, int g, int b) {
        this.r=r;
        this.g=g;
        this.b=b;
    }
    public Color(String r, String g, String b) {
        this.r=Integer.parseInt(r);
        this.g=Integer.parseInt(g);
        this.b=Integer.parseInt(b);
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setR(int r) {
        this.r = r;
    }
    public void setR(String r) {
        this.r=Integer.parseInt(r);
    }
    public void setG(String g) {
        this.g=Integer.parseInt(g);
    }
    public void setB(String b) {
        this.b=Integer.parseInt(b);
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color)) return false;
        Color color = (Color) o;
        return r == color.r &&
                g == color.g &&
                b == color.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }

    public Color add(Color toAdd) {
    return new Color(this.r+toAdd.getR(),this.g+toAdd.getG(),this.b+toAdd.getB());

    }

    public Color multiplyByComp(Color diffuse) {
    return new Color(this.r*diffuse.getR(),this.g*diffuse.getG(),this.b*diffuse.getB());
    }

    //not sure
    //maybe double
    public Color multiplyByScalar(float scalar) {
        return new Color(Math.round(this.r*scalar),Math.round(this.g*scalar),Math.round(this.b*scalar));
    }

    public void setR(double min) {
    setR((int)min);
    }

    public void setG(double min) {
        setG((int)min);
    }

    public void setB(double min) {
        setB((int)min);
    }
}