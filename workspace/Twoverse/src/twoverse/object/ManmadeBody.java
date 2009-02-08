package twoverse.object;

import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class ManmadeBody extends CelestialBody {
    public ManmadeBody(int id, User owner, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        super(id, owner, birthTime, deathTime, parentId, position, velocity,
                acceleration);
    }

    public ManmadeBody(CelestialBody body) {
        super(body);
    }
}
