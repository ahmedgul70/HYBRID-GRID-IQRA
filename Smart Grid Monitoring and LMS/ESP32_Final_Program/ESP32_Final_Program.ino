#define sendInterval 1000  // FIREBASE DATA SENDING TIMEOUT

#include "Config.h"

// ***********************************************************************************
void setup() {
  Init();
  Serial.begin(115200);
  Serial2.begin(9600, SERIAL_8N1, RXD2, TXD2);
  adsInit();
  Serial.println("Serial OK.");

  initWiFi();
  client.setInsecure();  // Use insecure mode for HTTPS
  initFirebase();
  read_RTDB();
  // read_Datalogger();

  // Init and get the time
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  printLocalTime();
  delay(1000);
  Serial.println("Smart Grid FYP");
}

void loop() {
  currentMillis = millis();
  HybridEMS();  // Source Priority based LMS

  if (currentMillis - sendDataPrevMillis > 2500) {
    scanCT(300);
    Scan_Solar_Battery();
    scanPzem();
    lcdUpdate();
    Write();
    // WriteEnergy();
    sendDataPrevMillis = currentMillis;
  }
  if (currentMillis - lastMillis > 60000 && scanNTP()) {
    lastMillis = currentMillis;
    int index = dayOfMonth - 1;  // day0-day29 = 30days
    logData(index);
  }
  delay(10);
}
// ***********************************************************************************

bool scanNTP() {
  int hour = 0;
  struct tm timeinfo;

  if (getLocalTime(&timeinfo)) {
    hour = timeinfo.tm_hour;
    dayOfMonth = timeinfo.tm_mday;
    int month = timeinfo.tm_mon;

    if (hour == 0 && !LastLogged) {
      return true;  // Midnight 12.00 am
    } else if (hour > 0 && LastLogged) {
      LastLogged = false;
    }
  } else {
    Serial.println("Failed to get time!");
  }
  return false;
}

// Function to log float data
void logData(int index) {
  LastLogged = true;

  // Get the next available index (circular buffer approach)
  if (index < MAX_ENTRIES) {  // Ensure we don't exceed the storage limit
    units[index] = Grid.units;
    WriteUnits(index);
    previousUnits = Grid.units;
  }
}
