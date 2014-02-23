package com.colatkinson.messenger;

import java.util.Arrays;
/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception{
        String[] a = {};
        if(args.length > 0) {
            if(args[0].equals("--window")) {
                MessageWindow r = new MessageWindow();
                r.main(a);
            } else if(args[0].equals("--text")) {
                TextOnly r = new TextOnly();
                r.main(a);
            } else {
                System.out.println("Select either --window or --text");
            }
        } else {
            MessageWindow r = new MessageWindow();
            r.main(a);
        }
    }
}
