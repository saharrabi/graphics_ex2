package RayTracing;

public class Material {
    private Color diffuseColor;
    private Color specularColor;
    private float phong;
    private Color reflectionColor;
    private float transparency;

    /*double incidence;*/
    //Constructors
    public Material() {
        this.diffuseColor = null;
        this.specularColor = null;
        this.reflectionColor = null;
        this.phong = -1;
        this.transparency = -1;
    }

    public Color getDiffuse() {
        return this.diffuseColor;
    }

    public Color getSpecular() {
        return this.specularColor;
    }

    public Color getReflection() {
        return this.reflectionColor;
    }

    public double getPhong() {
        return this.phong;
    }

    public double getTranparency() {
        return this.transparency;
    }


    public void parseMaterial(String[] params) {
        this.diffuseColor = new Color(params[0], params[1], params[2]);
        this.specularColor = new Color(params[3], params[4], params[5]);
        this.reflectionColor = new Color(params[6], params[7], params[8]);
        this.phong = Float.parseFloat(params[9]);
        this.transparency = Float.parseFloat(params[10]);
    }

    @Override
    public String toString() {
        return "Diffuse Color: " + this.diffuseColor + " Specular Color: " + this.specularColor + " Reflection Color: " + this.reflectionColor +
                " Phong: " + this.phong + " Transparency: " + this.transparency;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Material material = (Material) obj;
        return (material.diffuseColor == this.diffuseColor && material.specularColor == this.specularColor
                && material.reflectionColor == this.reflectionColor && material.phong == this.phong
                && material.transparency == this.transparency);

    }

}
