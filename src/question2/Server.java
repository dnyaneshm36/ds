// Java implementation of Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java
package question2;

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
	public static int count = 0;
	public static int write = 0;
	public static Map<Integer, String> queue = new HashMap<Integer, String>();
    // public static Map<String, String> content = new HashMap<String, String>();
    public static Map<String, Integer> total_res = new HashMap<String, Integer>();
    public static Map<String, Integer> aval_res = new HashMap<String, Integer>();
	public static void main(String[] args) throws IOException
	{
		// server is listening on port 5056
		total_res.put("a",4);
		total_res.put("b",4);
		total_res.put("c",4);

		aval_res.put("a",4);
		aval_res.put("b",4);
		aval_res.put("c",4);

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
		String received;//store string received from Client
		HashMap<String, Integer> allocated = new HashMap<>();//used to check how many resource were allocated to whom
		String toreturn="";
		int num=Server.count;
		Server.count++;
		while (true)
		{
			try {

				// Ask user what he wants
				String send = "Total resources are "+ Server.total_res+ " \nEnter resource and number from available resources and append alloc before to allocate and dealloc to deallocate" + Server.aval_res;
				dos.writeUTF(send);
				
				// receive the answer from client
				received = dis.readUTF();
				//break the string into words
				String [] words = received. split(" ");
				if(words[0].equals("Exit"))//if client wants to exit close connection
				{
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
					break;
				}
				if(words.length==3)
				{
					if(words[0].equals("alloc"))//if user wants to allocate resource
					{
						if(Server.total_res.containsKey(words[1]))//check if resource name is correct
						{
							if(Server.aval_res.get(words[1])>=Integer.parseInt(words[2]))//check if requested number of resource actually available
							{
								Server.aval_res.replace(words[1], Server.aval_res.get(words[1])-Integer.parseInt(words[2]));//allocate resouce
								toreturn="Resource allocated";
								if(allocated.containsKey(words[1]))//if previously allocated, increase count
								{
									allocated.replace(words[1],allocated.get(words[1])+Integer.parseInt(words[2]));
								}
								else//else store the number of resiurces allocated
								{
									allocated.put(words[1],Integer.parseInt(words[2]));
								}
								dos.writeUTF(toreturn);
							}
							else//requested number of resources not available
							{
								toreturn="Resources are not free enter wait to wait for all resources, get for getting the rest abort for aborting";//user can wait,get whatever available or abort
								dos.writeUTF(toreturn);
								received = dis.readUTF();
								switch (received)
								{
									case "wait" :
										toreturn="Wait for the resources";
										dos.writeUTF(toreturn);
										Server.queue.put(num,words[1]);
										boolean flag=true;
										while(flag)// wait and check if requested number of resiyrces made available
										{
											if(Server.aval_res.get(words[1])>=Integer.parseInt(words[2]))//requested number of resiurce allocated
											{
												for (Map.Entry mapElement : Server.queue.entrySet()) //check if it is the turn for it in queue
												{
													int key = (int)mapElement.getKey();
													String value = (String)mapElement.getValue();
													if((key==num)&&(value.equals(words[1])))//resources available and it it it's turn
													{
														Server.aval_res.replace(words[1], Server.aval_res.get(words[1])-Integer.parseInt(words[2]));
														Server.queue.remove(num);//remove request from queue
														toreturn="Resource allocated";
														dos.writeUTF(toreturn);
														if(allocated.containsKey(words[1]))//if previously allocated, increase count
														{
															allocated.replace(words[1],allocated.get(words[1])+Integer.parseInt(words[2]));
														}
														else//else store the number of resiurces allocated
														{
															allocated.put(words[1],Integer.parseInt(words[2]));
														}
														flag=false;
														break;
													}
													else if((key!=num)&&(value.equals(words[1])))//come out of for loop if it is not its turn
													{
														break;
													}
												}
											}
										}
										break;
									case "get" ://allocate whichever resource available in this moment
										if(allocated.containsKey(words[1]))//if previously allocated, increase count
										{
											allocated.replace(words[1],allocated.get(words[1])+Server.aval_res.get(words[1]));
										}
										else//else store the number of resiurces allocated
										{
											allocated.put(words[1],Server.aval_res.get(words[1]));
										}
										dos.writeUTF(toreturn);
										Server.aval_res.replace(words[1], 0);
										toreturn="Resource allocated";
										dos.writeUTF(toreturn);
										break;
									case "abort" ://abort
										toreturn="Aborted";
										dos.writeUTF(toreturn);
										break;
								}
							}
						}
						else
						{
							toreturn="No such resource exists";
							dos.writeUTF(toreturn);
						}
					}
					else if(words[0].equals("dealloc"))//deallocate resource
					{
						if(allocated.containsKey(words[1]))//check if it was actually allocated
						{
							if(allocated.get(words[1])>=Integer.parseInt(words[2]))//check if returned resources are less than or equals to actually allocated
							{
								Server.aval_res.replace(words[1], Server.aval_res.get(words[1])+Integer.parseInt(words[2]));
								toreturn="Resource deallocated";
								dos.writeUTF(toreturn);
							}
							else
							{
								toreturn="Trying to return more resources than allocated. transaction aboreted";
								dos.writeUTF(toreturn);
							}
						}
						else//trying to return unallocated resource
						{
							toreturn="No such resource was allocated. Transaction aborted";
							dos.writeUTF(toreturn);
						}
					}
					else//improper command
					{
						toreturn="Enter proper command";
						dos.writeUTF(toreturn);
					}
				}
				else
				{
					toreturn="Enter proper command";
					dos.writeUTF(toreturn);
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