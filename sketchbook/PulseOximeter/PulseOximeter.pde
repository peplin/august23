int sensorPin = 0;    // select the input pin for the sensorentiometer
int lightLevel = 0;       // variable to store the lightLevel coming from the sensor
int previousLightLevel = 0;

const int BEAT_THRESHOLD = 10;

void setup() {
    pinMode(sensorPin, INPUT);  // declare the ledPin as an OUTPUT
    Serial.begin(9600); 
}

void loop() {
    lightLevel = analogRead(sensorPin);    // read the value from the sensor
    int lightDelta = previousLightLevel - lightLevel;
    if(abs(lightDelta) < BEAT_THRESHOLD) {
        //Serial.println("heartbeat");
    }
    
    Serial.print(lightLevel);
    Serial.print(" ");
    previousLightLevel = lightLevel;
}
