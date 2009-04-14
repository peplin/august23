import wiremap.Wiremap;
import wiremap.WiremapSliver;

final int DEPTH_DIVISIONS = 3;

Wiremap map;
WiremapSliver sliver;
PrintWriter outfile;

int currentWire = 0;
int previousDepths[];
int newDepths[];

int round = 0;
int section = 0;

boolean sectionMode = false;
int sectionModeCounter = 0;

void setup() {
    size(1024, 768, P3D);

    map = new Wiremap(this, 256, 90, 36, 36, 48, .1875, .1875, 2,
            "emptydepths.txt");

    previousDepths = new int[map.getWireCount()];
    newDepths = new int[map.getWireCount()];

    sliver = new WiremapSliver(map, 0, 0, color(255, 255, 255),
            height, 0, color(255, 0, 0));
    sliver.setBaseColor(color(0, 255, 0));
    currentWire = 0;
}

void draw() {
    background(0);

    if(sectionMode) {
    for(int i = 0; i < map.getWireCount(); i++) {
        if(newDepths[i] == sectionModeCounter) {
            sliver.setWire(i);
            sliver.display();
        }
    }

    } else {
        if(currentWire == -1) {
            section++;
            println("advancing to section " + section + " in round " + round);
            if(section == pow(3, round)) {
                outputFile();
                round++;
                println("advancing to round " + round);
                section = 0;
            }
            while(currentWire == -1) {
                currentWire = findNextWire(section);
                if(currentWire != -1) {
                    break;
                }
                section++;
                if(section == pow(3, round)) {
                    println("no more precision possible");
                    exit();
                }
            }
            println("next current wire is " + currentWire);
        }

        sliver.setWire(currentWire);
        sliver.display();
    }
    noLoop();
}

void outputFile() {
    outfile = createWriter("calibration-round" + round + ".txt");
    for(int i = 0; i < map.getWireCount(); i++) {
        outfile.println(newDepths[i]);
        previousDepths[i] = newDepths[i];
    }
    outfile.flush();
    outfile.close();
}

void keyPressed() {
    if(keyCode == LEFT || keyCode == RIGHT || key == '1'
            || key == '2' || key == '3' || keyCode == KeyEvent.VK_SPACE) {
        if(keyCode == LEFT) {
            currentWire = max(currentWire - 1, 0);
        } else if(keyCode == RIGHT) {
            currentWire = min(currentWire + 1, map.getWireCount());
        } else if(key == '1') {
            newDepths[currentWire] = section * 3;
        } else if(key == '2') {
            newDepths[currentWire] = section * 3 + 1;
        } else if(key == '3') {
            newDepths[currentWire] = section * 3 + 2;
        } else if(keyCode == KeyEvent.VK_SPACE) {
            newDepths[currentWire] = -1;
        }
        currentWire = findNextWire(section);
        redraw();
    }

    if(key == 'v') {
        if(sectionModeCounter == 0 && !sectionMode) {
            sectionMode = true;
        } else {
            sectionModeCounter++;
        }
        if(sectionModeCounter == pow(3, round)) {
            sectionModeCounter = 0;
            sectionMode = false;
        }
        redraw();
    }
}

int findNextWire(float depth) {
    for(int i = currentWire + 1; i < map.getWireCount(); i++) {
        if(previousDepths[i] == depth) {
            return i;
        }
    }
    return -1;
}

