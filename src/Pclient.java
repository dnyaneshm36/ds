import java.io.*;
import java.net.*;
import java.util.Scanner;
  
// Client class
public class Pclient 
{
    public static void main(String[] args) throws IOException 
    {
        try
        {
            Scanner scn = new Scanner(System.in);
              
            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");
      
            // establish the connection with server port 5056
            Socket s = new Socket(ip, 5056);
      
            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
      
            // the following loop performs the exchange of
            // information between client and client handler
            while (true) 
            {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                  
                // If client sends exit,close this connection 
                // and then break from the while loop
                if(tosend.equals("Exit"))
                {
                    s.close();
                    break;
                }
                  
                // printing date or time as requested by client
                String received = dis.readUTF();
                //System.out.println(received);
                if(received.equals("Present"))
                {   
                    System.out.println("r to read w to write");
                    String option = scn.nextLine();
                    dos.writeUTF(option);
                    if(option.equals("r") || option.equals("read"))
                    {
                        received = dis.readUTF();
                        System.out.println(received);
                        received = dis.readUTF();
                        System.out.println(received);
                    }
                    else
                    {
                        System.out.println("write");
                        String text = scn.nextLine();
                        dos.writeUTF(text);
                        received = dis.readUTF();
                        System.out.println(received);
                    }
                        

                }
                else if(received.equals("Write"))
                {   
                    System.out.println("No Access");
                }
                
                else
                {
                    System.out.println(received);
                }
                
            }
              
            // closing resources
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}