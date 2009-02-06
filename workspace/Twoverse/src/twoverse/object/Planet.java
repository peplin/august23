package twoverse.object;

import java.awt.Color;
import java.sql.Time;

import twoverse.util.PhysicsVector3d;
import twoverse.util.User;

public class Planet extends CelestialBody {
    public Planet(int id, User owner, Time birthTime, Time deathTime,
            CelestialBody parent, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Color color) {
        super(id, owner, birthTime, deathTime, parent, velocity, acceleration,
                color);
    }
}
