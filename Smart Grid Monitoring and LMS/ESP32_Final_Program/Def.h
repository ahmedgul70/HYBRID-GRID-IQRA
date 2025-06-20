// https://randomnerdtutorials.com/esp32-save-data-permanently-preferences/
// https://simplyexplained.com/blog/Home-Energy-Monitor-ESP32-CT-Sensor-Emonlib/

#define Solar_ON_Limit 12.0  // 14.0
#define Battery_ON_Limit 9.0
#define Battery_40_Limit 9.7
#define Grid_ON_Limit 220.0

#define WiFiled 2

#define CT1 0
#define CT2 1
#define CT3 2

#define SSR1 25  // HIGH
#define SSR2 27  // NORMAL
#define SSR3 26  // LOW
#define SSR4 14

#define Button1 36
#define Button2 39
#define Button3 34
#define MODE_SW 35
#define SourceSW1 23
#define SourceSW2 18
#define SourceSW3 19

#define SOLAR_VOLTAGE_PIN 0
#define BATTERY_VOLTAGE_PIN 1
#define SOLAR_ACS712_PIN 2
#define BATTERY_ACS712_PIN 3
#define RXD2 16  // to TX of Pzem module
#define TXD2 17  // to RX of Pzem module

// VDR : R1=10k , R2=1k --> for voltage sensor
// VDR : R1=1k , R2=2k --> for acs712 sensor
/*
  1.  ADS1115 16-bit
    -> SOLAR DC VOLTAGE -- A0                         
    -> BATTERY DC VOLTAGE  -- A1
    -> SOLAR DC AMPS (ACS712-20A) -- A2             
    -> BATTERY DC AMPS (ACS712-20A) -- A3

  2.  ADS1115 16-bit
    -> LOAD # 1 CT (ACS712-20A) -- A0
    -> LOAD # 2 CT (ACS712-20A) -- A1
    -> LOAD # 3 CT (ACS712-20A) -- A2

  4. PZEM-004Tv2
  5. LCD 20X4
  6. ESP32
  7. LOAD SWs (3) -- (VP,VN,G34,G35)
  8. LOAD SSRs (3) --  USING BC547 (25,26,27)
  9. AUTO/MANUAL SW
*/

#include <Preferences.h>
#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h>
#include <ArduinoJson.h>
#include <PZEM004Tv30.h>
#include <Wire.h>
#include <Adafruit_ADS1X15.h>
#include <LiquidCrystal_I2C.h>
#include "time.h"

#define MAX_ENTRIES 30
LiquidCrystal_I2C lcd(0x27, 20, 4);
Adafruit_ADS1115 ads1; /* Use this for the 16-bit version */
Adafruit_ADS1115 ads2; /* Use this for the 16-bit version */
PZEM004Tv30 Invpzem(Serial2, RXD2, TXD2, 0x07);
PZEM004Tv30 Gridpzem(Serial2, RXD2, TXD2, 0x01);
// PZEM004Tv30 Gridpzem(Serial2, RXD2, TXD2);
Preferences preferences;
WiFiClientSecure client;

unsigned long currentMillis = 0;
unsigned long sendDataPrevMillis = 0;
unsigned long readSWMillis = 0;
unsigned long lastMillis = 0;
bool swapScreen = false;

const char* ntpServer = "pool.ntp.org";
const long gmtOffset_sec = 18000;  // +5-GMT
const int daylightOffset_sec = 0;
int dayIndex, dayOfMonth = 0;
bool LastLogged = false;

/*
  Voltage= ADC value × FULL-SCALE VOLTAGE /  RESOLUTION
  where :
  FULL-SCALE VOLTAGE => 4.096V
  RESOLUTION => 2^16 / 2 = 32768 i.e MAX ADC VALUE => 32768
*/

// ADS1115 Settings
#define SENSOR_MAX_ANALOG 26400  // 3.30V
// #define SENSOR_MIN_ANALOG 13200  // 1.65V
#define SENSOR_MIN_ANALOG 0  // 0.0V
#define ADC_Ports 4          // 4 Channels for each ADS1115

float R1 = 10000.0, R2 = 1000.0;  // voltage sensor VDR
// For ACS712-20A (100mV/A)
const float sensitivity = 0.1;  // 100mV per Amp
const float Vref = 3.3;         // ADC reference voltage

// Insert your network credentials
#define WIFI_SSID "ECC"
#define WIFI_PASSWORD "12345678"

// Insert Firebase project API Key
const String API_KEY = "AIzaSyBdBDXyjPZMg1QEPDqb9t3xAkZvHcx5Zh8";  // Replace with your Firebase Auth Token
// Insert RTDB URLefine the RTDB URL */
const String DATABASE_URL = "https://homeenergymeter-490cf-default-rtdb.firebaseio.com/";  // Replace with your Firebase URL
bool signupOK = false;

struct EnergyMeter {
  float Tpower = 0.0;
  float voltage = 0.0;
  float Tamps = 0.0;
  float units = 0.0;
  float freq = 0.0;
  float pf = 0.0;
};

struct BasicParameters {
  float power = 0.0;
  float voltage = 0.0;
  float amps = 0.0;
  String state = "OFF";
};

BasicParameters LoadA;  // HIGH - SSR1
BasicParameters LoadB;  // Medium - SSR2
BasicParameters LoadC;  // LOW - SSR3
BasicParameters Solar;
BasicParameters Battery;

EnergyMeter Grid;
EnergyMeter InvOut;

// FIREBASE RTDB PARAMETERS
float Lunits = 0;
int Total = 0;
float units[MAX_ENTRIES] = { 0 };  // day0-day29 = 30days
float previousUnits = 0;

// Units pricing Slab
float unitRate_0 = 0;      // <200
float unitRate_200 = 0;    // >200 && <700
float unitRate_700 = 0;    // >700
float unitRate_PKHRS = 0;  // PEAK HRS
int PKHRS_0 = 0;           // START HR INDEX
int PKHRS_1 = 0;           // END HR INDEX
// float previousUnits = 0.0;  // Grid Units for DataLogging each day

// USER CONTROL FROM FIREBASE
bool SW1, SW2, SW3, modeSW = false;
String load1SW, load2SW, load3SW = "OFF";  // From Firebase
String Gridstate = "OFF";

// ACS712 CALIBRATION FOR AC CURRENT MEASUREMENT
#define ADC_COUNTS 32768
#define ICAL 10.0  // (Current Calibration Constant) = 1 / sensitivity = 1 / 0.1 = 10
#define SupplyVoltage 3300.0
// double I_RATIO = ICAL * (SupplyVoltage / ADC_COUNTS);  // -1.007
double I_RATIO = ICAL * ((SupplyVoltage / 1000.0) / (ADC_COUNTS));
