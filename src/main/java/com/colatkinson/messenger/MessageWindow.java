package com.colatkinson.messenger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.Timer;

public class MessageWindow {
    public JFrame window = new JFrame();
    private static Scanner scanner = new Scanner( System.in );
    private static String ip;
    private static String[] to;
    private static String name;
    JTextField field = new JTextField();
    InetAddress receiverAddress;
    DatagramSocket datagramSocket;
    JTextArea area;
    static JDialog d = new JDialog();
    static File tmp;
    static Receiver rec;
    static boolean server;
    JCheckBoxMenuItem serverButton;
    static Thread worker;
    
    private class UpdateText extends TimerTask {
        public void run() {
            //area.setText(readFile("myfile.txt"));
            //System.out.println(tmp.getAbsolutePath());
            try {
                area.setText(readFile(tmp.getAbsolutePath()));
            } catch(Exception e) {
                System.out.println("I dunno, something broke?");
            }
        }
    }
    
    private static boolean getInfoDialog() {
        /*d.setLayout(new BoxLayout(d.getContentPane(), BoxLayout.Y_AXIS));

        d.add(Box.createRigidArea(new Dimension(0, 10)));

        d.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel name = new JLabel("Notes, 1.23");
        name.setFont(new Font("Serif", Font.BOLD, 13));
        name.setAlignmentX(0.5f);
        d.add(name);

        d.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                d.dispose();
            }
        });

        close.setAlignmentX(0.5f);
        d.add(close);

        d.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);

        d.setTitle("Enter your information");
        d.setDefaultCloseOperation(d.DISPOSE_ON_CLOSE);
        d.setLocationRelativeTo(null);
        d.setSize(300, 200);
        
        d.setVisible(true);*/
        JTextField username = new JTextField();
        JTextField recip = new JTextField();
        JPasswordField password = new JPasswordField();
        JCheckBox server_check = new JCheckBox();
        final JComponent[] inputs = new JComponent[] {
            new JLabel("Your IP: "+ip),
            new JLabel("Username"),
            username,
            new JLabel("Recipients"),
            recip,
            new JLabel("Server?"),
            server_check
        };
        JOptionPane.showMessageDialog(null, inputs, "Enter your info", JOptionPane.PLAIN_MESSAGE);
        
        name = username.getText();
        to = recip.getText().split(" ");
        server = server_check.isSelected();
        
        if(name != "" && to.length > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    private static void infoDialog() {
        JTextField username = new JTextField();
        username.setText(name);
        JTextArea recips = new JTextArea();
        recips.setRows(10);
        recips.setColumns(20);
        
        String str = "";
        
        for(int i=0; i<to.length; i++) {
            str += to[i]+"\n";
        }
        recips.setText(str);
        
        final JComponent[] inputs = new JComponent[] {
            new JLabel("Username"),
            username,
            new JLabel("Recipients"),
            recips
        };

        Object[] options = { "OK", "Cancel" };
        int n = JOptionPane.showOptionDialog(new JFrame(),
            inputs, "",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
            options, options[0]);
        
        if(n == JOptionPane.YES_OPTION) {
            //System.out.println("OK");
            name = username.getText();
            to = recips.getText().split("\n");
        }
    }
    
    private String readFile(String fn) {
        BufferedReader br = null;
        String out = "";
 
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(fn));
			while ((sCurrentLine = br.readLine()) != null) {
				out += sCurrentLine+"\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
        return out;
    }
    
    public static void startReceiver() {
        /*if(worker != null) {
            System.out.println(worker.isAlive());
            while(worker.isAlive()) {
                //System.out.println("Waiting for old Receiver to die");
                rec.kill();
            }
        }*/
        
        try {
            tmp = File.createTempFile("chat", ".tmp");
            tmp.deleteOnExit();
        } catch(Exception e) {
            System.out.println("Error creating temp file");
        }
        String[] pass = {"file", tmp.getAbsolutePath()};
        
        if(!server) {
            rec = new Receiver(pass);
        } else {
            rec = new ServerReceiver(to, pass);
        }
        //tmp = rec.tmp;
        //System.out.println(tmp);
        worker = new Thread(rec);
        worker.start();
    }
    
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("192.168.1.1", 80);
        ip = s.getLocalAddress().getHostAddress();
        s.close();
        
        /*System.out.println("Hello, and welcome to this chat program");
        System.out.println("Your IP address: " + ip);
        System.out.print("Username: ");
        name = scanner.nextLine();
        System.out.print("Recipient: ");
        to = scanner.nextLine();*/
        /*try {
            System.out.println(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch(Exception e) {
            System.out.println("Some theme thing is borken");
        }*/
        getInfoDialog();
        
        
        
        startReceiver();
        System.out.println("Started receiving messages");
        
        DatagramSocket datagramSocket = new DatagramSocket();

        //byte[] buffer = "0123456789".getBytes();
        //InetAddress receiverAddress = InetAddress.getLocalHost();
        //InetAddress receiverAddress = InetAddress.getByName(to);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MessageWindow ex = new MessageWindow();
                //ex.tmp = rec.tmp;
                //System.out.println(rec.tmp);
                ex.initUI();
                ex.window.setVisible(true);
            }
        });
        
        /*while(true) {
            String inp = scanner.nextLine();
            String send = name+": "+inp;
            byte[] buffer = send.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 8000);
            datagramSocket.send(packet);
        }*/
    }
    
    public MessageWindow() {
        /*try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch(Exception e) {
            System.out.println("Some theme thing is borken");
        }*/
        //MessageWindow win = new MessageWindow();
        //win.initUI();
    }
    
    public void initUI() {
        window.setTitle("Message Window");
        window.setSize(640, 480);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(window.EXIT_ON_CLOSE);
        
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        
        serverButton = new JCheckBoxMenuItem("Act as server", server);
        /*serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                server = serverButton.getState();
                rec.kill();
                //worker.interrupt();
                System.out.println("About to restart the receiver");
                startReceiver();
            }
        });*/
        serverButton.setEnabled(false);
        file.add(serverButton);
        
        JMenuItem prefs = new JMenuItem("Configure chat");
        prefs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                infoDialog();
            }
        });
        file.add(prefs);
        
        file.addSeparator();
        
        JMenuItem exitButton = new JMenuItem("Quit");
        exitButton.setToolTipText("Exit the application");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Message eMsg = new Message(name+" has left", to);
                eMsg.send();
                System.exit(0);
            }
        });
        file.add(exitButton);
        
        menubar.add(file);
        window.setJMenuBar(menubar);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane pane = new JScrollPane();
        area = new JTextArea();

        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        area.setEditable(false);
        DefaultCaret caret = (DefaultCaret) area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        pane.getViewport().add(area);
        panel.add(pane);

        window.add(panel);
        
        Timer timer = new Timer();
        timer.schedule(new UpdateText(), 0, 500);
        
        /*SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                while(true) {
                    area.setText("benis :DDD");
                    try {
                        Thread.sleep(30000);
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });*/
        
        //datagramSocket = new DatagramSocket();
        //receiverAddress = InetAddress.getByName(to);
        field.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //System.out.println(field.getText());
                String inp = field.getText();
                field.setText("");
                try {
                    sendMsg(inp);
                } catch(Exception e2) {
                    System.out.println("Something is borken");
                }
            }
        });
        field.requestFocus();
        panel.add(field, BorderLayout.SOUTH);
    }
    
    public void sendMsg(String inp) throws Exception {
        String send = name+": "+inp;
        Message msg = new Message(send, to);
        msg.send();
        
        try {
            //System.out.println(":"+tmp.getAbsolutePath());
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tmp.getAbsolutePath(), true)));
            out.println(send);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
            System.out.println("file is borken");
        }
    }
}
