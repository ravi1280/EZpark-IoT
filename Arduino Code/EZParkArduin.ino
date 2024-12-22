#include <SPI.h>
#include <MFRC522.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ESP32Servo.h>

// WiFi credentials
const char* ssid = "***";      
const char* password = "***";

// Server URLs
const String rfidServerUrl = "https://server/EZPark/Rfid"; 
const String distanceServerUrl = "https://server/EZPark/test"; 
WiFiServer server(80);

// Ultrasonic sensor pins
const int trigPin = 12; 
const int echoPin = 14;

// Servo motor
Servo myServo;
const int servoPin = 13;

// Flags and variables
bool sentAboveThreshold = false; // Flag for distance > 5 sent

// RFID module pins
#define SS_PIN 21       // SDA pin connected to GPIO21 on ESP32
#define RST_PIN 5       // RST pin connected to GPIO5 on ESP32
MFRC522 rfid(SS_PIN, RST_PIN); // Create MFRC522 instance

void setup() {
  Serial.begin(115200);

  // Setup ultrasonic sensor pins
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(16, OUTPUT);

  // Setup servo motor
  myServo.attach(servoPin);

  // Setup RFID module
  SPI.begin();                      // Initialize SPI bus
  rfid.PCD_Init();                  // Initialize MFRC522 module
  Serial.println("RFID Reader Initialized");

  // Connect to WiFi
  Serial.print("Connecting to WiFi");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected.");

  // Start HTTP server
  server.begin();
  Serial.println("Server started!");
  Serial.println(WiFi.localIP());
}

void loop() {
  measureAndSendDistance(); // Measure distance and send to server
  handleClientRequests();   // Handle HTTP requests for servo control
  readAndSendRFID();        // Read RFID data and send to server
}

void measureAndSendDistance() {
  long duration, distance;

  // Trigger ultrasonic pulse
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  // Measure the response pulse duration
  duration = pulseIn(echoPin, HIGH);

  // Calculate the distance in cm
  distance = duration * 0.034 / 2;

  if (distance < 5) {
    // Always send if distance < 5
    sendDataToServer(distance);
    sentAboveThreshold = false; // Reset the flag
  } else if (!sentAboveThreshold) {
    // Send one value for distance > 5
    sendDataToServer(distance);
    sentAboveThreshold = true; // Set the flag
  }

  delay(500); // Wait for a second before sending the next data
}

void sendDataToServer(long distance) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(distanceServerUrl);
    http.addHeader("Content-Type", "application/json");

    String jsonData = "{\"distance\": " + String(distance) + "}";
    int httpResponseCode = http.POST(jsonData);

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println("Data sent to server successfully.");
      Serial.println(response);
    } else {
      Serial.print("Error code: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  } else {
    Serial.println("WiFi not connected.");
  }
}

void handleClientRequests() {
  WiFiClient client = server.available();
  if (client) {
    String requestText = "";
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        Serial.write(c);
        requestText += c;

        if (c == '\n') {
          Serial.println(requestText);

         
          if (requestText.startsWith("GET /?status=70")) {
            myServo.write(0); 
             digitalWrite(16, LOW);// Move servo to 0 degrees
          } else if (requestText.startsWith("GET /?status=90")) {
            myServo.write(90);
             digitalWrite(16, HIGH); // Move servo to 90 degrees
          }
         
          break;
        }
      }
    }
    client.stop();
  }
}

void readAndSendRFID() {
  if (!rfid.PICC_IsNewCardPresent()) {
    delay(500); // Wait and continue
    return;
  }

  if (!rfid.PICC_ReadCardSerial()) {
    delay(500); // Wait and continue
    return;
  }

  // Construct UID string
  String uid = "";
  for (byte i = 0; i < rfid.uid.size; i++) {
    uid += String(rfid.uid.uidByte[i] < 0x10 ? "0" : "") + String(rfid.uid.uidByte[i], HEX);
  }

  Serial.print("UID: ");
  Serial.println(uid);

  // Send UID to server
  sendUIDToServer(uid);

  rfid.PICC_HaltA();
}

void sendUIDToServer(String uid) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(rfidServerUrl);
    http.addHeader("Content-Type", "application/json");

    String jsonData = "{\"uid\": \"" + uid + "\"}";
    int httpResponseCode = http.POST(jsonData);

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println("RFID data sent to server successfully.");
      Serial.println(response);
    } else {
      Serial.print("Error in sending RFID data: ");
      Serial.println(httpResponseCode);
    }
    http.end();
  } else {
    Serial.println("WiFi not connected");
  }
}
