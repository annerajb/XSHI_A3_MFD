class CfgPatches 		
{
	class MFD	{
		units[] = {};
		weapons[] = {};
		requiredVersion = 0.1;
		requiredAddons[] = {"Extended_EventHandlers",CBA_MAIN};
		author = "annerajb";
        mail = "annerjb@gmail.com";
	};
};
class Extended_GetIn_EventHandlers
{
    class Air
    {
        class Air_GetIn
        {
			getin = "[] execVM '\MFD\getin.sqf'";

        };
    };
	class Plane
    {
        class Plane_GetIn
        {
			getin = "[] execVM '\MFD\getin.sqf'";

        };
    };
	class Helicopter
    {
        class Helicopter_GetIn
        {
			getin = "[] execVM '\MFD\getin.sqf'";

        };
    };
};