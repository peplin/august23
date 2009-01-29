package twoverse.object;

import java.sql.Time;
import javax.vecmath.GVector;

abstract class CelestialBody {

    int getId() {
        return id;
    }

    User getOwner() {
        return owner;
    }

    Time getBirthTime() {
        return birthTime;
    }

    Time getDeathTime() {
        return deathTime;
    }

    CelestialBody getParent() {
        return parent;
    }

    GVector getVelocity() {
        return velocity;
    }

    GVector getAcceleration() {
        return acceleration;
    }

    Color getColor() {
        return color;
    }

    
    private int id;
    private User owner;
    private Time birthTime;
    private Time deathTime;
    private CelestialBody parent;
    private GVector velocity;
    private GVector acceleration;
    private Color color;
}
