#include "\MFD\ids.hpp"
_role = ((assignedVehicleRole player) select 0);
if (_role == "Driver" || _role == "Turret" ) then
{
	diag_log "Running GetinSQF";
	hint "Sending GETinData"; 
	while {true} do 
	{
		_packet = "pack|";
		if (isEngineOn vehicle player) then
		{
			
			_packet = format ["%1%2=%3/",_packet,SIM_COCKPIT_ELECTRICAL_BATTERY_ON ,1];
			_packet = format ["%1%2=%3/",_packet,SIM_COCKPIT_ELECTRICAL_AVIONICS_ON ,1];
			_deg = getDir vehicle player;
			_packet = format ["%1%2=%3/",_packet,SIM_FLIGHTMODEL_POSITION_MAGPSI ,_deg];
			
			_daylight = currentVisionMode player ;
			_packet = format ["%1%2=%3/",_packet,SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON ,_daylight];
			_aslheight = getPosASL vehicle player;
			_packet = format ["%1%2=%3/",_packet,SIM_FLIGHTMODEL_POSITION_ELEVATION ,_aslheight select 2];
			_aglheight = getPosATL vehicle player;
			_packet = format ["%1%2=%3/",_packet,SIM_FLIGHTMODEL_POSITION_Y_AGL ,_aglheight select 2];
			_speed = speed vehicle player;
			_packet = format ["%1%2=%3/",_packet,SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED ,_speed];
			_time = daytime;//passed as 16.5 for 16:30
			_packet = format ["%1%2=%3/",_packet,SIM_TIME_ZULU_TIME_SEC ,_time];
			
			//_gridPos = mapGridPosition player;//use this for LAT LONG
			_packet = format ["%1%2=%3/",_packet,SIM_WEATHER_WIND_DIRECTION_DEGT ,windDir];
			_packet = format ["%1%2=%3/",_packet,SIM_WEATHER_WIND_SPEED_KT ,windStr];
			_fuel = fuel vehicle player;
			if(_fuel < 0.2) then
			{
				_packet = format ["%1%2=%3/",_packet,SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY ,1];
			}else
			{
				_packet = format ["%1%2=%3/",_packet,SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY ,0];
			};
			_packet = format ["%1%2=%3/",_packet,SIM_COCKPIT2_FUEL_QUANTITY_+1 ,_fuel];
			_packet = format ["%1%2=%3/",_packet,SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS ,1];
			_packet = format ["%1%2=%3/",_packet,SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES ,1];
			_packet = format ["%1%2=%3/",_packet,SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_+1 ,1];//we only have one fuel tank
			_vecDir = vectorDir vehicle player; 
			_pitch = asin (_vecDir select 2); 
			_packet = format ["%1%2=%3/",_packet,SIM_FLIGHTMODEL_POSITION_THETA ,_pitch];
			_vecUp = vectorDir vehicle player; 
			_bank =  asin (_vecUp select 2); 
			_packet = format ["%1%2=%3",_packet, SIM_FLIGHTMODEL_POSITION_PHI,_bank];
			//wishlist
			//stall
			//damage components (avionics?)
			//landing gear 
			//elevation?
			//vertical speed
			//targetDist
			//rpm
			
			//"debug_console" callExtension _packet;
			"MFD_Extension" callExtension _packet;
			
		}else
		{
			_packet = format ["%1%2=%3/",_packet,SIM_COCKPIT_ELECTRICAL_BATTERY_ON ,0];
			_packet = format ["%1%2=%3",_packet,SIM_COCKPIT_ELECTRICAL_AVIONICS_ON ,0];
			
			
			//"debug_console" callExtension _packet;
			"MFD_Extension" callExtension _packet;
		};
		sleep 0.3;
	};
	
	
};

