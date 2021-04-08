package question1;

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
				while (true)//run infinite loop
				{
					System.out.println(dis.readUTF());//print string received from server
					String tosend = scn.nextLine();//take input to send
					dos.writeUTF(tosend);//send input
					String [] words = tosend.split(" ");//get words from input string
					if(words[0].equals("Exit"))// If client sends exit,close this connection and then break from the while loop
					{
						System.out.println("Closing this connection : " + s);
						s.close();
						System.out.println("Connection closed");
						break;
					}
					else if(words[0].equals("Write"))//if user wants to write
					{
						String received = dis.readUTF();
						System.out.println(received);
						if(!received.equals("No access to the file"))
						{
							while(!words[0].equals("StopWrite"))//give user to write until stop write
							{
								System.out.println("Enter append to append and replace to replace before content. Enter StopWrite to stop writing");
								tosend = scn.nextLine();//send proper command to append replace or stop writing
								dos.writeUTF(tosend);
								words = tosend.split(" ");
								received = dis.readUTF();
								System.out.println(received);
							}
						}
					}
					else//handle all other cases like read or stopread as only one receiving is enough here
					{
						String received = dis.readUTF();
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
