package question2;

// Java implementation for a client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class
public class Client
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
				String [] words = tosend.split(" ");
				// If client sends exit,close this connection
				// and then break from the while loop
				if(words[0].equals("Exit"))//close connection
				{
					System.out.println("Closing this connection : " + s);
					s.close();
					System.out.println("Connection closed");
					break;
				}
				else if(words[0].equals("dealloc"))//wants to deallocate
				{
					String received = dis.readUTF();
					System.out.println(received);
				}
				else//wants to allocate
				{
					String received = dis.readUTF();
					System.out.println(received);
					if(received.equals("Resources are not free enter wait to wait for all resources, get for getting the rest abort for aborting"))//if not allocated wait, get or abort
					{
						tosend = scn.nextLine();
						dos.writeUTF(tosend);
						received = dis.readUTF();
						System.out.println(received);
						if(!tosend.equals("abort"))
						{
							received = dis.readUTF();
							System.out.println(received);
						}
					}
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