package twoverse;

import twoverse.object.Galaxy;
import twoverse.util.Point;
import twoverse.util.PhysicsVector3d;
import twoverse.util.GalaxyShape;
import twoverse.util.User;

public class TwoverseClientTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ObjectManagerClient objectManager = new ObjectManagerClient();
        RequestHandlerClient requestHandler =  new RequestHandlerClient(objectManager);

        User user = new User(0, "peplin", "123123123", "peplin@umich.edu", "3135804520", 42);

        Galaxy galaxy = new Galaxy(-1, user, "MyGalaxy", null, null, -1,
                new Point(42, 43, 44), new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8), new GalaxyShape(23, "TheShape", "TheTexture"),
                1000, 20);
        System.out.println("Galaxy ID: " + requestHandler.add(galaxy));
    }
}
