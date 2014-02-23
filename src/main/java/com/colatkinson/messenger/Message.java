package com.colatkinson.messenger;

import java.net.*;

class Message {
    String text;
    String[] to;
    
    public Message(String send, String[] rec) {
        text = send;
        to = rec;
    }
    
    public boolean send() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            byte[] buffer = text.getBytes();
            
            for(int i = 0; i<to.length; i++) {
                InetAddress receiverAddress = InetAddress.getByName(to[i]);
            
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 8000);
                datagramSocket.send(packet);
            }
        
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
