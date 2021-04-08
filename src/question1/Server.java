// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java
package question1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Server class
public class Server
{	
	public static int read = 0;
	public static int write = 0;
	public static Map<String, String> filedata = new HashMap<String, String>();
    // public static Map<String, String> content = new HashMap<String, String>();
    public static Map<String, Integer> fileread = new HashMap<String, Integer>();
    public static Map<String, Integer> filewrite = new HashMap<String, Integer>();
	public static void main(String[] args) throws IOException
	{
		// server is listening on port 5056
		filedata.put("first.txt"," content first file data.");
		filedata.put("second.txt"," content second file data.");
		filedata.put("third.txt"," content third file data.");
		fileread.put("first.txt",0);
		fileread.put("second.txt",0);
		fileread.put("third.txt",0);
		filewrite.put("first.txt",0);
		filewrite.put("second.txt",0);
		filewrite.put("third.txt",0);


		ServerSocket ss = new ServerSocket(5056);
		
		// running infinite loop for getting
		// client request
		while (true)
		{
			Socket s = null;
			
			try
			{
				// socket object to receive incoming client requests
				s = ss.accept();
				
				System.out.println("A new client is connected : " + s);
				
				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				
				System.out.println("Assigning new thread for this client");

				// create a new thread object
				Thread t = new ClientHandler(s, dis, dos);

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
	public static ArrayList< String> read;
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	

	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)
	{
		this.s = s;
		this.dis = dis;
		this.dos = dos;
	}

	@Override
	public void run()
	{
		String received;//for storing information received from client
		String toreturn="";//for storing information to be sent to client
		while (true)
		{
			try {

				// Ask user what he wants
				dos.writeUTF("Read filename for reading | Write filename for writing | StopRead file name to stop reading\n"+
							"Type Exit to terminate connection.");
				

				received = dis.readUTF();// receive the answer from client
				String [] words = received. split(" ");//get the words from the string
				
				if(words[0].equals("Exit"))//Close connection of the client
				{
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
					break;
				}
				
				// write on output stream based on the
				// answer from the client
				switch (words[0]) {
					case "Read" ://Client wants to read
						if(words.length==2)//if len!=2 Client has not sent proper comand
						{
							if (Server.filedata.containsKey(words[1]))//check if filename is available
							{
								if(Server.filewrite.get(words[1])==0)//check if noone is writing
								{
									if(Server.fileread.get(words[1])==0)//check if no one is reading
									{
										toreturn="The content of the file is: "+Server.filedata.get(words[1]);//give access to read
										Server.fileread.replace(words[1], Server.fileread.get(words[1])+1);//increase read count
										read.add(words[1]);//add filename of which access were given
										
									}
									else//if no one writing but someone reading
									{
										toreturn="Read only mode : The content of the file is: "+Server.filedata.get(words[1]);//give access in read only mode
										Server.fileread.replace(words[1], Server.fileread.get(words[1])+1);//increase read count
										read.add(words[1]);//add filename of which access were given
										
									}
								}
								else//someone is writing so no access is given
								{
									toreturn="No access to the file \n ";
								}
								dos.writeUTF(toreturn);
							}
						}
						else//filename is not entered abort transmission
						{
							toreturn=" Enter filename \n ";
							dos.writeUTF(toreturn);
						}
						break;
					case "Write" ://Client wants to write
						if(words.length==2)//check command is proper
						{
							if (Server.filedata.containsKey(words[1]))//filename is available
							{
								if((Server.filewrite.get(words[1])==0)&&(Server.fileread.get(words[1])==0))//noone is reading or writing
								{
									toreturn="You can start writing write the changed version";//give access to write
									Server.filewrite.replace(words[1], Server.filewrite.get(words[1])+1);//increase writecount
									dos.writeUTF(toreturn);//return comment
									received = dis.readUTF();//store the received
									String [] words1 = received.split(" ");
									while(!words1[0].equals("StopWrite"))//check if Client wants to stop writing
									{
										if(words1[0].equals("replace"))//replace content of file
										{
											String newstr="";
											for(int i=1;i<words1.length;i++)
											{
												newstr+=" ";
												newstr+=words1[i];
											}
											Server.filedata.replace(words[1], newstr);
											toreturn="Replaced";
											dos.writeUTF(toreturn);
										}
										else if(words1[0].equals("append"))//append content of file
										{
											String newstr=Server.filedata.get(words[1]);
											for(int i=1;i<words1.length;i++)
											{
												newstr+=" ";
												newstr+=words1[i];
											}
											Server.filedata.replace(words[1], newstr);
											toreturn="Appended";
											dos.writeUTF(toreturn);
										}
										else//proper operation is not entered
										{
											toreturn="Enter proper operation";
											dos.writeUTF(toreturn);
										}
										received = dis.readUTF();
										words1 = received.split(" ");
									}
									Server.filewrite.replace(words[1], Server.filewrite.get(words[1])-1);
									toreturn="Write access taken back";
									dos.writeUTF(toreturn);
								}
								else//someone is reading or writing
								{
									toreturn="No access to the file";
									dos.writeUTF(toreturn);
								}
							}
							else//filename is not available
							{
								toreturn="No such file exists";
								dos.writeUTF(toreturn);
							}
						}
						else//no proper command
						{
							toreturn="Enter filename";
							dos.writeUTF(toreturn);
						}
						break;
					
					case "StopRead" ://user wants to stop reading
						if(words.length==2)//check command is proper
						{
							if (Server.filedata.containsKey(words[1]))
							{
								if(read.contains(words[1]))//check if actually access was given previously
								{
									Server.fileread.replace(words[1], Server.fileread.get(words[1])-1);
									toreturn="Reading access taken back";
									read.remove(words[1]);//remove access
								}
								else//no access
								{
									toreturn="You did not have reading access to the file";
								}
							}
							else//wrong filename
							{
								toreturn="No such file exists";
							}
							dos.writeUTF(toreturn);
						}
						else//not proper command
						{
							toreturn="Enter filename";
							dos.writeUTF(toreturn);
						}
						break;
					default://invalid input
						dos.writeUTF("Invalid input");
						break;
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

