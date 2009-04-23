/**
** Pulse Oximeter (Analog Sensor Averaging)
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

const int SENSOR_PIN = 0;
const int AVERAGE_COUNT = 20;

int averageLevel = 0;
int averageIndex = 0;

/**
 * The Pulse Oximeter sketch is an Arduino sketch intended for use with an
 * analog sensor reading the amount of light passing through a human finger.
 * 
 * It could be made more generically into an analog signal averaging sketch.
 * There is nothing here that specifically deals with heart rate measurement.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
void setup() {
    pinMode(SENSOR_PIN, INPUT);
    Serial.begin(9600); 
}

void loop() {
    int lightLevel = analogRead(SENSOR_PIN);
    if(lightLevel > 100) {

        averageLevel += lightLevel;
        averageIndex++;
        if(averageIndex >= AVERAGE_COUNT) {
            averageLevel /= AVERAGE_COUNT;

            Serial.print(averageLevel);
            Serial.print(" ");

            averageLevel = 0;
            averageIndex = 0;
        }
    }
}
