// MFD_Extension.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include <iostream>
#include <boost/thread.hpp>  
#include <boost/array.hpp>
#include <boost/asio.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/archive/text_oarchive.hpp>
#include <boost/lexical_cast.hpp>
#include <boost/property_tree/ptree.hpp>
#include <boost/property_tree/xml_parser.hpp>
#include "endinaness.h"
#include "ids.h"
#define PLUGIN_VERSION_NUMBER 20005
using boost::asio::ip::udp;
extern "C"
{
	__declspec (dllexport) void __stdcall RVExtension(char *output, int outputSize, const char *function);
}

struct SimDataPoint {
	int	id;
	float	value;
	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
    {
        ar & id;
        ar & value;
    }
};

struct SimDataPacket {
	char				packet_id[4];
	int				nb_of_sim_data_points;
	struct SimDataPoint sim_data_points[150];

	template<class Archive>
    void serialize(Archive & ar, const unsigned int version)
    {
        ar & packet_id;
        ar & nb_of_sim_data_points;
        ar & sim_data_points;
    }
};
std::vector<SimDataPoint> data_points;
boost::mutex mtx_;
int createADCPacket(SimDataPacket& packet)
{
	int i = 0;
	strncpy(packet.packet_id, "ADCD", 4);

	packet.sim_data_points[i].id = custom_htoni(PLUGIN_VERSION_ID);
	packet.sim_data_points[i].value = custom_htonf((float) PLUGIN_VERSION_NUMBER);
	i++;
	mtx_.lock();
	for(size_t j = 0;j<data_points.size();j++)
	{
		packet.sim_data_points[i].id = custom_htoni(data_points[j].id);
		
		packet.sim_data_points[i].value = custom_htonf(data_points[j].value);
		i++;
	}
	mtx_.unlock();
	packet.nb_of_sim_data_points = custom_htoni( i) ;
	int packet_size = 8 + i * 8;
	return packet_size;
}
void workerFunc()  
{  
	using boost::property_tree::ptree;
    ptree pt;

    // Load the XML file into the property tree. If reading fails
    // (cannot open file, parse error), an exception is thrown.
    read_xml("config.xml", pt);

    // Get the filename and store it in the m_file variable.
    // Note that we construct the path to the value by separating
    // the individual keys with dots. If dots appear in the keys,
    // a path type with a different separator can be used.
    // If the debug.filename key is not found, an exception is thrown.
    std::string ip = pt.get<std::string>("config.ip","127.0.0.1");
	std::string port = pt.get<std::string>("config.port","49020");
	do 
	{
		try
		{
			SimDataPacket packet;
			boost::asio::io_service io_service;

			udp::resolver resolver(io_service);
			udp::resolver::query query(udp::v4(),ip, port);
			udp::endpoint receiver_endpoint = *resolver.resolve(query);

			udp::socket socket(io_service);
			socket.open(udp::v4());
		
			std::ostringstream archive_stream;
			boost::archive::text_oarchive archive(archive_stream);
			do
			{
				int packet_size = createADCPacket(packet);
				archive << packet;
				//boost::array<char, 128> recv_buf;
				//udp::endpoint sender_endpoint;
				//size_t len = socket.receive_from(boost::asio::buffer(recv_buf), sender_endpoint);
					
				socket.send_to(boost::asio::buffer((char *)&packet,packet_size), receiver_endpoint);
					
			}while(1);
		}
		catch (std::exception& e)
		{
			std::cerr << e.what() << std::endl;
		}
	}while(1);
}  
void __stdcall RVExtension(char *output, int outputSize, const char *function)
{
	static bool started = false;
	typedef std::vector< std::string > split_vector_type;
	split_vector_type dataVec; 
	split_vector_type key_val; 
	if(!strcmp(function,"version"))
	{
		strncpy_s(output, outputSize, "1.0", _TRUNCATE);
	}else
	{
		SimDataPoint pack;
		//pack|2=360/
		if(std::string(function,0,4) == "pack")
		{
			std::string instring = function;
			boost::replace_first(instring, "pack|", "");
			//add mutex
			mtx_.lock();
			data_points.clear();
			//key=val/key=val/key=val;
			mtx_.unlock();
			boost::split( dataVec, instring, boost::is_any_of("/") ); 
			for(size_t i =0; i < dataVec.size();++i)
			{
				dataVec[i];
				//key=val
				boost::split( key_val, dataVec[i], boost::is_any_of("=") ); 
				pack.id = boost::lexical_cast<int>(key_val[0]);
				try
				{
					pack.value = boost::lexical_cast<float>(key_val[1]);
				}catch(boost::bad_lexical_cast&)
				{
					pack.value = (float)boost::lexical_cast<int>(key_val[1]);
				}
				mtx_.lock();
				data_points.push_back(pack);
				mtx_.unlock();
			}
			if(!started)
			{
				boost::thread workerThread(workerFunc);
				started = true;
			}
		
		}
	}

}
