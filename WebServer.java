/**
 * WebServer.java
 * 
 * This is a minimal working Web server to demonstrate
 * Java socket programming and simple HTTP interactions.
 * 
 * Author: Ooi Wei Tsang (ooiwt@comp.nus.edu.sg)
 */
import java.net.*;
import java.io.*;

class WebServer {

	// Configure the directory where all HTML files are 
	// stored.  You need to change this to your own local
	// directory if you want to play with this server code.
	static String WEB_ROOT = "/Users/ooiwt/cs2105/code";

	public static void main(String args[]) 
	{
		ServerSocket serverSocket;
		// Create a server socket, listening on port 2105.
		try 
		{
			serverSocket = new ServerSocket(2105);
		} 
		catch (IOException e)
		{
			System.err.println("Unable to listen on port 2105: " + e.getMessage());
			return;
		}

		// The server listens forever for new connections.  This
		// version handles only one connection at a time.
		while (true) 
		{
			Socket s;
			InputStream is;
			OutputStream os;
			BufferedReader br;
			DataOutputStream dos;

			// Wait for someone to connect.
			try 
			{
				s = serverSocket.accept();
			} 
			catch (IOException e)
			{
				System.err.println("Unable to accept connection: " + e.getMessage());
				continue;
			}
			System.out.println("Connection accepted.");
			
			// Get the input stream (to read from) and output stream
			// (to write to), and wrap nice reader/writer classes around
			// the streams.
			try 
			{
				is = s.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));

				os = s.getOutputStream();
				dos = new DataOutputStream(os);

				// Now, we wait for HTTP request from the connection
				String line = br.readLine();

				// Bail out if line is null. In case some client tries to be 
				// funny and close immediately after connection.  (I am
				// looking at you, Chrome!)
				if (line == null)
				{
					continue;
				}
				
				// We are expecting the first line to be GET <filename> ...
				// We only care about the first two tokens here.
				String tokens[] = line.split(" ");

				// If the first word is not GET, bail out.  We do not
				// support PUT, HEAD, etc.
				if (!tokens[0].equals("GET"))
				{
					String errorMessage = "This simplistic server only understand GET request\r\n";
					dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
					dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
					dos.writeBytes(errorMessage);
					s.close();
					continue;
				}

				// We do not really care about the rest of the HTTP
				// request header either.  Read them off the input
				// and throw them away.
				while (!line.equals("")) 
				{
					line = br.readLine();
				}

				// Print to screen so that we have a log of client's 
				// requests.
				System.out.println("GET " + tokens[1]);

				// The second token indicates the filename.
				String filename = WEB_ROOT + tokens[1];
				File file = new File(filename);

				// Check for file permission or not found error.
				if (!file.exists()) 
				{
					String errorMessage = "I cannot find " + tokens[1] + " on this server.\r\n";
					dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
					dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
					dos.writeBytes(errorMessage);
					s.close();
					continue;
				}
				if (!file.canRead()) 
				{
					String errorMessage = "You have no permission to access " + tokens[1] + " on this server.\r\n";
					dos.writeBytes("HTTP/1.1 403 Forbidden\r\n");
					dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
					dos.writeBytes(errorMessage);
					s.close();
					continue;
				}

				// Assume everything is OK then.  Send back a reply.
				dos.writeBytes("HTTP/1.1 200 OK\r\n");

				// We send back some HTTP response headers.
				dos.writeBytes("Content-length: " + file.length() + "\r\n");

				// We could have use Files.probeContentType to find 
				// the content type of the requested file, but let 
				// me do the poor man approach here.
				if (filename.endsWith(".html")) 
				{
					dos.writeBytes("Content-type: text/html\r\n");
				}
				if (filename.endsWith(".jpg")) 
				{
					dos.writeBytes("Content-type: image/jpeg\r\n");
				}
				dos.writeBytes("\r\n");

				// Finish with HTTP response header.  Now send
				// the body of the file.
				
				// Read the content 1KB at a time.
				byte[] buffer = new byte[1024];
				FileInputStream fis = new FileInputStream(file);
				int size = fis.read(buffer);
				while (size > 0) 
				{
					dos.write(buffer, 0, size);
					size = fis.read(buffer);
				}
				dos.flush();

				// Finally, close the socket and get ready for
				// another connection.
				s.close();
			}
			catch (IOException e)
			{
				System.err.println("Unable to read/write: "  + e.getMessage());
			}
		}
	}
}
