import ca.ubc.cs.wiimote.*;
import ca.ubc.cs.wiimote.event.*;
import javax.bluetooth.*;
import lll.wrj4P5.*;
import lll.Loc.*;
import wiiremotej.*;
import wiiremotej.event.*;
import wiiremotej.event.WiiDeviceDiscoveryListener;

Wrj4P5 wii;

Wiimote remote;
Controller controller;
Listener listener;

void setup(){
    size(100, 100);
    controller = new Controller();
    listener = new Listener();
    /*WiimoteDiscoverer.getWiimoteDiscoverer().
            addWiimoteDiscoveryListener(listener);
    WiimoteDiscoverer.getWiimoteDiscoverer().startWiimoteSearch();
    */
    wii = new Wrj4P5(this);
    wii.connect();
}

void draw(){
  background(0);
  stroke(255);
  translate(300/2,300/2,0);
  lights();
  rotateX((int) (wii.rimokon.senced.x*30+300));
  rotateY((int) (wii.rimokon.senced.y*30+300));
  rotateZ((int) (wii.rimokon.senced.z*30+300));
  box(100,100,100);
}

void buttonPressed(RimokonEvent evt, int rid) {
   if (evt.wasPressed(RimokonEvent.TWO)) println("2");
   if (evt.wasPressed(RimokonEvent.ONE)) println("1");
   if (evt.wasPressed(RimokonEvent.B)) println("B");
   if (evt.wasPressed(RimokonEvent.A)) println("A");
   if (evt.wasPressed(RimokonEvent.MINUS)) println("Minus");
   if (evt.wasPressed(RimokonEvent.HOME)) println("Home");
   if (evt.wasPressed(RimokonEvent.LEFT)) println("Left");
   if (evt.wasPressed(RimokonEvent.RIGHT)) println("Right");
   if (evt.wasPressed(RimokonEvent.DOWN)) println("Down");
   if (evt.wasPressed(RimokonEvent.UP)) println("Up");
   if (evt.wasPressed(RimokonEvent.PLUS)) println("Plus");
}

public class Listener implements WiimoteDiscoveryListener {
    public void wiimoteDiscovered(Wiimote wiimote) {
        remote = wiimote;
        WiimoteDiscoverer.getWiimoteDiscoverer().stopWiimoteSearch();
        remote.addListener(controller);
    }
}


public class Controller implements WiimoteListener {
    void wiiAccelInput(WiiAccelEvent e) {
        System.out.println("Got accel event: x = " + e.x
                + ", y = " + e.y
                + ", z = " + e.z
                + " from controller " + e.getWiimote());
    }

    void wiiButtonChange(WiiButtonEvent e) {
        System.out.println("Got button event: " + e
                + " from controller " + e.getWiimote());
    }

    void wiiIRInput(WiiIREvent e) {
        System.out.println("Got IR event: " + e
                + " from controller " + e.getWiimote());

    }
}
