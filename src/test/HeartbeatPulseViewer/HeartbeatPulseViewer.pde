/**
** Heartbeat Pulse Viewer (analog data grapher with filtering)
**
** by Christopher Peplin (chris.peplin@rhubarbtech.com)
** for August 23, 1966 (GROCS Project Group)
** University of Michigan, 2009
**
** http://august231966.com
** http://www.dc.umich.edu/grocs
**
** Copyright 2009 Christopher Peplin 
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at 
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and
** limitations under the License. 
*/

import processing.serial.*;

Serial port;
float graph[];
int currentIndex = 0;
int currentAverageIndex = 0;
final int AVERAGE_COUNT = 10;
int lastLevel = 1023;
int lastLevelCount = 0;
float bigAverage = 0;
float bigAverageIndex = 0;
final float BIG_AVERAGE_COUNT = 100;

float minimumValue = 1023;
int lastAverage = 0;
boolean upbeat = false;

void setup() {
    size(1024, 768, P3D);
    graph = new float[width];
    port = new Serial(this, Serial.list()[0], 9600); 
    stroke(255);
}

void draw() {
    background(0);
    String value = "";
    if(port.available() > 0) {
        value = trim(port.readString());
    }
    String[] values = value.split(" ");

    for(int i = 0; i < values.length; i++) {
        if(values[i] != null && values[i] != "") {
            try {
                int y = Integer.parseInt(values[i]);
                if(y > 100) { 
                    // serial in is often split, creating weird outliers
                    graph[currentIndex] += y;
                    currentAverageIndex++;
                    bigAverage += y;
                    bigAverageIndex++;
                    if(currentAverageIndex == AVERAGE_COUNT) {
                        if(graph[currentIndex] / (float)AVERAGE_COUNT < minimumValue) {
                            minimumValue = graph[currentIndex] / (float)AVERAGE_COUNT;
                        }
                        float valueAverage = graph[currentIndex] / AVERAGE_COUNT;
                        if(currentIndex >= 10 && valueAverage < graph[(currentIndex - 10) % width] 
                                / (float)AVERAGE_COUNT && !upbeat) {
                            if(!upbeat) {
                                println("value avg: " + valueAverage);
                                println("minimumValue: " + (bigAverage / bigAverageIndex));
                                background(255);
                                upbeat = true;
                            }
                        } else if(currentIndex >= 10 && valueAverage
                                > graph[(currentIndex - 10) % width]
                                / (float)AVERAGE_COUNT) {
                           upbeat = false; 
                        }
                        currentIndex = (currentIndex + 1) % (width - 1);
                        graph[currentIndex] = 0;
                        currentAverageIndex = 0;
                    }

                    if(bigAverageIndex == BIG_AVERAGE_COUNT) {
                        bigAverage = 0;
                        bigAverageIndex = 0;
                    }

                }
            } catch (NumberFormatException e) {
                println(e);
            }
        }
    }

    beginShape(LINES);
    int x = 0;
    for(int i = currentIndex + 1; i != currentIndex; i = (i + 1) % (width - 1)) {
        float valueAverage = graph[i] / AVERAGE_COUNT;
        vertex(x++, height - map(valueAverage, 0, 1023, 0, height));
    }
    endShape();
}

void keyPressed() {
    bigAverage = 0;
    bigAverageIndex = 0;
}
