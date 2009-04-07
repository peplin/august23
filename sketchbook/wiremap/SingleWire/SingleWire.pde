import wiremap.Wiremap;
import wiremap.WiremapSliver;

Wiremap map;
WiremapSliver sliver;
PrintWriter outfile;

int currentWire = 0;
int newDepths[];

void setup() {
    size(1024, 768, P3D);

    map = new Wiremap(this, 256, 90, 36, 48, .1875, .1875, 4,
            "/home/peplin/programming/august/sketchbook/wiremap/depth256.txt");

    newDepths = new int[map.getWireCount()];

    sliver = new WiremapSliver(map, 0, 0, color(255, 255, 255),
            height, 0, color(255, 0, 0));
    outfile = createWriter("newdepths.txt");
}

void draw() {
    background(0);
    float thirdOfMaximumDepth = ((float) map.getMaximumDepth() + 1) / 3;

    if(map.getWireDepth(currentWire) > thirdOfMaximumDepth 
            && map.getWireDepth(currentWire) < thirdOfMaximumDepth * 2) {
        sliver.setBaseColor(color(255, 0, 0));
    } else if(map.getWireDepth(currentWire) > thirdOfMaximumDepth * 2) {
        sliver.setBaseColor(color(0, 255, 0));
    } else {
        sliver.setBaseColor(color(0, 0, 255));
    }
    sliver.setWire(currentWire);
    sliver.display();
    noLoop();
}

void keyPressed() {
    if(keyCode == LEFT) {
        currentWire = max(currentWire - 1, 0);
    } else if(keyCode == RIGHT) {
        currentWire = min(currentWire + 1, map.getWireCount());
    } else if(key == '1') {
        newDepths[currentWire] = 1;
        outfile.println(1);
        currentWire = min(currentWire + 1, map.getWireCount());
    } else if(key == '2') {
        newDepths[currentWire] = 2;
        outfile.println(2);
        currentWire = min(currentWire + 1, map.getWireCount());
    } else if(key == '3') {
        newDepths[currentWire] = 3;
        outfile.println(3);
        currentWire = min(currentWire + 1, map.getWireCount());
    } else if(keyCode == KeyEvent.VK_SPACE) {
        newDepths[currentWire] = 0;
        newDepths[currentWire + 1] = 0;
        outfile.println(0);
        outfile.println(0);
        currentWire = min(currentWire + 2, map.getWireCount());
    }
    outfile.flush();
    redraw();
}

