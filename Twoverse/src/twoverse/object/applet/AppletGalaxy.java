
class AppletGalaxy implements AppletCelestialBodyInterface {
    private Galaxy mGalaxy;

    public AppletGalaxy(PApplet parent, Galaxy galaxy) {
        mParent = parent;
        mGalaxy = galaxy;
    }

    public void display() {
        mParent.noStroke();
        mParent.translate(galaxy.getX(), galaxy.getY(), galaxy.getZ());
        mParent.sphere(galaxy.getMass());
    }
}
