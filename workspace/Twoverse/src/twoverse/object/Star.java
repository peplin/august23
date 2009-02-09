package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class Star extends CelestialBody implements Serializable {
    public Star(int id, User owner, String name, Timestamp birthTime, Timestamp deathTime,
            int parentId, Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration) {
        super(id, owner, name, birthTime, deathTime, parentId, position, velocity,
                acceleration);
    }
}
