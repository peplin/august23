package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class ManmadeBody extends CelestialBody implements Serializable {
    public ManmadeBody(int id, User owner, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        super(id, owner, name, birthTime, deathTime, parentId, position, velocity,
                acceleration);
    }

    public ManmadeBody(CelestialBody body) {
        super(body);
    }
}
