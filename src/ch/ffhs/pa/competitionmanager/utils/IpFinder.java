package ch.ffhs.pa.competitionmanager.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Utility class to find the ip of the computer that is executing the code.
 */
public class IpFinder {

    /**
     * Get the IPv4 of the computer in its local network.
     * @return A string with the IPv4.
     */
    public static String getLocalIpV4() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 14092);
            String ip = socket.getLocalAddress().getHostAddress();
            return ip;
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return "IP could not be determined.";
    }
}
