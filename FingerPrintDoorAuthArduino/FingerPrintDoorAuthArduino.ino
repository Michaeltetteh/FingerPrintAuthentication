#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <Servo.h>


#define FIREBASE_HOST "door-automation.firebaseio.com"
#define FIREBASE_AUTH "eeBGofaBRo87hfqYih0N1ouE1WwbBnFBitCGgnAg"
#define WIFI_SSID "LAZARUS"
#define WIFI_PASSWORD "select * from Biggie{7520}#"

Servo door;
int state;

void setup() {
  door.attach(5);
  Serial.begin(115200);
 // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
 
 while (WiFi.status() != WL_CONNECTED) {
  Serial.print(".");
  delay(500);
 }
 
 Serial.println();
 Serial.print("connected: ");
 Serial.println(WiFi.localIP());
 
 Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

}

void openDoor(){
  door.write(90);
}
void closeDoor(){
  door.write(5);
}

void loop() {
  delay(1000);
  state = Firebase.getInt("servo_state");

// get value 
  Serial.print("servo_state: ");
  Serial.println(state);
  if(state == 1){
    openDoor();
  }else{
    closeDoor();
  }
  // digitalWrite(Relay_1, !state_1);
  Serial.println("________________________");
  delay(100);
}
