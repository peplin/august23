package twoverse.object;

import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class Star extends CelestialBody {
    public Star(int id, User owner, Timestamp birthTime, Timestamp deathTime,
            int parentId, Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration) {
        super(id, owner, birthTime, deathTime, parentId, position, velocity,
                acceleration);
    }
}
