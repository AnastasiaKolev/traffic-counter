import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.pcap4j.util.Packets;


import java.io.IOException;
import java.io.EOFException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class MainClass {

    private static int totalPacketLength;
    private static int maxPacketLength = 1073741824;    // todo: get from hive
    private static int minPacketLength = 1024;

    private static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void main(String[] args) throws UnknownHostException, PcapNativeException, EOFException, TimeoutException, NotOpenException {
        // The code we had before
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println( "You chose: " + device );

        // New code below here
        if (device == null) {
            System.out.println( "No device chosen." );
            System.exit( 1 );
        }

        // Open the device and get a handle
        int snapshotLength = 65536; // in bytes
        int readTimeout = 500; // in milliseconds
        final PcapHandle handle;
        handle = device.openLive( snapshotLength, PromiscuousMode.PROMISCUOUS, readTimeout );

        // текущее время для захвата трафика за 5 минут
        final long[] startTime = {System.currentTimeMillis()};

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(PcapPacket pcapPacket) {
                int len = pcapPacket.length();
                totalPacketLength += len;

                long time = System.currentTimeMillis();
                // захват трафика за 5 минут
                if (time > startTime[0] + 5 * 60 * 1000) {
                    if (totalPacketLength < minPacketLength) {
                        // send alert to Kafka
                        // sendAlert();

                        System.out.println( "Alert to Kafka: total.length() is less than min value " + totalPacketLength );
                    }
                    if (totalPacketLength > maxPacketLength) {
                        // send alert to Kafka
                        // sendAlert();

                        System.out.println( "Alert to Kafka: total.length() is higher than max value " + totalPacketLength );
                    }
                    // обнуление счетчиков
                    System.out.println( "5 minutes have passed \tTraffic is within the bounds: " + totalPacketLength );
                    totalPacketLength = 0;
                    startTime[0] = System.currentTimeMillis();
                }

                // обновление пороговых значений каждые 20 минут
                if (time > startTime[0] + 20 * 60 * 1000) {
                    maxPacketLength += 500000;
                    minPacketLength += 10000;
                }

                System.out.println( "Current traffic: " + totalPacketLength );

//                System.out.println( "1.pcapPacket.length() "+ len );
//                System.out.println( "2.handle.getTimestampPrecision() " + handle.getTimestampPrecision() );
//                System.out.println( "3.pcapPacket "  + pcapPacket );
            }
        };

        try {
            int maxPackets = 0; // 0 = INFINITE loop
            handle.loop( maxPackets, listener );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cleanup when complete
        handle.close();

        System.out.println( "Total Packet Length " + totalPacketLength
                + " per " + readTimeout + " milliseconds" );
    }
}