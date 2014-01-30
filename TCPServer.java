/**
 * TCPServer.java
 * 
 * This is a minimal TCPServer that reads strings from the socket
 * and echo the same string back to the client.  
 *
 * This demo illustrates how to use ServerSocket class and Socket
 * class.  
 * 
 * Author: Ooi Wei Tsang (ooiwt@comp.nus.edu.sg)
 */
import java.net.*;
import java.io.*;

class TCPServer {
	public static void main(String args[]) throws Exception
	{
		ServerSocket serverSocket;
		try 
		{
			// Listen on port 2105 for incoming connections.
			serverSocket = new ServerSocket(2105);
		}
		catch (IOException e)
		{
			System.err.println("Unable to listen on port 2105: " + e.getMessage());
			return;
		}

		// Repeatedly accepts connection from clients until the server is killed.
		while (true)
		{
			Socket socket;
			try 
			{
				// Wait for a connection to come
				socket = serverSocket.accept();
				System.out.println("connection accepted\n");
			} 
			catch (IOException e)
			{
				System.err.println("Unable to accept connection on port 2105: " + e.getMessage());
				return;
			}
		
			// Connection accepted.  Now, get the input/output stream
			// so that we can read from/write into the socket.
			try 
			{
				InputStream is = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);

				// Repeatedly read a line from the socket and echo
				// it back to the socket.
				while (true) 
				{
					String line = br.readLine();
					if (line == null) 
					{
						socket.close();
						break;
					}
					if (line.equals("bye")) 
					{
						socket.close();
						break;
					}
					dos.writeBytes(line + "\n");
					dos.flush();
				}
			} 
			catch (IOException e)
			{
				System.err.println("Unable to read/write on socket: " + e.getMessage());
				return;
			}
		}
	}
}
