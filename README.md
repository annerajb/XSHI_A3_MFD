XSHI_A3_MFD
===========

This addon and Extension allow Arma 3 To interface with XSHI

# MFD 
The MFD folder can be packed with PBO Bin Tools and dropped in @MFD

The MFD_Extension compiles with VS2019 and requires
https://sourceforge.net/projects/boost/files/boost-binaries/1.71.0/boost_1_71_0-msvc-14.2-64.exe/download
 to be placed under MFD_Extension/../../boost_1_71_0 
 (or update vc directories variables of the project and the linker include directory)

it has a Post Build to copy the dll to your Arma 3/@MFD/ Folder

You need to install MSVC v142 vs 2019 c++ x64/x86 build tools.


# UDPxhsiTest
test application to generate fake udp data to java application
# XSHI 

This requires openjdk 13 and netbeans 11.x for building