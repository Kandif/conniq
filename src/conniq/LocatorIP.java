package conniq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;



/**
 * Class wchich is include methods to get ip addresses
 * @author kandif
 */
public class LocatorIP {
    /**
     * Ir returns your public IP.
     * @return
     * @throws Exception 
     */
    public static String getPublicIp() throws Exception {
        URL whatismyip = new URL("http://checkip.dyndns.com/");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip.substring(76, ip.indexOf("</body>"));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * It returns your local ip like. 192.168...
     * @return
     * @throws SocketException 
     */
    public static String getLocalIp() throws SocketException, UnknownHostException{
       Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces();
       NetworkInterface element = net.nextElement();
        Enumeration<InetAddress> addresses = element.getInetAddresses();
        while (addresses.hasMoreElements()){
            InetAddress ip = addresses.nextElement();
            if (ip instanceof Inet4Address){
                if (ip.isSiteLocalAddress()){
                    return ip.getHostAddress();
                }

            }
            
        }		
		
        return InetAddress.getLocalHost().getHostAddress();
    }
}
