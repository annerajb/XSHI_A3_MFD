// UDPxhsiTest.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <windows.h>
#include <iomanip>
#include <iostream>
int _tmain(int argc, _TCHAR* argv[])
{
  /* get handle to dll */ 
   HINSTANCE hGetProcIDDLL = LoadLibrary("E:\\SteamLibrary\\SteamApps\\common\\Arma 3\\@MFD\\MFD_Extension.dll"); 

   /* get pointer to the function in the dll*/ 
   FARPROC lpfnGetProcessID = GetProcAddress(HMODULE (hGetProcIDDLL),"_RVExtension@12"); 
   if(lpfnGetProcessID == NULL)
   {
		DWORD lastError = GetLastError();
		std::cout << "General failure. GetLastError returned " << std::hex << lastError << ".";
   }
   /* 
      Define the Function in the DLL for reuse. This is just prototyping the dll's function. 
      A mock of it. Use "stdcall" for maximum compatibility. 
   */ 
   typedef void (__stdcall * pICFUNC)(char *, int,char*); 

   pICFUNC dllExtension; 
   dllExtension = pICFUNC(lpfnGetProcessID); 
   //-noPause -nosplash -mod=@CBA_A3;@JayArmA2Lib;@ACRE;@fhq_m4_a3;RH_pdw1.0;@fhq_accessories;@ac130x_a3;@MFD;@JS_JC_FA18 -showScriptErrors  "C:\Users\bonilann\Documents\Arma 3 - Other Profiles\annerajb\missions\mfdtest.Stratis\mission.sqm"
   /* The actual call to the function contained in the dll */ 
   char* output = new char[4096];
   memset(output,0,4096);
   dllExtension(output, 4096,"version"); 
   //output should be version#
   //
   dllExtension(output, 4096,"pack|81=0/80=0"); 
   Sleep(5000);
   while(1)
   {
		dllExtension(output, 4096,"pack|81=1/81=0/302=12.5000"); 
   }
   
   delete[] output;




   /* Release the Dll */ 
   FreeLibrary(hGetProcIDDLL); 
	return 0;
}

