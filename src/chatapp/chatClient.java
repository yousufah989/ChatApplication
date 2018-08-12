/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hitesh Mohite
 */
public class chatClient {

    /**
     * @param args the command line arguments
     */
    public static int connecting_port = 20500;
    public static int total_players = 0;
    public static InetAddress server_name;
    public static Socket client;
    
    
    public static String readData = "", game_status = "", read_user = "";
    public static boolean breakable = true;
    public static int strikes = 0, strikes_req = 5, store_temp = 0;
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        // TODO code application logic here
        
        server_name = InetAddress.getLocalHost();
        client = new Socket(server_name, connecting_port);
        chat_on();
    }
    
    public static void chat_on() throws IOException
    {
        writeMessage WM = new writeMessage(client);
        readMessage RM = new readMessage(client);
    }
}
class writeMessage extends Thread
{
    public DataOutputStream sendData;
    public BufferedReader from_user, at_client;
    public String send_message = "";
    
    public writeMessage(Socket Client) throws IOException 
    {
        from_user = new BufferedReader(new InputStreamReader(System.in));
        sendData = new DataOutputStream(Client.getOutputStream());
        System.out.println("You can write a message now!!!");
        start();
    }
    
    public void run()
    {
        while(true)
        {
            try {
                long start_time = System.currentTimeMillis();
                
                while(((System.currentTimeMillis() - start_time) <= 20000) && !from_user.ready())
                {
                    
                }
                
                if(from_user.ready())
                {
                    try {
                        send_message = from_user.readLine();
                        write_to_server(send_message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    System.out.println("Entry TIMEDOUT!!!!");
                    System.out.println("BE QUICK!!!");
                }
            } catch (IOException ex) {
                Logger.getLogger(writeMessage.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        }
    }
    
    public void write_to_server(String send) throws IOException
    {
        sendData.writeUTF(send);
    }
}
class readMessage extends Thread
{
    public DataInputStream getData;
    public String readData = "";
    
    public readMessage(Socket Client) throws IOException 
    {
        getData = new DataInputStream(Client.getInputStream());
        System.out.println("Messages from any other user will be displayed");
        start();
    }
    
    public void run()
    {
        while(true)
        {
            try {
                read_from_server();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void read_from_server() throws IOException
    {
        readData = getData.readUTF(); 
        System.out.println("message received : ");
        System.out.println(readData);
    }
}