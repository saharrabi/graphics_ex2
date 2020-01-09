package RayTracing;

import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Main class for ray tracing exercise.
 */
public class RayTracer {
    public int imageWidth;
    public int imageHeight;
    static RayTracing.Camera camera = new RayTracing.Camera();
    static List<RayTracing.Light> lightList = new ArrayList<>();
    static List<RayTracing.Material> materialList = new ArrayList<>();
    static List<Surfface> surffaceList = new ArrayList<>();
    //Set variables:
    static int numberShadowRays, maxRecursionLevel, superSamplingLevel;
    static Color backgroundColor = null;


    /**
     * Runs the ray tracer. Takes scene file, output image file and image size as input.
     */
    public static void main(String[] args) {

        try {

            RayTracer tracer = new RayTracer();

            // Default values:
            tracer.imageWidth = 500;
            tracer.imageHeight = 500;

            if (args.length < 2)
                throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

            String sceneFileName = args[0];
            String outputFileName = args[1];

            if (args.length > 3) {
                tracer.imageWidth = Integer.parseInt(args[2]);
                tracer.imageHeight = Integer.parseInt(args[3]);
            }
            // Parse scene file:
            tracer.parseScene(sceneFileName);
            // Render scene:
            tracer.renderScene(outputFileName);

        } catch (RayTracerException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    /*======Our variables======*/
    /*========================*/

    /*========Funciton valid scene=========*/
    public void checkvalidity() throws RayTracerException {
        //if camera was set and set was set
        if (camera.getPosition() == null || backgroundColor == null) {
            throw new RayTracerException("Scene is invalid!");
        }
    }

    /*=====================================*/
    /*======Funcitions Intersection======*/
    public Intersection findClosestInter(List<Surfface> surfaces, Ray ray) {
        double minDistance = Double.MAX_VALUE;
        Surfface closestSurface = null;
        Point closestInter = null;
        for (ListIterator<Surfface> iterator = surfaces.listIterator(); iterator.hasNext(); ) {
            Surfface surface = iterator.next();
            Point intersection = surface.findIntersection(ray);
            if (intersection != null) {
                double distance = intersection.subtract(ray.getOrigin()).getLength();
                if (distance < minDistance && distance > 0.00001) {
                    minDistance = distance;
                    closestSurface = surface;
                    closestInter = intersection;
                }
            }
        }
        if (closestSurface != null && minDistance > 0.00001) {
            return new Intersection(closestInter, closestSurface);
        }
        return null;
    }

    public Intersection findClosestInter2(List<Surfface> surfaces, Ray ray, Surfface withoutme) {
        double minDistance = Double.MAX_VALUE;
        Surfface closestSurface = null;
        Point closestInter = null;
        for (ListIterator<Surfface> iterator = surfaces.listIterator(); iterator.hasNext(); ) {
            Surfface surface = iterator.next();
            Point intersection = surface.findIntersection(ray);
            if (intersection != null && !surface.equalTo(withoutme)) {
                double distance = intersection.subtract(ray.getOrigin()).getLength();
                if (distance < minDistance && distance > 0.00001) {
                    minDistance = distance;
                    closestSurface = surface;
                    closestInter = intersection;
                }
            }
        }
        if (closestSurface != null && minDistance > 0.00001) {
            return new Intersection(closestInter, closestSurface);
        }
        return null;
    }

    public Intersection findIntersectionForShadow(List<Surfface> surfaces, Ray ray, Intersection hit) {
        for (ListIterator<Surfface> iterator = surfaces.listIterator(); iterator.hasNext(); ) {
            Surfface surface = iterator.next();
            Point newhit = surface.findIntersection(ray);
            if (newhit != null) {
                if (ray.getOrigin().subtract(newhit).getLength() < ray.getOrigin().subtract(hit.getIntersectionPoint()).getLength()) {
                    //if we found a closer object
                    return new Intersection(newhit, surface);
                }
            }
        }
        return null;
    }

    /*===================================*/
    /*========Functions Color========*/
    public Color calcDiffuse(Intersection hit, Light light) {

        Material material = hit.getSurface().getMaterial();
        Point L = hit.getIntersectionPoint().subtract(light.getPosition()).normalVector();
        Point N = (hit.getSurface().normalOnPoint(hit.getIntersectionPoint())).multiplyByScalar(-1).normalVector();

        Color diffuse = light.getColor().multiplyByComp(material.getDiffuse())
                .multiplyByScalar((int) Math.abs(L.dot_product(N)));
        return diffuse;
    }

    public Color calcSpecular(Ray ray, Intersection hit, Light light) {
        Material material = hit.getSurface().getMaterial();
        Point V = ray.getDirection().subtract(ray.getOrigin()).normalVector();

        Point lightDirection = hit.getIntersectionPoint().subtract(light.getPosition()).normalVector();
        Point surfaceNormal = (hit.getSurface().normalOnPoint(hit.getIntersectionPoint())).multiplyByScalar(-1).normalVector();
        Point R = surfaceNormal.multiplyByScalar(2 * surfaceNormal.dot_product(lightDirection)).subtract(lightDirection);

        Color lightIntensity = light.getColor().multiplyByScalar((float) light.getSpecular());
        if (R.dot_product(V) < 0) {
            return new Color(0, 0, 0);
        }

        Color specular = lightIntensity
                .multiplyByComp(material.getSpecular())
                .multiplyByScalar((float) Math.pow(R.dot_product(V), material.getPhong()));

        return specular;
    }

    public Color calcLightsColor(Ray ray, Intersection hit) {
        Color color = new Color();
        //Check if shadowrayNum is 1 or not
        if (numberShadowRays == 1) {//Send just one shadow ray
            for (ListIterator<Light> iterator = lightList.listIterator(); iterator.hasNext(); ) {
                Light light = iterator.next();
                Point lightDirection = hit.getIntersectionPoint().subtract(light.getPosition()).normalVector();
                Ray rayoflight = new Ray(light.getPosition(), lightDirection);
                Intersection lighthit = findClosestInter(surffaceList, rayoflight);

                Color toAdd = calcDiffuse(hit, light).add(calcSpecular(ray, hit, light));
                //Add shadow
                if (lighthit != null && lighthit.getIntersectionPoint().subtract(hit.getIntersectionPoint()).getLength() > 0.00001) {//was == ||
                    double shadowthing = 1 - light.getShadow();
                    if (lighthit.getSurface().getMaterial().getTranparency() != 0 && !lighthit.getSurface().equalTo(hit.getSurface())) {//The material that was hit prior wasn't opaque
                        shadowthing = 1 - (light.getShadow() * (1 - lighthit.getSurface().getMaterial().getTranparency()));
                    }
                    toAdd = toAdd.multiplyByScalar((float) shadowthing);
                }
                color = color.add(toAdd);
            }
            return color;
        }
        //Send several shadow rays from a squere plane
		color = sendShadow(ray, hit, color);

		return color;
    }

	private Color sendShadow(Ray ray, Intersection hit, Color color) {
		for (ListIterator<Light> iterator = lightList.listIterator(); iterator.hasNext(); ) {
			Light light = iterator.next();
			//base color
			Color toAdd = calcDiffuse(hit, light).add(calcSpecular(ray, hit, light));
			//Soft shadow:
			Point w, h;
			//Find plane for squere light
			Point normalOfShadowPlane = ray.getDirection();
			double offset = (-1) * (normalOfShadowPlane.dot_product(light.getPosition()));
			//Calculate for finding a random point on the plane which is not the light position
			double a = normalOfShadowPlane.getX();
			double b = normalOfShadowPlane.getY();
			double c = normalOfShadowPlane.getZ();
			if (c != 0) {
				w = new Point(light.getPosition().getX() + 1, 1, (float) (-(a * (light.getPosition().getX() + 1) + b + offset) / c));
			} else {
				if (b != 0) {
					w = new Point(light.getPosition().getX() + 1, (float) (-(a * (light.getPosition().getX() + 1) + c + offset) / b), 1);
				} else {
					w = new Point((float) (-(b * (light.getPosition().getY() + 1) + c + offset) / a), light.getPosition().getY() + 1, 1);
				}
			}
			int numOfHits = numberShadowRays * numberShadowRays;
			w = w.subtract(light.getPosition()).normalVector();//was add
			h = ray.getDirection().cross_product(w).normalVector();//get a vector in 90 to the width vector on the plane
			//Start sending shadow rays
			Random rnd = new Random();
			Ray midShadowRay = new Ray(light.getPosition(), hit.getIntersectionPoint().subtract(light.getPosition()).normalVector());
			Intersection midshadowhit = findIntersectionForShadow(surffaceList, midShadowRay, hit);
			float transp = 1;
			if (midshadowhit != null && midshadowhit.getIntersectionPoint().subtract(hit.getIntersectionPoint()).getLength() > 0.00001) {
				transp = findTransparency(midShadowRay, midshadowhit, hit.getSurface());
			}
			for (int i = 0; i < numberShadowRays; i++) {
				for (int j = 0; j < numberShadowRays; j++) {
					Point startOfShadowRay = light.getPosition().add(w.multiplyByScalar((float) (((i / numberShadowRays - 0.5) + rnd.nextDouble() / numberShadowRays) * light.getRadius())))
							.add(h.multiplyByScalar((float) (((j / numberShadowRays - 0.5) + rnd.nextDouble() / numberShadowRays) * light.getRadius())));
					Ray shadowRay = new Ray(startOfShadowRay, hit.getIntersectionPoint().subtract(startOfShadowRay).normalVector());
					Intersection shadowhit = findIntersectionForShadow(surffaceList, shadowRay, hit);

					if (shadowhit != null && shadowhit.getIntersectionPoint().subtract(hit.getIntersectionPoint()).getLength() > 0.00001) {
						numOfHits--;
					}
				}
			}
			//Calculate light intensity
			double intensity = (((double) numOfHits) / (Math.pow(numberShadowRays, 2)));
			toAdd = toAdd.multiplyByScalar((float) intensity).multiplyByScalar((float) transp);
			color = color.add(toAdd);
		}
		return color;
	}

	public Color calcReflectionColor(Ray ray, Intersection hit, int recDepth) {
        Color color = new Color();
        Material mat = hit.getSurface().getMaterial();
        if (recDepth <= 0) {
            return color;
        }
        if (mat.getReflection().getR() == 0 && mat.getReflection().getG() == 0 && mat.getReflection().getB() == 0) {
            return color;
        }
        Point N = null;//(hit.getSurface().normalOnColor(hit.getIntersectionColor())).normalVector();
        Color R =null; //ray.getDirection().subtract(N.multiplyByScalar(2 * N.dot_product(ray.getDirection())));
        Ray refRay = null;//new Ray(hit.getIntersectionPoint(), R.normalVector());
        Intersection refHit = findClosestInter2(surffaceList, refRay, hit.getSurface());
        if (refHit == null) {
            return backgroundColor;
        }
        //Calculate color with the camera ray being the reflection ray
        return calcFinalColor(refRay, refHit, recDepth - 1);
    }

    public Color calcTranspColor(Ray ray, Intersection hit, int recDepth) {
        Color color = new Color();
        if (hit.getSurface().getMaterial().getTranparency() == 0) {
            //material is opaque
            return color;
        }
        Ray nextRay = new Ray(hit.getIntersectionPoint(), ray.getDirection());
        Intersection nextHit = findClosestInter(surffaceList, nextRay);
        if (nextHit == null) {
            return backgroundColor;
        }
        Ray newRay = new Ray(ray.getOrigin(), nextHit.getIntersectionPoint().subtract(ray.getOrigin()).normalVector());
        return calcFinalColor(newRay, nextHit, recDepth - 1);
    }

    public Color calcFinalColor(Ray ray, Intersection hit, int rec) {
        Color color = new Color();
        if (hit == null || rec == 0) {
            return backgroundColor;
        }
        //get all the color parts to add
        Material mat = hit.getSurface().getMaterial();
        Color colorLights = calcLightsColor(ray, hit);
        Color colorRef = calcReflectionColor(ray, hit, rec);
        Color colorTransp = calcTranspColor(ray, hit, rec);

		setRGB(color, mat, colorLights, colorRef, colorTransp);
		return color;
    }

	private void setRGB(Color color, Material mat, Color colorLights, Color colorRef, Color colorTransp) {
		color.setR(Math.min(0, colorLights.getR() * (1 - mat.getTranparency()) +
				(mat.getReflection().getR() * colorRef.getR() + (mat.getTranparency() * colorTransp.getR()))));
		color.setG(Math.min(0, colorLights.getG() * (1 - mat.getTranparency()) +
				(mat.getReflection().getG() * colorRef.getG() + (mat.getTranparency() * colorTransp.getG()))));
		color.setB(Math.min(0, colorLights.getB() * (1 - mat.getTranparency()) +
				(mat.getReflection().getB() * colorRef.getB() + (mat.getTranparency() * colorTransp.getB()))));
	}

	public float findTransparency(Ray shadowRay, Intersection shadowHit, Surfface lastHitSurface) {
        float transparensy = 1;
        int counter = 0;
        while (shadowHit != null && shadowHit.getSurface().getMaterial().getTranparency() != 0 && (!shadowHit.getSurface().equalTo(lastHitSurface)) && counter < maxRecursionLevel) {
            counter++;
            transparensy *= (shadowHit.getSurface().getMaterial().getTranparency());
            shadowHit = findIntersectionForShadow(surffaceList, new Ray(shadowHit.getIntersectionPoint(), shadowRay.getDirection()), shadowHit/*shadowHit.getSurface()*/);
        }
        return transparensy;
    }
    /*===============================*/

    /**
     * Parses the scene file and creates the scene. Change this function so it generates the required objects.
     */
    public void parseScene(String sceneFileName) throws IOException, RayTracerException {
        FileReader fr = new FileReader(sceneFileName);

        BufferedReader r = new BufferedReader(fr);
        String line = null;
        int lineNum = 0;
        System.out.println("Started parsing scene file " + sceneFileName);

        while ((line = r.readLine()) != null) {
            line = line.trim();
            ++lineNum;

            if (line.isEmpty() || (line.charAt(0) == '#')) {  // This line in the scene file is a comment
                continue;
            } else {
                String code = line.substring(0, 3).toLowerCase();
                // Split according to white space characters:
                String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

                if (code.equals("cam")) {
                    //code
                    camera.parseCamera(params);
                    System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
                } else if (code.equals("set")) {
                    //code
                    backgroundColor = new Color();
                    backgroundColor.setR(params[0]);
                    backgroundColor.setG(params[1]);
                    backgroundColor.setB(params[2]);
                    numberShadowRays = Integer.parseInt(params[3]);
                    maxRecursionLevel = Integer.parseInt(params[4]);
                    superSamplingLevel = Integer.parseInt(params[5]);

                    System.out.println(String.format("Parsed general settings (line %d)", lineNum));
                } else if (code.equals("mtl")) {
                    //code
                    Material material = new Material();
                    material.parseMaterial(params);
                    materialList.add(material);

                    System.out.println(String.format("Parsed material (line %d)", lineNum));
                } else if (code.equals("sph")) {
                    //code
                    Sphere sphere = new Sphere();
                    if (Integer.parseInt(params[4]) > materialList.size()) {
                        throw new RayTracerException(String.format("The material for this sphere doesn't exist! (line %d)", lineNum));
                    }
                    sphere.parseSphere(params, materialList);
                    surffaceList.add(sphere);

                    System.out.println(String.format("Parsed sphere (line %d)", lineNum));
                } else if (code.equals("pln")) {
                    //code
                    InfinitePlane infinitePlane = new InfinitePlane();
                    if (Integer.parseInt(params[4]) > materialList.size()) {
                        throw new RayTracerException(String.format("The material for this plane doesn't exist! (line %d)", lineNum));
                    }
                    infinitePlane.parsePlane(params, materialList);
                    surffaceList.add(infinitePlane);

                    System.out.println(String.format("Parsed plane (line %d)", lineNum));
                } else if (code.equals("trg")) {
                    //wasnt in the new one
                    Triangle triangle = new Triangle();
                    if (Integer.parseInt(params[9]) > materialList.size()) {
                        throw new RayTracerException(String.format("The material for this triangle doesn't exist! (line %d)", lineNum));
                    }
                    triangle.parseTriangle(params, materialList);
                    surffaceList.add(triangle);

                    System.out.println(String.format("Parsed Triangle (line %d)", lineNum));
                } else if (code.equals("lgt")) {
                    Light light = new Light();
                    light.parseLight(params);
                    lightList.add(light);

                    System.out.println(String.format("Parsed light (line %d)", lineNum));
                } else {
                    System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
                }
            }
        }
        // It is recommended that you check here that the scene is valid.
        checkvalidity();
        r.close();
        System.out.println("Finished parsing scene file " + sceneFileName);
    }

    /**
     * Renders the loaded scene and saves it to the specified file location.
     */
    public void renderScene(String outputFileName) {
        long startTime = System.currentTimeMillis();
        //Camera directions:
        Random rnd = new Random();
		byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];// Create a byte array to hold the pixel data:
		Point towards = camera.getDirection().normalVector();//direction towards the screen
		Point cameraRight = camera.getDirection().cross_product(camera.getUpVector()).normalVector();
		Point cameraUp = cameraRight.cross_product(camera.getDirection()).normalVector();
		//Calculate center of screen:
		Point centerScreen = camera.getPosition().add(towards.multiplyByScalar(camera.getScreenDistance()));
		//Get Down left point of screen:
		Point kodkod = centerScreen.add(cameraUp.multiplyByScalar((float) (0.5 * camera.getScreenWidth())))
				.add(cameraRight.multiplyByScalar((float) (0.5 * camera.getScreenHeight(imageHeight, imageWidth))));//go to corner of screen
		//Fill image with white
        for (int i = 0; i < this.imageHeight; i++) {
            for (int j = 0; j < this.imageWidth; j++) {
                rgbData[(i * this.imageWidth + j) * 3] = (byte) 0;
                rgbData[(i * this.imageWidth + j) * 3 + 1] = (byte) 0;
                rgbData[(i * this.imageWidth + j) * 3 + 2] = (byte) 0;
            }
        }

        // Create ray for every pixel:
		createRayForFixel(cameraRight, cameraUp, kodkod, rnd, rgbData);
		// Write pixel color values in RGB format to rgbData:
        // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
        //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
        //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
        //
        // Each of the red, green and blue components should be a byte, i.e. 0-255

        //dont change
        long endTime = System.currentTimeMillis();
        Long renderTime = endTime - startTime;

        // The time is measured for your own conveniece, rendering speed will not affect your score
        // unless it is exceptionally slow (more than a couple of minutes)
        System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

        // This is already implemented, and should work without adding any code.
        saveImage(this.imageWidth, rgbData, outputFileName);

        System.out.println("Saved file " + outputFileName);

    }

	private void createRayForFixel(Point cameraRight, Point cameraUp, Point kodkod, Random rnd, byte[] rgbData) {
		for (int i = 0; i < this.imageHeight; i++) {
			for (int j = 0; j < this.imageWidth; j++) {
				Point pixel = kodkod.subtract(cameraUp.multiplyByScalar((camera.getScreenWidth() * i / this.imageWidth)))
						.subtract(cameraRight.multiplyByScalar((float) (camera.getScreenHeight(this.imageHeight, this.imageWidth) * j / this.imageHeight)));
				Color color = new Color();
				for (int x = 0; x < superSamplingLevel; x++) {
					for (int y = 0; y < superSamplingLevel; y++) {
						//devide pixel into SSLxSSL pieces
						double u = rnd.nextDouble();
						double v = rnd.nextDouble();
						if (superSamplingLevel == 1) {
							u = 0.5;
							v = 0.5;
						}
						Point piece = pixel.add(cameraUp.multiplyByScalar((float) (u * (camera.getScreenWidth() / this.imageWidth))))
								.add(cameraRight.multiplyByScalar((float) (v * (camera.getScreenHeight(this.imageHeight, this.imageWidth) / this.imageHeight))));

						Ray ray = new Ray(camera.getPosition(), (piece.subtract(camera.getPosition())).normalVector());

						Intersection interRayWSurface = findClosestInter(surffaceList, ray);

						color = color.add(calcFinalColor(ray, interRayWSurface, maxRecursionLevel).multiplyByScalar(255));

					}
				}
				int SuperSamplingSquered = superSamplingLevel * superSamplingLevel;
				rgbData[(i * this.imageWidth + j) * 3] = (byte) (color.getR() / SuperSamplingSquered);
				rgbData[(i * this.imageWidth + j) * 3 + 1] = (byte) (color.getG() / SuperSamplingSquered);
				rgbData[(i * this.imageWidth + j) * 3 + 2] = (byte) (color.getB() / SuperSamplingSquered);

			}
		}
	}


	//////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

    /*
     * Saves RGB data as an image in png format to the specified location.
     */
    public static void saveImage(int width, byte[] rgbData, String fileName) {
        try {

            BufferedImage image = bytes2RGB(width, rgbData);
            ImageIO.write(image, "png", new File(fileName));

        } catch (IOException e) {
            System.out.println("ERROR SAVING FILE: " + e.getMessage());
        }

    }

    /*
     * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
     */
    public static BufferedImage bytes2RGB(int width, byte[] buffer) {
        int height = buffer.length / width / 3;
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        DataBufferByte db = new DataBufferByte(buffer, width * height);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        BufferedImage result = new BufferedImage(cm, raster, false, null);

        return result;
    }

    public static class RayTracerException extends Exception {
        public RayTracerException(String msg) {
            super(msg);
        }
    }


}