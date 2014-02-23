package com.colatkinson.messenger;

import java.io.*;
import java.net.*;
import java.lang.*;

public class Receiver implements Runnable {
    String out_type;
    //File tmp;
    String fn;
    String[] pass;
    volatile boolean cont = true;
    DatagramSocket datagramSocket;
    
    public Receiver(String[] out) {
        out_type = out[0];
        if(out.length > 1) {
            fn = out[1];
        }
        pass = out;
        /*try {
            tmp = File.createTempFile("chat", ".tmp");
        } catch(Exception e) {
            System.out.println("Error creating temp file");
        }*/
    }
    
    @Override
    public void run() {
        Receiver Receiver = new Receiver(pass);
        try {
            Receiver.server();
        } catch(Exception e) {
            System.out.println(e);
            System.out.println("Something is borken with the server");
        }
    }
    
    public synchronized void kill() {
        cont = false;
        //Message killmsg = new Message("RIP in piece ;_;", new String[]{"127.0.0.1"});
        //killmsg.send();
        byte[] x = new byte[1000];
        try{
            DatagramPacket p = new DatagramPacket(x,x.length,InetAddress.getLocalHost(),8737);
            this.datagramSocket.send(p);
        } catch(Exception e) {
            System.out.println("Boogity");
        }
    }
    
    public void server() throws Exception {
        datagramSocket = new DatagramSocket(8000);
        //DatagramSocket datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        //datagramSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8000));

        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while(cont) {
            datagramSocket.receive(packet);
            System.out.println(cont);
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
        }
        datagramSocket.close();
    }
}
