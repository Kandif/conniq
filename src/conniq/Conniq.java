package conniq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java Net libary.
 *
 * @author Kandif
 * @version 0.4.1
 */
public class Conniq {

    private ServerSocket server;
    private ArrayList<Socket> clients = new ArrayList<>();
    private ArrayList<BufferedReader> tabin = new ArrayList<>();
    private ArrayList<PrintWriter> tabout = new ArrayList<>();
    private ArrayList<ObjectInputStream> obtabin = new ArrayList<>();
    private ArrayList<ObjectOutputStream> obtabout = new ArrayList<>();
    private DatagramSocket ds;
    private static byte buff[] = new byte[1024];
    private int TCPPORT = 0, UDPPORT = 0;
    private ArrayList<String> addresses = new ArrayList<>();
    private String logs;
    private boolean isEnabletojoin = true;
    private boolean readytosend = false;
    private boolean addwithconnect = false;
    private boolean isHooster = false;
    private int computerID = 0;

    public void createServer(int port, int port2) {
        try {
            if (getTCPPORT() != 0) {
                server = new ServerSocket(getTCPPORT());
                ds = new DatagramSocket(getUDPPORT());
            } else {
                server = new ServerSocket(port);
                ds = new DatagramSocket(port2);
                UDPPORT = port2;
                TCPPORT = port;
            }
        } catch (IOException ex) {
            logs = "Create host Error: " + ex+"\n";
        }
    }

    public void createServer(int port) {
        try {
            if (getTCPPORT() != 0) {
                server = new ServerSocket(getTCPPORT());
            } else {
                server = new ServerSocket(port);;
                TCPPORT = port;
            }
        } catch (IOException ex) {
            logs = "Create host Error: " + ex+"\n";
        }
    }

    public void disconnect() {
        try {
            if (server != null) {
                server.close();
            }
            for (Socket client : clients) {
                if (client != null) {
                    client.close();
                }
            }
            for (BufferedReader in : tabin) {
                if (in != null) {
                    in.close();
                }
            }
            for (PrintWriter out : tabout) {
                if (out != null) {
                    out.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Conniq.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isConnect(int id) {
        if (clients.get(id) != null) {
            return clients.get(id).isConnected();
        }
        return false;
    }

    public void enableJoin(boolean active) {
        isEnabletojoin = active;
    }

    public void setBind(int TCP, int UDP) {
        TCPPORT = TCP;
        UDPPORT = UDP;
    }

    public void waitClient() {
        if (isEnabletojoin) {
            try {
                clients.add(server.accept());
                isHooster=true;
                tabout.add(new PrintWriter(clients.get(clients.size() - 1).getOutputStream()));
                obtabout.add(new ObjectOutputStream(clients.get(clients.size() - 1).getOutputStream()));
                tabin.add(new BufferedReader(new InputStreamReader(clients.get(clients.size() - 1).getInputStream())));
                obtabin.add(new ObjectInputStream(clients.get(clients.size() - 1).getInputStream()));
                readytosend = true;
                addresses.add(tabin.get(clients.size() - 1).readLine());
                sendTCP("#GCId"+(clients.size()), clients.size()-1);
                if(clients.size()>1){
                    for(int i=0;i<clients.size()-1;i++){
                         sendTCP("#GA"+addresses.get(i), clients.size()-1);
                         sendTCP("#GA"+addresses.get(clients.size()-1), i);
                    }
                }
            } catch (IOException ex) {
                logs = "Joining client Error: " + ex+"\n";
            }
        }
    }

    public void joinServer(String address, int port, int port2) {

        try {
            if (getTCPPORT() != 0) {
                clients.add(new Socket(address, getTCPPORT()));
                ds = new DatagramSocket(getUDPPORT());
            } else {
                clients.add(new Socket(address, port));
                ds = new DatagramSocket(port2);
                UDPPORT = port2;
                TCPPORT = port;
            }
            tabout.add(new PrintWriter(clients.get(clients.size() - 1).getOutputStream()));
            obtabout.add(new ObjectOutputStream(clients.get(clients.size() - 1).getOutputStream()));
            tabin.add(new BufferedReader(new InputStreamReader(clients.get(clients.size() - 1).getInputStream())));
            obtabin.add(new ObjectInputStream(clients.get(clients.size() - 1).getInputStream()));
            readytosend = true;
            tabout.get(clients.size() - 1).println(getLocalIP());
            tabout.get(clients.size() - 1).flush();
            addresses.add(address);
            afterjoin();
        } catch (IOException ex) {
            logs = "Joining to host Error: " + ex+"\n";
        }
    }

    public void joinServer(String address, int port) {

        try {
            if (getTCPPORT() != 0) {
                clients.add(new Socket(address, getTCPPORT()));
            } else {
                clients.add(new Socket(address, port));
                TCPPORT = port;
            }
            tabout.add(new PrintWriter(clients.get(clients.size() - 1).getOutputStream()));
            obtabout.add(new ObjectOutputStream(clients.get(clients.size() - 1).getOutputStream()));
            tabin.add(new BufferedReader(new InputStreamReader(clients.get(clients.size() - 1).getInputStream())));
            obtabin.add(new ObjectInputStream(clients.get(clients.size() - 1).getInputStream()));
            readytosend = true;
            tabout.get(clients.size() - 1).println(getLocalIP());
            tabout.get(clients.size() - 1).flush();
            addresses.add(address);
            afterjoin();
        } catch (IOException ex) {
            logs = "Joining to host Error: " + ex+"\n";
        }
    }
    
    public void afterjoin(){
        computerID = Integer.parseInt(getTCP(0));
                if(computerID>1){
                    for(int i=0;i<clients.size()-1;i++){
                         addresses.add(getTCP(0));
                    }
                }
    }

    public void sendTCP(String v, int id) {
        if(isHooster){
            tabout.get(id).println(v);
            tabout.get(id).flush();
        }else{
            if(id>0){
                if(computerID>9)
                    tabout.get(0).println("#Mt"+id+v);
                else
                    tabout.get(0).println("#mt"+id+v);
            }
            else tabout.get(0).println(v);
            
            tabout.get(0).flush();
        }
    }
    
    public void sendObjTCP(Object v, int id) {
        try {
            if(isHooster){
                    ObjectStream os = new ObjectStream(v);
                    obtabout.get(id).writeObject(os);
                    obtabout.get(id).flush();
                    obtabout.get(id).reset();
            }
            else{
                ObjectStream os = new ObjectStream(v,id);
                obtabout.get(0).writeObject(os);
                obtabout.get(0).flush();
                obtabout.get(0).reset();
            }
        } catch (IOException ex) {
               logs = "Send TCP object stream error:"+ex+"\n";
        }
    }
    
     public void HsendObjTCP(Object v, int wid) {
        try {
            if(isHooster){
                    for(int i=0;i<clients.size();i++){
                        if(i==wid) continue;
                        obtabout.get(i).writeObject(new ObjectStream(v));
                        obtabout.get(i).flush();
                        obtabout.get(i).reset();
                    };
            }
        } catch (IOException ex) {
               logs = "Send TCP object stream error:"+ex+"\n";
        }
    }
    
    public void sendObjTCP(Object v) {
        try {
            if(isHooster){
                    for(int i=0;i<clients.size();i++){
                        obtabout.get(i).writeObject(new ObjectStream(v));
                        obtabout.get(i).flush();
                        obtabout.get(i).reset();
                    }
                    //obtabout.get(i).writeObject(v);
            }
            else{
                obtabout.get(0).writeObject(new ObjectStream(v,-1));
                obtabout.get(0).flush();
                obtabout.get(0).reset();
            }
        } catch (IOException ex) {
               logs = "Send TCP object stream error:"+ex+"\n";
        }
    }

    public void sendTCP(String v) {
        if(isHooster){
            for (int id = 0; id < tabout.size(); id++) {
                tabout.get(id).println(v);
                tabout.get(id).flush();
            }
        }else{
            tabout.get(0).println("#AM"+v);
            tabout.get(0).flush();
        }
    }
    
    public void HsendTCP(String v,int wint) {
            for (int id = 0; id < tabout.size(); id++) {
                if(id!=wint){
                    tabout.get(id).println(v);
                    tabout.get(id).flush();
                }
            }
    }

//    public void sendTCPs(String s){
//        out.println(s);
//        out.flush(); 
//    }
    public void sendUDP(String s, int id) {
        for (int i = 0; i < s.length(); i++) {
            buff[i] = (byte) s.charAt(i);
        }
        try {
            ds.send(new DatagramPacket(buff, s.length(), InetAddress.getByName(addresses.get(id)), getUDPPORT()));
        } catch (UnknownHostException ex) {
            logs = "Send UDP package error: " + ex+"\n";
        } catch (IOException ex) {
            logs = "Send UDP package error: " + ex+"\n";
        }
    }

    public void sendUDP(String s) {
        for (int i = 0; i < s.length(); i++) {
            buff[i] = (byte) s.charAt(i);
        }
        try {
            for (int id = 0; id < addresses.size(); id++) {
                ds.send(new DatagramPacket(buff, s.length(), InetAddress.getByName(addresses.get(id)), getUDPPORT()));
            }
        } catch (UnknownHostException ex) {
            logs = "Send UDP package error: " + ex+"\n";
        } catch (IOException ex) {
            logs = "Send UDP package error: " + ex+"\n";
        }

    }
    
    public Object getObjTCP(int id){
        try {
            if(isHooster){
                Object ob = obtabin.get(id).readObject();
                ObjectStream stream = (ObjectStream)ob;
                if(stream.getid()==-1){
                        HsendObjTCP(ob,id);
                        return stream.getOb();
                }
                else if(stream.getid()==0){
                    return stream.getOb();
                }
                else{
                    sendObjTCP(ob,id);
                }
                return getObjTCP(id);
            }
            else{
                Object ob = obtabin.get(id).readObject();
                ObjectStream stream = (ObjectStream)ob;
                return stream.getOb();
                //return ob;
            }
        } catch (IOException ex) {
            logs = "Get TCP object stream Error: " + ex+"\n";
        } catch (ClassNotFoundException ex) {
            logs = "Get TCP object stream Error: " + ex+"\n";
        }
        return null;
    }
    
    public String getTCP(int id) {
        try {
            String msg = tabin.get(id).readLine();
            if(isHooster){
                if(msg.startsWith("#AM")){
                     msg = msg.substring(3);
                     HsendTCP(msg,id);
                     return msg;
                }
                if(msg.startsWith("#mt")){
                    int idtm=Integer.parseInt(msg.substring(3, 4));
                    msg = msg.substring(4);
                    if(idtm==0) return msg;
                    sendTCP(msg,idtm-1);
                }else if(msg.startsWith("#Mt")){
                    int idtm=Integer.parseInt(msg.substring(3, 5));
                    msg = msg.substring(5);
                    sendTCP(msg,idtm-1);
                }
            }else{
                if(msg.startsWith("#GCId")){
                    msg = msg.substring(5);
                    return msg;
                }
                if(msg.startsWith("#GA")){
                    msg = msg.substring(3);
                    addresses.add(msg);
                    return getTCP(0);
                }
            }
            return msg;
        } catch (IOException ex) {
            logs = "Get TCP stream Error: " + ex+"\n";
            return null;
        }
    }

    public String getUDP() {
        try {
            DatagramPacket pack = new DatagramPacket(buff, buff.length);
            ds.receive(pack);
            String s = new String(pack.getData(), 0, pack.getLength());
            //buff = new byte[1024];
            return s.trim();
        } catch (IOException ex) {
            logs = "Get UDP steam Error: " + ex+"\n";
            return null;
        } catch (NullPointerException e) {
            logs = "Get UDP steam Error: " + e+"\n";
            return null;
        }

    }

    public String getLogs() {
        return logs;
    }

    public void getLogsAddType(boolean type) {
        addwithconnect = type;
    }

    public void showLogs() {
        System.out.println("Logs: " + logs);
    }

    private void addLogs(String s) {
        if (addwithconnect) {
            logs = logs + "\n" + s;
        } else {
            logs = s;
        }
    }

    public static String getLocalIP() {
        try {
            return LocatorIP.getLocalIp();
        } catch (SocketException e) {
            return "Check Error";
        } catch (UnknownHostException ex) {
            return "Check Error";
        }

    }

    public static String getPublicIP() {
        try {
            return LocatorIP.getPublicIp();
        } catch (Exception e) {
            return "Check Error";
        }
    }

    /**
     * @return the TCPPORT
     */
    public int getTCPPORT() {
        return TCPPORT;
    }

    /**
     * @return the UDPPORT
     */
    public int getUDPPORT() {
        return UDPPORT;
    }
    
    public int getCpId(){
        return computerID;
    }
      
}

class ObjectStream implements Serializable{
        
        private int id=0;
        private Object ob;
       
        
        ObjectStream(Object o,int i){
            this.id=i;
            ob=o;     
        }
        
        ObjectStream(Object o){
            ob=o;
        }
        
        Object getOb(){
            return this.ob;
        }
        
        void getOb(Object ob){
            this.ob=ob;
        }
        
        int getid(){
            return id;
        }
        
    }
