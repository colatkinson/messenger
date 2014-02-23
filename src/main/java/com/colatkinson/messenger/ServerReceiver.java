package com.colatkinson.messenger;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.Arrays;

class ServerReceiver extends Receiver {
    String[] to;
    
    //@Override
    public ServerReceiver(String[] rec, String[] out) {
        super(out);
        to = rec;
    }
    
    @Override
    public void run() {
        ServerReceiver rec = new ServerReceiver(to, pass);
        try {
            rec.server();
        } catch(Exception e) {
            System.out.println(e);
            System.out.println("Something is borken with the server");
        }
    }
    
    /*public void kill() {
        cont = false;
    }*/
    
    @Override
    public void server() throws Exception {
        System.out.println("Running server");
        DatagramSocket datagramSocket = new DatagramSocket(8000);
        //DatagramSocket datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        //datagramSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8000));

        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while(cont) {
            datagramSocket.receive(packet);
            //System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
            String str = new String(packet.getData(), packet.getOffset(), packet.getLength());
            if(out_type == "file") {
                try {
                    //System.out.println(":"+tmp.getAbsolutePath());
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fn, true)));
                    out.println(str);
                    out.close();
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                    System.out.println("file is borken");
                }
            } else {
                System.out.println(str);
            }
            
            //System.out.println(Arrays.toString(to));
            
            Message fwd = new Message(str, to);
            fwd.send();
        }
        datagramSocket.close();
    }
}
