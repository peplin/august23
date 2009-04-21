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
