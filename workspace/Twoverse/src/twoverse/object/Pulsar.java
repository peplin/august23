package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;

public class Pulsar extends CelestialBody implements Serializable {
    // This class stands for both satellites and deep space probes - one
    // is just orbiting
    public Pulsar(int id, int ownerId, String name, Timestamp birthTime,
                       Timestamp deathTime, int parentId, Point position,
                       PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        super(id, ownerId, name, birthTime, deathTime, parentId, position,
                velocity, acceleration);
    }

    public Pulsar(CelestialBody body) {
        super(body);
    }
}
