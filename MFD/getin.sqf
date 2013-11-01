#include "\MFD\ids.hpp"
_vehRole = assignedVehicleRole player;
if((count _vehRole) != 0) then
{
	_role = (_vehRole select 0);
	if (_role == "Driver" || _role == "Turret" ) then
	{
		diag_log "Running GetinSQF";
		while {true} do 
		{
			_packet = "pack|";
			if ( isEngineOn (vehicle player)) then
			{
				
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_BATTERYON ,1];
				_deg = getDir vehicle player;
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_HEADING ,_deg];
				
				_daylight = currentVisionMode player ;
				_packet = format ["%1%2=%3/",_packet,VEHICLE_COCKPIT_LIGHTS_ON ,_daylight];
				_aslheight = getPosASL vehicle player;
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_ALT_ASL ,_aslheight select 2];
				_aglheight = getPosATL vehicle player;
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_ALT_AGL ,_aglheight select 2];
				_speed = speed vehicle player;
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_TAS ,_speed];
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_GROUNDSPEED ,_speed];
				
				_time = daytime;//passed as 16.5 for 16:30
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_TIME ,_time];
				
				//_gridPos = mapGridPosition player;//use this for LAT LONG
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_WIND_DIR ,windDir];
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_WIND_SPEED ,windStr];
				_fuel = fuel vehicle player;
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_FUEL ,_fuel];
				_vecDir = vectorDir vehicle player; 
				_pitch = asin (_vecDir select 2); 
				_packet = format ["%1%2=%3/",_packet,INSTRUMENT_PITCH ,_pitch];
				_vecUp = ( vehicle player )call BIS_fnc_getPitchBank; 
				_bank =  (_vecUp select 1); 
				_packet = format ["%1%2=%3",_packet, INSTRUMENT_BANK,_bank];
				_targetPosition = ((waypoints player) select 0);
				//_targetHeading = ;
				//_targetDist = ;
				
				//wishlist
				//stall
				//landing gear 
				//vertical speed
				//rpm
				
				/*
				damage components http://community.bistudio.com/wiki/getHitPointDamage
				http://forums.bistudio.com/showthread.php?113418-HandleDamage-EH-explained-(-poor-man-s-getHit)
				get avionics, engine */
				
				
				//"debug_console" callExtension _packet;
				"MFD_Extension" callExtension _packet;
				
			}else
			{
				_packet = format ["%1%2=%3",_packet,INSTRUMENT_BATTERYON ,0];
				
				
				//"debug_console" callExtension _packet;
				"MFD_Extension" callExtension _packet;
			};
			sleep 0.2;
		};
		
		
	};
};
