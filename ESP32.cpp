#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <string>

using namespace std;


/*const char* ssid = "Orange_Swiatlowod_FB10";
const char* password = "grzempki12345";*/

const char* ssid = "SAMSUNG Jakub";
const char* password = "HasloSamsungKuba123";

const char* serverGet = "http://JakubOleksy.pythonanywhere.com/actions";
const char* serverPost = "http://JakubOleksy.pythonanywhere.com/action/add";

StaticJsonDocument<4096> jsonDocument;

unsigned long previousMillis = 0;
const unsigned long interval = 500;  // Interval in milliseconds


int LED_BUILTIN = 2;
int SENSOR_BREAK = 13; 
int ACTUATOR_PIN = 22; 
int motorpin1 = 32;
int motorpin2 = 33;
const int motorpwm = 25;


const int freq = 5000;
const int ledChannel = 0;
const int resolution = 8;
bool actuatorSawEachOther = true;

bool* codeStatus = nullptr; 
int numberOfCodes = 0;    

void setup() {
  Serial.begin(115200);

  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(SENSOR_BREAK, INPUT);   // Set the break sensor pin as INPUT
  pinMode(ACTUATOR_PIN, OUTPUT);  // Set the actuator pin as OUTPUT
  //digitalWrite(ACTUATOR_PIN, HIGH); // Initialize the actuator pin to HIGH

  pinMode(motorpin1, OUTPUT);
  pinMode(motorpin2, OUTPUT);
  pinMode(motorpwm, OUTPUT); 
    
  ledcSetup(ledChannel, freq, resolution);
  ledcAttachPin(motorpwm, ledChannel);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println("");
  Serial.print("Connected to WiFi. IP address: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
      blinkWifiConnected();
  }

    String response = getRequest(serverGet);
    
    // Parse the JSON response and get the code count
     int actionCount = countActions(response);

    if (actionCount >= 0) {
      Serial.print("Total number of actions on the server: ");
      Serial.println(actionCount);
    } else {
      Serial.println("Error: Unable to retrieve action count from the server.");
    }



  int sensorState = digitalRead(SENSOR_BREAK);

  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;  // Save the current time

    actuatorSawEachOther = readSensorState(sensorState);
    if(actuatorSawEachOther == false)
    {
      Serial.println("I don't see anything...");
    }
    else if(actuatorSawEachOther == true)
    {
      Serial.println("There you are...");
    }

    checkStatusChangeAndSpinMotor();
  }
 
 if (!actuatorSawEachOther) {
    stopMotor();  // Stop the motor if actuator pins don't see each other
    deleteRecords();
  } else {
    keepspinning();
  }
}

void blinkWifiConnected() {
  digitalWrite(LED_BUILTIN, HIGH);
  delay(500);
  digitalWrite(LED_BUILTIN, LOW);
  delay(500);
}

bool readSensorState(int sensorState) {
  if (sensorState == LOW) {
    digitalWrite(ACTUATOR_PIN, LOW);
    return false;
  } else {
    digitalWrite(ACTUATOR_PIN, HIGH);
    return true;
  }
}

void spinMotor() {
  Serial.println("Starting motor...");
  digitalWrite(motorpin1, 1);
  digitalWrite(motorpin2, 0);
  for(int dutyCycle = 95; dutyCycle >= 90 ; dutyCycle--) {
    ledcWrite(ledChannel, dutyCycle);   
    delay(600);
  }
  ledcWrite(ledChannel, 0);  
  delay(3000);
}

void stopMotor() {
  Serial.println("Stopping motor...");
  digitalWrite(motorpin1, 0);
  digitalWrite(motorpin2, 0);
  ledcWrite(ledChannel, 0);
  
}

void keepspinning() {
    return;
}

int countActions(String response) {
  // Parse the JSON response
  deserializeJson(jsonDocument, response);

  // Check if the parsed JSON document is an array
  if (jsonDocument.is<JsonArray>()) {
    int actionCount = jsonDocument.as<JsonArray>().size(); // Count the number of actions
    return actionCount;
  } else {
    return -1; // Error: JSON structure is not an array
  }
}
   
String getRequest(const char* serverName) {
  WiFiClient client;
  HTTPClient http;

  http.begin(client, serverName);
  int httpResponseCode = http.GET();

  String payload = "{}";

  if (httpResponseCode > 0) {
    payload = http.getString();
    Serial.print("HTTP Response Code: ");
    Serial.println(httpResponseCode);
    Serial.print("Server Response: ");
    Serial.println(payload);
  } else {
    Serial.print("HTTP Error Code: ");
    Serial.println(httpResponseCode);
  }
  http.end();

  return payload;
}

void deleteRecords() {
  String serverUrl = "http://JakubOleksy.pythonanywhere.com/delete_records";

  WiFiClient client;
  HTTPClient http;

  http.begin(serverUrl);
  http.setReuse(false); // Prevents persistent connections

  // Send a DELETE request
  int httpResponseCode = http.sendRequest("DELETE", "");

  if (httpResponseCode == HTTP_CODE_OK) {
    Serial.println("Records deleted successfully.");
  } else {
    Serial.print("HTTP Error Code: ");
    Serial.println(httpResponseCode);
  }

  http.end();
}

void checkStatusChangeAndSpinMotor() {
  String response = getRequest(serverGet);
  int actionCount = countActions(response);

  if (actionCount >= 0) {
    Serial.print("Total number of actions on the server: ");
    Serial.println(actionCount);
  } else {
    Serial.println("Error: Unable to retrieve action count from the server.");
  }

  // Check each code's status
  for (int i = 0; i < actionCount; i++) {
    String code = jsonDocument[i]["code"].as<String>();
    bool isUsed = jsonDocument[i]["is_used"].as<bool>();

    if (isUsed) {
      // Code status changed to true, call spinMotor() or any other action
      Serial.println("Code " + code + " status changed to true. Spinning the motor.");
      if(isUsed == true && actuatorSawEachOther == true)
      {spinMotor();}
    }
  }
}
