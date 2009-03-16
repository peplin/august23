package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Vector;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;

public class Cluster extends CelestialBody implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1238619111582524372L;

    // This class stands for both satellites and deep space probes - one
    // is just orbiting
    public Cluster(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Vector<Integer> children) {
        super(id,
                ownerId,
                name,
                birthTime,
                deathTime,
                parentId,
                position,
                velocity,
                acceleration,
                children);
    }

    public Cluster(CelestialBody body) {
        super(body);
    }
}