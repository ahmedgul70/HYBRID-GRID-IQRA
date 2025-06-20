
	COMPONENTS
***************************************************
1.  ADS1115 16-bit
	-> SOLAR DC VOLTAGE
	-> BATTERY DC VOLTAGE
	-> SOLAR DC AMPS
	-> BATTERY DC AMPS
	-> LOAD # 1 CT
	-> LOAD # 2 CT
	-> LOAD # 3 CT
2. DS3231 RTC
3. PZEM-004Tv2
4. LCD 20X4
5. ESP32
6. LOAD SWs (3)
7. LOAD SSRs (3)
8. AUTO/MANUAL SW

***************************************************

INVERTER 12V TO 220V AC
BRIDGE RECTIFIER
12V 10A POWER SUPPLY
MPPT CHARGE CONTROLLER
12V 12Ah BATTERY
SOLAR PLATE 150W
3 LOADS + 3 LOAD SWs + 3 SOURCE SWs + 1 A/M SW

***************************************************



DESCRIPTION: 
The IoT-based grid monitoring and energy management system integrates solar power, battery storage, and the utility grid to provide efficient energy utilization, 
storage, and real-time monitoring with remote access capabilities. Solar panels capture sunlight to generate DC electricity, which is converted into AC using a solar inverter 
to power household appliances. Surplus energy is stored in a battery for later use, ensuring uninterrupted power supply. When solar power is insufficient, the system prioritizes
 energy sources using an 8-channel relay module. The relay logic enables dynamic switching based on predefined priorities: solar power is used first (high priority), 
battery power is utilized next (normal priority), and the utility grid is accessed only as a last resort (low priority). For example, if a high-priority bulb needs to be powered 
and solar energy is unavailable, the system will switch to the battery, and if that is also insufficient, it will rely on the grid. To enable real-time monitoring and control, voltage 
and current sensors connected to an Arduino microcontroller measure key parameters, which are then processed and transmitted via a GSM module to a cloud server (Firebase).
 This data can be accessed through a mobile application, providing users with insights into energy usage and system performance. The control panel acts as the central unit, 
managing energy flow between the solar panel, battery, grid, and appliances, ensuring seamless energy management. This IoT-enabled system enhances energy efficiency, 
offers remote accessibility, and provides a sustainable and reliable solution for managing power requirements.

Grid, solar aur battery se jo energy arahi hai wo voltage aur current sensor se Arduino mei calculation hogi uski ke ki power consumption horahi hai aur voltages calculate honge 
aur konsi appliances kitni power use kar rahi hai wo sab calculation kar ke gsm model ke Zaria firebase pe send kardega uske baad application ke Zaria display hoga.
Ye sab real time monitor kar raha hoga.


Features Confirmation for application 

✔️ Biometric/PIN authentication
✔️ Dashboard (real-time voltage, current, power consumption)
✔️ Live Graphs for consumption trends
✔️ Bill generation (unit price input + calculation)
✔️ Bill history (stored bills + past usage trends)



Work to be done:
Graph monitoring (only units) for 1 month
Auto/manual button 
3 source sw
3 load sw 

--Solar page
Solar volt
Solar power
Solar state

--Grid page
Grid state
Grid voltage
Grid power
Grid units

--Battery page
Battery volt
Battery state
Battery power

--Load page
Load 1 sw
Load 2 sw
Load 3 sw
Load priority part (software)


Dashboard 
Graph 
3 loads power,volt,current,total units
No Fingerprint authentication -- only SignUp&Login Authentication

Generate bill pdf (7,15,30 days)
Unit price (set) - slab system
Peak hours (set)
Peak hour unit price (set)

Total 150,000/- including thesis. Without any presentation 
(Without Proteus simulation)
(Without any stored bills + past usage trends )




