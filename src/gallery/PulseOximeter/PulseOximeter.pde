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

const int SENSOR_PIN = 0;    // select the input pin for the sensorentiometer
const int AVERAGE_COUNT = 20;

int averageLevel = 0;
int averageIndex = 0;

void setup() {
    pinMode(SENSOR_PIN, INPUT);  // declare the ledPin as an OUTPUT
    Serial.begin(9600); 
}

void loop() {
    int lightLevel = analogRead(SENSOR_PIN);    // read the value from the sensor
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
