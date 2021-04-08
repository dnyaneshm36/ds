import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
  
// Server class

public class Pserver 
{
   
    
    public static void main(String[] args) throws IOException 
    {
        // server is listening on port 5056
        Map<String, Integer> files = new HashMap<String, Integer>();
        Map<String, String> content = new HashMap<String, String>();
        Map<String, Integer> readers = new HashMap<String, Integer>();
        Map<String, Integer> writers = new HashMap<String, Integer>();
        files.put("England.txt", 1);
        content.put("England.txt", "Londan");
        ServerSocket ss = new ServerSocket(5056);
        System.out.println("Server Started....  ");
        // running infinite loop for getting
        // client request
        //
        // 0-abset 1-present 2-writeMode
           
        //
        while (true) 
        {
            Socket s = null;
              
            try 
            {
                // socket object to receive incoming client requests
                s = ss.accept();
                System.out.println("A new client is connected : " + s.getLocalPort());

                  
                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                  
                
                // create a new thread object
                Thread t = new ClientHandler(s, dis, dos,files,content,readers,writers,s.getLocalPort());
  
                // Invoking the start() method
                t.start();
                  
            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}
  
// ClientHandler class
class ClientHandler extends Thread 
{

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    final int client_name;
    Map<String, Integer> files = new HashMap<String, Integer>();
    Map<String, String> content = new HashMap<String, String>();
    Map<String, Integer> readers = new HashMap<String, Integer>();
    Map<String, Integer> writers = new HashMap<String, Integer>();
    
  
    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos,Map<String, Integer> files,Map<String, String> content,Map<String, Integer> readers,Map<String, Integer> writers,int client_name) 
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.files=files;
        this.content=content;
        this.readers=readers;
        this.writers=writers;
        this.client_name=client_name;
    }
  
    @Override
    public void run() 
    {
        String received;
        String operation;


        while (true) 
        {
            try {
                
                dos.writeUTF("Enter the file name you want to access\n"+"Type Exit to terminate connection.");
                  
                // receive the answer from client
                received = dis.readUTF();
                  
                if(received.equals("Exit"))
                { 
                    this.s.close();
                    break;
                }
                int status=0;
                
                if(files.get(received).equals(null))
                {
                    dos.writeUTF("File NotFound");
                }
                else
                {
                    status = files.get(received);
                    switch(status)
                    {
                        case 0 :
                        dos.writeUTF("Deleted");
                        break;
                        case 1 :
                        dos.writeUTF("Present");
                        operation = dis.readUTF();
                        if(operation.equals("r") || operation.equals("read") )
                        {
                            readers.put(received,client_name);
                            System.out.println("Setting "+received);
                            dos.writeUTF(content.get(received));
                            dos.writeUTF("Success");
                        }
                        else if(operation.equals("w") || operation.equals("write"))
                        {
                            files.replace(received,2);
                            writers.put(received,client_name);
                            String text = dis.readUTF();
                            if(!(text.equals("stop")))
                            content.replace(received,text);
                            dos.writeUTF("Success");
                            //setting free from write
                            files.replace(received,1);
                        }
                        break;
                        case 2 :
                        dos.writeUTF("Write");
                        break;
                        
                    }
                }
                    
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
          
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
              
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}