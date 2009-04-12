package twoverse.object.applet;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import twoverse.object.Star;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
public class AppletStar extends Star implements AppletBodyInterface {
    private PApplet mParent;
    private static PImage wormImg, wormTexture;
    private static PGraphics mBlackHoleGraphic;
    private int[] reg = new int[15];
    private static boolean sInitialized;
    private static boolean sShifted = false;

    public AppletStar(PApplet parent, Star star) {
        super(star);
        mParent = parent;

        if(!sInitialized) {
            // Reference image used to transpose texture
            wormImg = mParent.loadImage("wormhole.png");
            wormImg.loadPixels();

            // Texture image array
            wormTexture = mParent.loadImage("texture.gif");
            wormTexture.loadPixels();

            mBlackHoleGraphic = mParent.createGraphics(100, 100, mParent.P2D);
            sInitialized = true;
        }
    }

    public void display() throws TwoDimensionalException {
        if(getState() == 1) {
            drawPulsar();
        } else if(getState() == 2) {
            drawBlackHole();
        } else if(getState() == 3) {
            drawSupernova();
        } else if(getState() == 4) {
            drawInert();
        } else {
            drawFormation();
        }
    }

    private void drawPulsar() {
      //TODO
        drawFormation();
    }

    private void drawSupernova() {
      //TODO
        drawFormation();
    }

    private void drawInert() {
      //TODO
        drawFormation();
    }

    private void drawBlackHole() {
        //TODO
        drawFormation();
    }

    private void drawFormation() {
        mParent.pushMatrix();
        mParent.noStroke();
        try {
            mParent.translate((float) getPosition().getX(),
                    (float) getPosition().getY(),
                    (float) getPosition().getZ());
        } catch(TwoDimensionalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mParent.fill((float) getColorR(),
                (float) getColorG(),
                (float) getColorB());
        mParent.ellipse(0, 0, (float) getRadius(), (float) getRadius());
        mParent.popMatrix();
    }
}
