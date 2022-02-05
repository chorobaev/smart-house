#include <DHT_U.h>
#include <DHT.h>

#include <ESP8266WiFi.h>
#include <EasyButton.h>
#include <FirebaseArduino.h>
#include "DHT.h"
#include <FS.h>

#define AP_WIFI_SSID "APName" // Provide Access Point name
#define AP_WIFI_PASSWORD "password" // Provide Access Point password

#define R1_PATH ("modules/" + String(AP_WIFI_SSID) + "/R1")
#define R2_PATH ("modules/" + String(AP_WIFI_SSID) + "/R2")
#define TEMPERATURE_PATH ("modules/" + String(AP_WIFI_SSID) + "/temperature")
#define HUMIDITY_PATH ("modules/" + String(AP_WIFI_SSID) + "/humidity")

#define DHT_SEND_RATE 5 // Defines in how many led readings to send DHT info

#define FIREBASE_HOST "FIREBAS-HOST-URL"
#define FIREBASE_AUTH "FIREBASE-AUTH-TOKEN"
#define WIFI_CONFIG_PATH "/wificonfig.txt"

#define R1_PIN 13
#define R2_PIN 12
#define CONFIG_BUTTON_PIN 4
#define CONFIG_LED_PIN 14
#define DHTPIN 3

WiFiServer server(80);
EasyButton configBtn(CONFIG_BUTTON_PIN);
DHT dht(DHTPIN, DHT11);

String WIFI_SSID = "";
String WIFI_PASSWORD = "";
short dhtSendRateCount = 0;
bool defaultMode = true;

void setup() {
  Serial.begin(115200);
  Serial.println();
  configBtn.begin();
  configBtn.onPressed(onConfigButtonPressed);
  pinMode(R1_PIN, OUTPUT);
  pinMode(R2_PIN, OUTPUT);
  pinMode(CONFIG_LED_PIN, OUTPUT);

  bool mount = SPIFFS.begin(); // Mounting local storage
  if (mount) {
    Serial.println("File system mounted with success");
  } else {
    Serial.println("Error mounting the file system");
    return;
  }
  Serial.println('\n');

  lounchDefaultMode();
}

void onConfigButtonPressed() {
  defaultMode = !defaultMode;
  Serial.print("Config button pressed... Is default");
  Serial.println(defaultMode);
  WiFi.mode(WIFI_OFF);
  if (defaultMode) {
    lounchDefaultMode();
  } else {
    lounchConfigMode();
  }
}

void lounchDefaultMode() {
  Serial.println("Default mode...");
  digitalWrite(CONFIG_LED_PIN, LOW);
  bool inited = initWifiConfigs();
  if (inited) {
    Serial.println("Wifi creditionals are initialized!");
    Serial.println(WIFI_SSID);
    Serial.println(WIFI_PASSWORD);
  } else {
    Serial.println("Wifi creditionals aren't initialized!");
    lounchConfigMode();
    return;
  }
  
  delay(2000);
  Serial.println('\n');
  
  wifiConnect();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  dht.begin();

  delay(10);
}

void lounchConfigMode() {
  Serial.println("Config mode...");
  digitalWrite(CONFIG_LED_PIN, HIGH);
  WiFi.mode(WIFI_AP);
  WiFi.softAP(AP_WIFI_SSID, AP_WIFI_PASSWORD);
  server.begin();
}

void loop() {
  configBtn.read();
  if (defaultMode) {
    listenForFirebase();
    sendDHTInfo();
  } else {
    listenForWiFiConfigs();
  }
}

void listenForFirebase() {
  String r1 = Firebase.getString(R1_PATH); 
  if (Firebase.success()) {
    Serial.print("");
  } else {
    Serial.print("Getting value of R1 is failed \n");
  }
  
  String r2 = Firebase.getString(R2_PATH);
  if (Firebase.success()) {
    Serial.print("");
  } else {
    Serial.print("Getting value of R1 is failed \n");
  }
  
  digitalWrite(R1_PIN, r1.toInt());
  digitalWrite(R2_PIN, r2.toInt());
  
  delay(100);
  
  if(WiFi.status() != WL_CONNECTED) {
    wifiConnect();
  }
  
  delay(100);
}

void sendDHTInfo() {
  if (++dhtSendRateCount % DHT_SEND_RATE != 0) return;
  sendDHTInfo();
  dhtSendRateCount = 0;
  
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  if (isnan(h) || isnan(t)) {
    Serial.println(F("Failed to read from DHT sensor!"));
    return;
  }
  
  Firebase.setString(TEMPERATURE_PATH, String(t));
  if (Firebase.success()) {
    Serial.print("");
  } else {
    Serial.print("Setting humidity to firebase is failed! \n");
  }

  Firebase.setString(HUMIDITY_PATH, String(h));
  if (Firebase.success()) {
    Serial.print("");
  } else {
    Serial.print("Setting temperature to firebase is failed! \n");
  }
}

void listenForDHT() {
  float h = dht.readHumidity(); //Измеряем влажность
  float t = dht.readTemperature(); //Измеряем температуру
  if (isnan(h) || isnan(t)) {  // Проверка. Если не удается считать показания, выводится «Ошибка считывания», и программа завершает работу
    Serial.println("Ошибка считывания");
    return;
  }
}

void listenForWiFiConfigs() {
  WiFiClient client = server.available();
  if (!client) {
    return;
  }

  String request = client.readStringUntil('\r');
  Serial.println(request);
  client.flush();
  // Match request
  int ssidIndex = request.indexOf("ssid=");
  String ssid = "";
  String password = "";
  if(ssidIndex != -1) {
    for (int i = ssidIndex+5; request.charAt(i) != '&'; i++) {
      char ch = request.charAt(i);
      // Needs some optimization
      if (ch == '%') {
        ssid += ' ';
        i += 2;
      } else ssid += ch;
    }
  }
  int passwordIndex = request.indexOf("password=");
  if (passwordIndex != -1) {
    for (int i = passwordIndex+9; request.charAt(i) != ' '; i++) {
      char ch = request.charAt(i);
      if (ch == '+') password += ' '; else password += ch;
    }
  }
  client.flush();
  if (ssid.length() == 0 || password.length() == 0) return;
  bool result = saveWifiConfigs(ssid, password);
  String success = "success";
  if (!result) success = "failed";
  // JSON response
  String s = "HTTP/1.1 200 OK\r\n";
  s += "Content-Type: application/json\r\n\r\n";
  s += "{\"data\":{\"message\":\"";
  s += success;
  s += "\",\"ssid\":\"";
  s += ssid;
  s += "\",\"password\":\"";
  s += password;
  s += "\"}}\r\n";
  s += "\n";
  // Send the response to the client
  client.print(s);
  delay(1);
  Serial.println("Client disconnected");
}

void wifiConnect() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);     // Connect to the network
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID); 
  Serial.println(" ...");

  while (WiFi.status() != WL_CONNECTED && defaultMode) {   // Wait for the Wi-Fi to connect
    delay(100);
    configBtn.read();
    Serial.print('.');
  }

  Serial.println('\n');
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Connection established!");  
    Serial.print("IP address:\t");
    Serial.println(WiFi.localIP());          // Send the IP address of the ESP8266 to the computer
  }
}

bool initWifiConfigs() {
  WIFI_SSID = "";
  WIFI_PASSWORD = "";
  if (SPIFFS.exists(WIFI_CONFIG_PATH)) {
    File file = SPIFFS.open(WIFI_CONFIG_PATH, "r");
    if (!file) {
      Serial.println("Error opening fiel to init wifi configs!");
      return false;
    }
    bool isSSID = true;
    while (file.available()) {
      char ch = (char) file.read();
      if (ch == '\n') isSSID = false; else
      if (isSSID) {
        WIFI_SSID += ch;
      } else {
        WIFI_PASSWORD += ch;
      }
    }
    file.close();
    return true;
  }
  Serial.println("There is no saved wifi configs!");
  return false;
}

bool saveWifiConfigs(String ssid, String password) {
  Serial.println("WiFi configs to be saved:");
  Serial.println(ssid);
  Serial.println(password);
  File file = SPIFFS.open(WIFI_CONFIG_PATH, "w");
  if (!file) {
    Serial.println("Error opening fiel to save wifi configs!");
    return false;
  }
  int written = file.print(ssid + "\n" + password);
  file.close();
  if (written > 0) {
    Serial.println("Wifi configs seved successfully");
    return true;
  }
  Serial.println("Wifi configs not saved!");
  return false;
}
