/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Hitesh Mohite
 */
public class server_for_chat
{
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException
    {
        chatServer CS = new chatServer();
    }
}
class chatServer{

    /**
     * @param args the command line arguments
     */
    //port for the server
    public int connecting_port = 20500;
    //player number
    public int player = 0;
    //status of the game
    public boolean chat_status = false;
    //these two variable sets the no of clients playing the game
    public int no_of_clients = 0, max_clients = 2;
    //creating a serversocket variable
    public ServerSocket server;
    //this variable socket accepts clients
    public Socket accept_client;
    //stores the clients threads into this array list
    public ArrayList<client> clients;
    //defines the queue object for each player of type string
    public Queue<String> store_chat;
    //varible to hold the terminal condition
    public String result;
    //creates a object of type server
    public chatServer serve_client;
    //stores the specified client's object
    public client new_obj1, new_obj2;
    
    public chatServer() throws IOException {
        // TODO code application logic here
        //object for server socket
        server = new ServerSocket(connecting_port);
        //instatiating the arraylist object
        clients = new ArrayList<client>();
        //instatiating the newpoint object
        serve_client = this;
        //store the messeges in the list
        store_chat = new LinkedList<>();
        
        store_chat.add("FIRST of MESSAGES");
        //this while loop will iterate till it accepts all the clients
        //required to play the game
        while(no_of_clients < max_clients)
        {
            //accepting individual client
            accept_client = server.accept();
            //creating the thread for ech client
            client newClient = new client(accept_client);
            //adding each client object to the arraylist
            clients.add(newClient);
            if(no_of_clients == 0)
            {
                newClient.send_data("Set LIMIT");
                max_clients = Integer.parseInt(newClient.read_client_data());
            }
            //calls the starts method for each client
            newClient.starts(max_clients, serve_client, no_of_clients+1); 
            //incrementing the no of clients variable
            no_of_clients++;
        }
        chatHandler handle = new chatHandler(serve_client, clients);
    } 
}
class client extends Thread
{
    //creating an object for receiving the data
    DataInputStream getData;
    //creating an object for sending the data
    DataOutputStream sendData;
    //creates a socket variable
    Socket new_client;
    //creates the variable for newPoint
    chatServer obj;
    //stores the access of current player
    int current_player;
    //stores the no of players involved in this game
    int total_players;
    //variable to recieve data that is sent from client side 
    String data_received;
    
    public client(Socket S) throws IOException 
    {
        new_client = S;
        sendData = new DataOutputStream(new_client.getOutputStream());
        getData = new DataInputStream(new_client.getInputStream());
    }
    
    //method to initialise players, newPoint and player variables
    //and also to invoke start method
    public void starts(int players, chatServer np, int player)
    {
        total_players = players;
        current_player = player;
        obj = np;
        start();
    }
    
    public void run()
    {
        while(!data_received.equals("exit"))
        {
            try {
                data_received = read_client_data();
                obj.store_chat.add(data_received);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //reads data from the client
    String read_client_data() throws IOException
    {
        return getData.readUTF();
    }
    
    //sends data to the client
    void send_data(String data_to_send) throws IOException
    {
        System.out.println("The following data is to be sent a client");
        System.out.println(data_to_send);
        sendData.writeUTF(data_to_send);
        System.out.println("Data sent");
    }
}
class chatHandler extends Thread
{
    chatServer chat;
    //Queue<String> messages;
    ArrayList<client> clients;
    
    public chatHandler(chatServer s, ArrayList<client> c)
    {
        chat = s;
        clients = c;
        start();
    }
    
    public void run()
    {
        while(true)
        {
            if(chat.store_chat.isEmpty())
            {
                try {
                    wait(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            else
            {
                System.out.println(chat.store_chat.poll());
            }
        }
    }
}