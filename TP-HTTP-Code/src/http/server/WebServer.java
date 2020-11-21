///A Simple Web Server (WebServer.java)

package http.server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;


/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        
        InputStream socketInputStream = remote.getInputStream();
        OutputStream socketOutputStream = remote.getOutputStream();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(socketInputStream));
        PrintWriter out = new PrintWriter(socketOutputStream);

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        
        while (str != null && !str.equals("")) {
        	
        	
        	str = in.readLine();
        	System.out.println("donnees recues" + str);
        	

        	
	        if (str.startsWith("GET")) {
	        	String[] tmp = str.split(" ");
	        	String requete = tmp[0];
	        	String path = tmp[1];
	        	out.println("Get method");
		        responseGet(path, socketOutputStream);
	        }
	        remote.close();	
        }

      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  
  
  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  	public static void main(String args[]) {
  		WebServer ws = new WebServer();
  		ws.start();
  	}
  
  	public void responseGet(String path, OutputStream socketOutputStream) {
  		path = "/"+System.getProperty("user.dir")+"/../../ressources"+ path;
  		PrintWriter out = new PrintWriter(socketOutputStream);
    	File file = new File(path.substring(1));
    	
    	// Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);
	    	String ligne;
	    	while(true) {
	    		ligne = reader.readLine();
	    		if(ligne == null) {
	    			break;
	    		}
	    		out.write(ligne);
	    		out.write("\n");
	    	}

	        out.println("");
	        // Send the HTML page
	        out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
	        reader.close();
	        out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
  	}
  }
