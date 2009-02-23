package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;

import nu.xom.Element;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;

public class ManmadeBody extends CelestialBody implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6112151366389080968L;

    // This class stands for both satellites and deep space probes - one
    // is just orbiting
    public ManmadeBody(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        super(id, ownerId, name, birthTime, deathTime, parentId, position,
                velocity, acceleration);
    }

    public ManmadeBody(CelestialBody body) {
        super(body);
    }

    public ManmadeBody(Element element) {
        super(element);
    }
}
