/**
 * TCPClient.java
 * 
 * This is a minimal TCPClient that reads stuff from keyboard
 * and send the stuff to a server, then read stuff from server 
 * and print the stuff to screen. 
 *
 * This demo illustrates how to use Socket class to communicate
 * with a server.  You can use this to connect to a Web server 
 * and type in your HTTP request by hand.
 * 
 * Author: Ooi Wei Tsang (ooiwt@comp.nus.edu.sg)
 */
import java.net.*;
import java.io.*;

class TCPClient {
	public static void main(String args[]) {

		// Make sure we have sufficient number of command
		// line arguments.  
		if (args.length != 2) 
		{
			System.err.println("Usage: java TCPClient <hostname> <port>");
			return;
		}

		// We expect this program to be called like "java TCPClient www.nus.edu.sg 80"
		String hostname = args[0];
		int port  = Integer.parseInt(args[1]);

		// Initiate a connection to the given server.
		Socket socket;
		try 
		{
			socket = new Socket(hostname, port);
		} 
		catch (IOException e)
		{
			System.err.println("Unable to connect to " + hostname + ":" + port + " " + e.getMessage());
			return;
		}

		try 
		{
			// Set up input/output stream.
			InputStream is = socket.getInputStream();
			BufferedReader socketReader = new BufferedReader(new InputStreamReader(is));

			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);

			// Get ready to read from keyboard.
			BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));

			// Repeatedly read stuff from keyboard, until 
			// an empty line is read.  Every line is
			// sent to the server.
			while (true)
			{
				String line = keyboardReader.readLine();
				if (line.equals("")) 
				{
					dos.writeBytes("\r\n");
					break;
				}
				dos.writeBytes(line + "\r\n");
			}
			dos.flush();

			// Repeatedly read stuff from the socket, until 
			// the socket closes.  Every line is printed to
			// screen.
			String reply;
			reply = socketReader.readLine();
			while (reply != null)
			{
				System.out.println("+" + reply);
				reply = socketReader.readLine();
			}
			// close the socket finally.
			socket.close();
		} 
		catch (IOException e)
		{
			System.err.println("Unable to read/write correctly. " + e.getMessage());
		}
	}
}
