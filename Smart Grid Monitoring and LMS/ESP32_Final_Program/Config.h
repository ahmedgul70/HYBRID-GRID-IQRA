#include "Def.h"



// Writes complete JSON to RTDB
void Write() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = DATABASE_URL + "Data.json?auth=" + API_KEY;
    http.begin(client, url);
    http.addHeader("Content-Type", "application/json");

    StaticJsonDocument<1024> doc;
    doc["SolarAmps"] = Solar.amps;
    doc["SolarVolt"] = Solar.voltage;
    doc["SolarPower"] = Solar.power;
    doc["SolarState"] = Solar.state;

    doc["BatteryAmps"] = Battery.amps;
    doc["BatteryVolt"] = Battery.voltage;
    doc["BatteryPower"] = Battery.power;
    doc["Batterystate"] = Battery.state;

    doc["GridAmps"] = Grid.Tamps;
    doc["GridVolt"] = Grid.voltage;
    doc["GridPower"] = Grid.Tpower;
    doc["Gridfreq"] = Grid.freq;
    doc["Gridpf"] = Grid.pf;
    doc["Gridstate"] = Gridstate;
    doc["Gridunits"] = Grid.units;

    doc["Tvolt"] = LoadA.power;
    doc["amps1"] = LoadA.amps;
    doc["pow1"] = LoadA.power;
    doc["Load1state"] = LoadA.state;

    doc["Tvolt"] = LoadB.power;
    doc["amps2"] = LoadB.amps;
    doc["pow2"] = LoadB.power;
    doc["Load2state"] = LoadB.state;

    doc["Tvolt"] = LoadC.power;
    doc["amps3"] = LoadC.amps;
    doc["pow3"] = LoadC.power;
    doc["Load3state"] = LoadC.state;

    doc["Tamps"] = InvOut.Tamps;
    doc["Tvolt"] = InvOut.voltage;
    doc["Tpower"] = InvOut.Tpower;
    doc["Tpf"] = InvOut.pf;
    doc["Tunits"] = InvOut.units;

    String payload;
    serializeJson(doc, payload);
    int code = http.PATCH(payload);

    Serial.println("Data Sent!");
    http.end();
  }
}

void WriteEnergy() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = DATABASE_URL + "Energy.json?auth=" + API_KEY;
    http.begin(client, url);
    http.addHeader("Content-Type", "application/json");

    StaticJsonDocument<1024> doc;
    doc["Cunits"] = Grid.units;
    doc["Lunits"] = Lunits;
    doc["Total"] = Total;

    String payload;
    serializeJson(doc, payload);
    int code = http.PATCH(payload);

    Serial.println("Data Sent!");
    http.end();
  }
}

void WriteUnits(int index) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = DATABASE_URL + "DataLog.json?auth=" + API_KEY;
    http.begin(client, url);
    http.addHeader("Content-Type", "application/json");

    StaticJsonDocument<1024> doc;
    doc["day" + String(index)] = Grid.units - previousUnits;

    String payload;
    serializeJson(doc, payload);
    int code = http.PATCH(payload);

    Serial.println("Data Sent!");
    http.end();
  }
}

void read_Datalogger() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = DATABASE_URL + "DataLog.json?auth=" + API_KEY;
    http.begin(client, url);
    int code = http.GET();

    if (code > 0) {
      Serial.print("GET Response Code : ");
      Serial.println(code);

      StaticJsonDocument<200> doc;
      DeserializationError err = deserializeJson(doc, http.getString());
      if (!err) {
        units[0] = doc["day0"];
        units[1] = doc["day1"];
        units[2] = doc["day2"];
        units[3] = doc["day3"];
        units[4] = doc["day4"];
        units[5] = doc["day5"];
        units[6] = doc["day6"];
        units[8] = doc["day8"];
        units[9] = doc["day9"];
        units[10] = doc["day10"];
        units[11] = doc["day11"];
        units[12] = doc["day12"];
        units[13] = doc["day13"];
        units[14] = doc["day14"];
        units[15] = doc["day15"];
        units[16] = doc["day16"];
        units[17] = doc["day17"];
        units[18] = doc["day18"];
        units[19] = doc["day19"];
        units[20] = doc["day20"];
        units[21] = doc["day21"];
        units[22] = doc["day22"];
        units[23] = doc["day23"];
        units[24] = doc["day24"];
        units[25] = doc["day25"];
        units[26] = doc["day26"];
        units[27] = doc["day27"];
        units[28] = doc["day28"];
        units[29] = doc["day29"];
      } else {
        Serial.println("JSON Parsing Error !");
      }
    } else {
      Serial.print("Error on sending GET request: ");
      Serial.println(code);
    }
    http.end();
  }
}

int FetchNTP() {
  int hour = 0;
  struct tm timeinfo;
  if (getLocalTime(&timeinfo)) {
    hour = timeinfo.tm_hour;
    // Serial.printf("\n Hour: %02d, Minute: %02d, Day of Month: %d\n", hour, minute, dayOfMonth);
  } else {
    Serial.println("Failed to get time!");
  }
  return hour;
}

// Reads complete JSON from RTDB
void read_RTDB_SW() {
  if (currentMillis - readSWMillis > 1000) {
    if (WiFi.status() == WL_CONNECTED) {
      HTTPClient http;
      String url = DATABASE_URL + "Buttons.json?auth=" + API_KEY;
      http.begin(client, url);
      int code = http.GET();

      if (code > 0) {
        StaticJsonDocument<200> doc;
        DeserializationError err = deserializeJson(doc, http.getString());
        if (!err) {
          load1SW = doc["sw1"].as<String>();
          load2SW = doc["sw2"].as<String>();
          load3SW = doc["sw3"].as<String>();
        } else {
          Serial.println("JSON Parsing Error !");
        }
      } else {
        Serial.print("Error on sending GET request: ");
        Serial.println(code);
      }
      http.end();
    }
    readSWMillis = currentMillis;
  }
}

void Manual(int selection) {
  read_RTDB_SW();
  SW1 = digitalRead(Button1);
  SW2 = digitalRead(Button2);
  SW3 = digitalRead(Button3);

  if (SW1 == 0 || load1SW == "ON") {  // Turn ON
    digitalWrite(SSR1, HIGH);
    LoadA.state = "ON";
  } else {
    digitalWrite(SSR1, LOW);
    LoadA.state = "OFF";
  }

  if (selection != 3 && (SW2 == 0 || load2SW == "ON")) {  // Turn ON
    digitalWrite(SSR2, HIGH);
    LoadB.state = "ON";
  } else {
    digitalWrite(SSR2, LOW);
    LoadB.state = "OFF";
  }

  if (selection == 1 && (SW3 == 0 || load3SW == "ON")) {  // Turn ON
    digitalWrite(SSR3, HIGH);
    LoadC.state = "ON";
  } else {
    digitalWrite(SSR3, LOW);
    LoadC.state = "OFF";
  }
}

// Smart Hybrid Energy Management System
void HybridEMS() {
  // selection criteria
  // 1.  SSR1 , SSR2 , SSR3 --> Solar and Non-peak hrs of Grid
  // 2.  SSR1 , SSR2 --> during peak hrs of Grid and during battery
  // 3.  SSR1 --> when battery < 40%
  // int PKHRS_0 = 0;  // START HR INDEX
  // int PKHRS_1 = 0;  // END HR INDEX
  int selection = 0;
  int currentHr = FetchNTP();
  Solar.state = (Solar.voltage > Solar_ON_Limit) ? "ON" : "OFF";
  Battery.state = (Battery.voltage > Battery_ON_Limit) ? "ON" : "OFF";
  Gridstate = (Grid.voltage > Grid_ON_Limit) ? "ON" : "OFF";

  // SOLAR
  if (Solar.state == "ON") {
    selection = 1;
  }
  // GRID
  else if (Gridstate == "ON") {
    // PEAK HRS
    if (currentHr >= PKHRS_0 && currentHr <= PKHRS_1) {
      selection = 2;
    }
    // NON-PEAK HRS
    else {
      selection = 1;
    }
  }
  // BATTERY
  else if (Battery.state == "ON") {
    // LESS THAN 40%
    if (Battery.voltage >= Battery_40_Limit) {
      selection = 3;
    } else {
      selection = 2;
    }
  }
  if (selection > 0) Manual(selection);
}

void lcdUpdate2() {
  lcd.clear();
  lcd.setCursor(3, 0);
  lcd.print("Grid Specs");

  lcd.setCursor(0, 1);
  lcd.print("Voltage = ");
  lcd.print(Grid.voltage, 1);
  lcd.print(" V");

  lcd.setCursor(0, 2);
  lcd.print("Current = ");
  lcd.print(Grid.Tamps, 2);
  lcd.print(" A");

  lcd.setCursor(0, 3);
  lcd.print("Units = ");
  lcd.print(Grid.units, 2);
  lcd.print(" KWh");
}

void lcdUpdate1() {
  lcd.clear();
  lcd.setCursor(3, 0);
  lcd.print("Sources Status");

  lcd.setCursor(0, 1);
  lcd.print("Grid Status= ");
  lcd.print(Gridstate);

  lcd.setCursor(0, 2);
  lcd.print("Solar Status= ");
  lcd.print(Solar.state);

  lcd.setCursor(0, 3);
  lcd.print("Battery Status= ");
  lcd.print(Battery.state);
}

void lcdUpdate() {
  if (swapScreen) {
    lcdUpdate2();
    swapScreen = false;
  } else {
    lcdUpdate1();
    swapScreen = true;
  }
}

// Reads complete JSON from RTDB
void read_RTDB() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = DATABASE_URL + "UserInput.json?auth=" + API_KEY;
    http.begin(client, url);
    int code = http.GET();

    if (code > 0) {
      Serial.print("GET Response Code : ");
      Serial.println(code);

      StaticJsonDocument<200> doc;
      DeserializationError err = deserializeJson(doc, http.getString());
      if (!err) {
        String str1 = doc["PK0"].as<String>();
        String str2 = doc["PK1"].as<String>();
        String str3 = doc["unitsPK"].as<String>();
        String str4 = doc["units0"].as<String>();
        String str5 = doc["units200"].as<String>();
        String str6 = doc["units700"].as<String>();
        PKHRS_0 = str1.toFloat();
        PKHRS_1 = str2.toFloat();
        unitRate_PKHRS = str3.toFloat();
        unitRate_0 = str4.toFloat();
        unitRate_200 = str5.toFloat();
        unitRate_700 = str6.toFloat();
      } else {
        Serial.println("JSON Parsing Error !");
      }
    } else {
      Serial.print("Error on sending GET request: ");
      Serial.println(code);
    }
    http.end();
  }
}

void debug() {
  SW1 = digitalRead(Button1);
  SW2 = digitalRead(Button2);
  SW3 = digitalRead(Button3);

  if (SW1 == 0 || load1SW == "ON") {  // Turn ON
    digitalWrite(SSR1, HIGH);
    LoadA.state = "ON";
  } else {
    digitalWrite(SSR1, LOW);
    LoadA.state = "OFF";
  }

  if (SW2 == 0 || load2SW == "ON") {  // Turn ON
    digitalWrite(SSR2, HIGH);
    LoadB.state = "ON";
  } else {
    digitalWrite(SSR2, LOW);
    LoadB.state = "OFF";
  }

  if (SW3 == 0 || load3SW == "ON") {  // Turn ON
    digitalWrite(SSR3, HIGH);
    LoadC.state = "ON";
  } else {
    digitalWrite(SSR3, LOW);
    LoadC.state = "OFF";
  }
}

// Calculates Irms from CTs
void scanCT(int Number_of_Samples) {
  Serial.println("Scanning CTs......");
  unsigned long raw;
  double sumI_1, sumI_2, sumI_3 = 0;
  double voltage, current, adc_voltage = 0;

  for (int n = 0; n < Number_of_Samples; n++) {
    raw = ads2.readADC_SingleEnded(0);
    adc_voltage = (raw * 4.096) / 32768.0;  // 16-bit ADC
    voltage = (adc_voltage * 1.5);          // VDR : R1=1k , R2=2k --> for acs712 sensor
    current = (voltage - 2.51) / 0.1;       // sensitivity = 0.1 , Zero_Amps_Factor = 2.51
    sumI_1 += current * current;

    raw = ads2.readADC_SingleEnded(1);
    adc_voltage = (raw * 4.096) / 32768.0;  // 16-bit ADC
    voltage = (adc_voltage * 1.5);          // VDR : R1=1k , R2=2k --> for ACS712 sensor
    current = (voltage - 2.51);             // sensitivity = 0.1;
    sumI_2 += current * current;

    raw = ads2.readADC_SingleEnded(2);
    adc_voltage = (raw * 4.096) / 32768.0;  // 16-bit ADC
    voltage = (adc_voltage * 1.5);          // VDR : R1=1k , R2=2k --> for ACS712 sensor
    current = (voltage - 2.51);             // sensitivity = 0.1;
    sumI_3 += current * current;

    delayMicroseconds(100);  // Sample rate ~1kHz
  }
  // double I_RATIO = ICAL * ((SupplyVoltage / 1000.0) / (ADC_COUNTS));
  // double Irms = I_RATIO * sqrt(sumI / Number_of_Samples);

  LoadA.amps = sqrt(sumI_1 / Number_of_Samples);
  if (LoadA.amps < 0.2) LoadA.amps = 0;
  Serial.printf("\t CT # 1 : %.2f \n", LoadA.amps);
  LoadB.amps = sqrt(sumI_2 / Number_of_Samples);
  Serial.printf("\t CT # 2 : %.2f \n", LoadB.amps);
  LoadC.amps = sqrt(sumI_3 / Number_of_Samples);
  Serial.printf("\t CT # 3 : %.2f \n", LoadC.amps);
}

// FOR DC CURRENT MEASUREMENT
float scanACS712(float adc_voltage, float Zero_Amps_Factor) {
  // Calculate voltage at divider input
  // (1000.0 + 2000.0) / 2000.0) = 1.5
  float Vin = (adc_voltage * 1.5);  // VDR : R1=1k , R2=2k --> for acs712 sensor
  // ACS712 gives ~2.5V at 0A
  float current = (Vin - Zero_Amps_Factor) / sensitivity;
  return current;
}

// FOR DC VOLTAGE MEASUREMENT
float scanDCvolts(float adc_voltage) {
  // Vout = (adc_value * 3.3) / 4095.0; // when using VDR directly with ESP32 ADC
  // Vout = (adc_value * 4.096) / 32768.0;  // when using VDR with ADS1115 ADC

  // Calculate voltage at divider input
  float Vin = ((adc_voltage * (10000.0 + 1000.0) / 1000.0));  // VDR : R1=10k , R2=1k --> for voltage sensor
  return Vin;
}

void Scan_Solar_Battery() {
  Serial.println("Scanning DC Sensors......");
  float vIN, Vout = 0;

  for (int channel = 0; channel < ADC_Ports; channel++) {
    unsigned long raw = 0;
    for (int j = 0; j < 10; j++) {
      raw += ads1.readADC_SingleEnded(channel);
      delay(1);  // Small delay to reduce noise
    }
    raw /= 10;                                                   // Average of 10 samples
    raw = constrain(raw, SENSOR_MIN_ANALOG, SENSOR_MAX_ANALOG);  // From ADS1115 ADC
    Vout = (raw * 4.096) / 32768.0;                              // Voltage output at ADS1115 ADC pin

    switch (channel) {
      case 0:
        // (10000.0 + 1000.0) / 1000.0) = 11
        vIN = (Vout * 11);  // VDR : R1=10k , R2=1k --> for voltage sensor
        Battery.voltage = vIN;
        Serial.printf("\tBattery Voltage : %.2f \n", Battery.voltage);
        break;
      case 1:
        // (10000.0 + 1000.0) / 1000.0) = 11
        vIN = (Vout * 11);  // VDR : R1=10k , R2=1k --> for voltage sensor
        Solar.voltage = vIN;
        Serial.printf("\tSolar Voltage : %.2f \n", Solar.voltage);
        break;
      case 2:
        Battery.amps = scanACS712(Vout, 2.50);
        if (Battery.amps < 0.1) Battery.amps = 0.0;
        Serial.printf("\tBattery Amps : %.2f \n", Battery.amps);
        break;
      case 3:
        Solar.amps = scanACS712(Vout, 2.52);
        if (Solar.amps < 0.1) Solar.amps = 0.0;
        Serial.printf("\tSolar Amps : %.2f \n", Solar.amps);
        break;
      default:
        break;
    }
  }
  Serial.println();
}

void Init() {
  pinMode(SSR1, OUTPUT);
  digitalWrite(SSR1, LOW);
  pinMode(SSR2, OUTPUT);
  digitalWrite(SSR2, LOW);
  pinMode(SSR3, OUTPUT);
  digitalWrite(SSR3, LOW);
  pinMode(SSR4, OUTPUT);
  digitalWrite(SSR4, LOW);
  pinMode(2, OUTPUT);
  digitalWrite(2, LOW);

  pinMode(Button1, INPUT);
  pinMode(Button2, INPUT);
  pinMode(Button3, INPUT);
  pinMode(MODE_SW, INPUT);
  pinMode(SourceSW1, INPUT_PULLUP);
  pinMode(SourceSW2, INPUT_PULLUP);
  pinMode(SourceSW3, INPUT_PULLUP);
  // Invpzem.resetEnergy();
  // Gridpzem.resetEnergy();
}

void adsInit() {
  Wire.begin();
  if (!ads1.begin(0x48)) {
    Serial.println("ADS1115 # 1 not found at 0x48");
  } else {
    Serial.println("ADS1115 # 1 Initialized Successfully!");
  }

  if (!ads2.begin(0x49)) {
    Serial.println("ADS1115 # 2 not found at 0x49");
  } else {
    Serial.println("ADS1115 # 2 Initialized Successfully!");
  }
  ads1.setGain(GAIN_ONE);  // 1x gain (±4.096V)
  ads2.setGain(GAIN_ONE);  // 1x gain (±4.096V)
  // ads1.setDataRate(RATE_ADS1115_250SPS);
  // ads2.setDataRate(RATE_ADS1115_250SPS);

  lcd.init();
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(5, 0);
  lcd.print("Smart Grid ");
  lcd.setCursor(5, 1);
  lcd.print("Energy Flow");

  Serial.print("Invpzem Address: ");
  Serial.println(Invpzem.readAddress(), HEX);
  Serial.print("Gridpzem Address: ");
  Serial.println(Gridpzem.readAddress(), HEX);
}

void printLocalTime() {
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    Serial.println("Failed to obtain time");
    return;
  }
  dayIndex = timeinfo.tm_hour;
  Serial.println(&timeinfo, "%A, %B %d %Y %H:%M:%S");
}

void initWiFi() {
  WiFi.setAutoReconnect(true);
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi");

  unsigned long pmillis = millis();
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
    if (millis() - pmillis > 10000) {
      ESP.restart();
    }
  }

  Serial.print("\nConnected to SSID: ");
  Serial.println(WiFi.SSID());
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  digitalWrite(2, HIGH);
}

void initFirebase() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = DATABASE_URL + ".json?auth=" + API_KEY;
    http.begin(client, url);
    int code = http.GET();
    Serial.println(code == 200 ? "\u2705 Firebase Connected!" : "\u274C Firebase Not Connected!");
    http.end();
  }
}

void scanPzem() {
  // Read the data from the sensor
  Grid.voltage = Gridpzem.voltage();

  // Check if the data is valid
  if (isnan(Grid.voltage)) {
    Grid.voltage = Grid.Tamps = Grid.Tpower = Grid.freq = Grid.pf = 0.0;
    // Serial.println("Grid Pzem error!");
  } else {
    Grid.Tamps = Gridpzem.current();
    Grid.Tpower = Gridpzem.power();
    Grid.units = Gridpzem.energy() * 10;  // to accelerate the units
    Grid.freq = Gridpzem.frequency();
    Grid.pf = Gridpzem.pf();

    // Serial.println("\t ********  Grid Pzem Paramters  ********");
    // Serial.printf("Voltage: %.2f V\n", Grid.voltage);
    // Serial.printf("Current: %.2f A\n", Grid.Tamps);
    // Serial.printf("Power: %.2f W\n", Grid.Tpower);
    // Serial.printf("Energy: %.2f kWh\n", Grid.units);
    // Serial.printf("Frequency: %.1f Hz\n", Grid.freq);
    // Serial.printf("PF: %.2f\n", Grid.pf);
  }

  // Read the data from the sensor
  InvOut.voltage = Invpzem.voltage();

  // Check if the data is valid
  if (isnan(InvOut.voltage)) {
    InvOut.voltage = InvOut.Tamps = InvOut.Tpower = InvOut.freq = InvOut.pf = 0.0;
    LoadA.voltage = LoadB.voltage = LoadC.voltage = 0.0;
    // Serial.println("Grid Pzem error!");
  } else {
    LoadA.voltage = InvOut.voltage;
    LoadB.voltage = InvOut.voltage;
    LoadC.voltage = InvOut.voltage;
    InvOut.Tamps = Invpzem.current();
    InvOut.Tpower = Invpzem.power();
    InvOut.units = Invpzem.energy();
    InvOut.freq = Invpzem.frequency();
    InvOut.pf = Invpzem.pf();

    // Serial.println("\t ********  Inverter Pzem Paramters  ********");
    // Serial.printf("Voltage: %.2f V\n", InvOut.voltage);
    // Serial.printf("Current: %.2f A\n", InvOut.Tamps);
    // Serial.printf("Power: %.2f W\n", InvOut.Tpower);
    // Serial.printf("Energy: %.2f kWh\n", InvOut.units);
    // Serial.printf("Frequency: %.1f Hz\n", InvOut.freq);
    // Serial.printf("PF: %.2f\n", InvOut.pf);
  }
}
