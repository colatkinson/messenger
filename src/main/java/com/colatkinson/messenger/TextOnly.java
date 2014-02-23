//TODO: Add server support
package com.colatkinson.messenger;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TextOnly {
    private static Scanner scanner = new Scanner( System.in );
    private static String ip;
    private static String[] to;
    private static String name;
    
    public static void main(String args[]) throws Exception {
        Socket s = new Socket("192.168.1.1", 80);
        ip = s.getLocalAddress().getHostAddress();
        s.close();
        
        System.out.println("Hello, and welcome to this chat program");
        System.out.println("Your IP address: " + ip);
        System.out.print("Username: ");
        name = scanner.nextLine();
        System.out.print("Recipient(s): ");
        to = scanner.nextLine().split(" ");
        
        String[] pass = {"stdio"};
        Runnable Receiver = new Receiver(pass);
        Thread worker = new Thread(Receiver);
        worker.start();
        System.out.println("Started receiving messages");
        
        DatagramSocket datagramSocket = new DatagramSocket();

        //byte[] buffer = "0123456789".getBytes();
        //InetAddress receiverAddress = InetAddress.getLocalHost();
        //InetAddress receiverAddress = InetAddress.getByName(to);
        
        while(true) {
            String inp = scanner.nextLine();
            String send = name+": "+inp;
            /*byte[] buffer = send.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 8000);
            datagramSocket.send(packet);*/
            Message msg = new Message(send, to);
            msg.send();
        }
    }
}
