///A Simple Web Server (WebServer.java)

package http.server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.ArrayList;


/**
 * @author JeanJacques and MELDRUM Romain FONCK 
 * @version 1.0
 */
public class WebServer {

	/**
	 * This method verifies if strNum is a number
	 * @param strNum string
	 * @return boolean
	 */
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
  /**
   * WebServer starting method. Selects the right methods depending on the request sent
   */
  protected void start() {
    ServerSocket s;
    String port = "a";
   
    BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
    
    while(!isNumeric(port)) {
    	System.out.println("Sur quel port voulez-vous que le serveur écoute ? (entrez un nombre suppérieur à 1024 svp)");
    	
    	try {
			port = systemIn.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    int numPort = Integer.parseInt(port);

    
    System.out.println("Webserver starting up on port "+ numPort);
    System.out.println("(press ctrl-c to exit)");
 
    try {
      // create the main server socket
      s = new ServerSocket(numPort);
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
	        else if (str.startsWith("HEAD")) {
	        	String[] tmp = str.split(" ");
	        	String requete = tmp[0];
	        	String path = tmp[1];
	        	out.println("Head method");
		        responseHead(path, socketOutputStream);
	        }
	        else if (str.startsWith("DELETE")) {
	        	String[] tmp = str.split(" ");
	        	String requete = tmp[0];
	        	String path = tmp[1];
	        	out.println("Delete method");
		        responseDelete(path, socketOutputStream);
	        }
	        else if(str.startsWith("PUT")) {
	        	String[] tmp = str.split(" ");
	        	String requete = tmp[0];
	        	String path = tmp[1];
	        	out.println("Put method");
		        responsePut(path, socketOutputStream, in);
	        }
	        else if (str.startsWith("POST")) {
	        	String[] tmp = str.split(" ");
	        	String requete = tmp[0];
	        	String path = tmp[1];
	        	out.println("Post method");
		        responsePost(path, socketOutputStream, in);
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
  /**
   * Gets the request file and sends it to the output stream if the file exists and the permission is not denied 
   * @param path path of the file
   * @param socketOutputStream output stream socket
   */
  	public void responseGet(String path, OutputStream socketOutputStream) {
  		path = "/"+System.getProperty("user.dir")+"/src/ressources"+ path;
  		PrintWriter out = new PrintWriter(socketOutputStream);
    	File file = new File(path.substring(1));
    	
    	
    	
    	// Send the headers
    	if(!file.exists()) {
	        out.println("HTTP/1.0 404 NOT FOUND"); 
	        out.println("Server: Bot");
    	}
    	else if(!file.canRead()) {
	        out.println("HTTP/1.0 403 PERMISSION DENIED"); 
	        out.println("Server: Bot");
    	}
    	else {
    		
    		if(path.endsWith(".py")) {
    			System.out.println("execution en cours");
    		}else {

			try {
				
				int longueur = (int) file.length();
		        out.println("HTTP/1.0 200 OK");
		        out.println("Content-Length: " +longueur);
		    	out.println("Server: Bot");
		    	out.println("");
		    	
		    	out.flush();
		    	
		    	byte[] tableauDeByte = new byte[longueur] ;
		    	

		        FileInputStream fis = null;
		        
		        try {

		            fis = new FileInputStream(file);

		            /**
		             * read file input stream into bytes
		             */
		            fis.read(tableauDeByte);

		        } finally {
		            if (fis != null) {
		                fis.close();
		            }
		        }
		        /**
		         * send the data to the socketOuputStream 
		         */
		    	socketOutputStream.write(tableauDeByte);
		        
			} catch (IOException e) {
				
	
				e.printStackTrace();
			}
    		}
		}
    	
  	}
  	/**
  	 * Head method sends the right headers to the client if the file requested exists or not
  	 * @param path path of the file
  	 * @param socketOutputStream output stream socket
  	 */
  	public void responseHead(String path, OutputStream socketOutputStream) {
  		path = "/"+System.getProperty("user.dir")+"/src/ressources"+ path;
  		PrintWriter out = new PrintWriter(socketOutputStream);
    	File file = new File(path.substring(1));

    	if(file.exists()) {
        	// Send the headers
            out.println("HTTP/1.0 200 OK"); 
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            out.println("Content-Length : "+ file.length()+ " b");  
    	}
    	else {
        	// Send the headers
            out.println("HTTP/1.0 404 NOT FOUND"); 
            out.println("Content-Type: text/html");
            out.println("Server: Bot");

    	}
    	out.flush();

    	
  	}
  	
  	/**
  	 * Delete method deletes the requested file if it has autorisation and the file exists
  	 * @param path path of the file
  	 * @param socketOutputStream output stream socket
  	 */
  	public void responseDelete(String path, OutputStream socketOutputStream) {
  		path = "/"+System.getProperty("user.dir")+"/src/ressources"+ path;
  		PrintWriter out = new PrintWriter(socketOutputStream);
    	File file = new File(path.substring(1));
    	
    	if(!file.exists() && file.canWrite()) {
        	// Send the headers
            out.println("HTTP/1.0 404 NOT FOUND "); 
            out.println("Server: Bot");
    	}
    	else if(!file.canWrite()) {
        	// Send the headers
            out.println("HTTP/1.0 403 FORBIDDEN ");
            out.println("Server: Bot");

    	}
    	else {
        	// Send the headers
            out.println("HTTP/1.0 202 ACCEPTED"); 
            out.println("Server: Bot");
            out.println("Content-Length: "+ file.length()+ " b");
    		file.delete();
    	}   
    	out.flush();
  	}
  	/**
  	 * The put method reads the head and the body of request and writes it into the selected file, if the file existed it will be overwritten 
  	 * @param path path of the file
  	 * @param socketOutputStream output stream socket
  	 * @param in Input stream to read the body of the request
  	 */
 	public void responsePut(String path, OutputStream socketOutputStream, BufferedReader in) {
  		path = "/"+System.getProperty("user.dir")+"/src/ressources"+ path;
  		PrintWriter out = new PrintWriter(socketOutputStream);
    	File file = new File(path.substring(1));
    	
    	String line = new String(":");
    	int tailleCorps = 0;
    	while (line.contains(":")) {
    		try {
    	
				line = in.readLine();
				System.out.println("header: " + line);
				
				if(line.contains("Content-length")) {
					String[] ligneTailleRequete = line.split(": ");
					tailleCorps = Integer.parseInt(ligneTailleRequete[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	

		System.out.println("taille corps : " + tailleCorps);
    	ArrayList<String> Contenu = new ArrayList<String>();
    	Integer compteurTaille = 0;

    	while(compteurTaille < tailleCorps) {
    		try {
    			
    			String ligneContenu = in.readLine();
    			Contenu.add(ligneContenu);
    			System.out.println("contenu: " +ligneContenu);
    			compteurTaille += ligneContenu.length()+1;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
    		System.out.println("taille restante à lire: " + compteurTaille);
    		
    	}
        
    	FileWriter myWriter;
		try {
			
			myWriter = new FileWriter(path);
	    	for(String ligne : Contenu) {
	    		System.out.println("contenu écrit: "+ligne);
	            myWriter.write(ligne + "\n");
	    	}
	    	myWriter.flush();
	        myWriter.close();
	        
	        
        	// Send the headers
            out.println("HTTP/1.0 202 ACCEPTED"); 
            out.println("Server: Bot");
            out.println("File-location: "+ path);
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
			
            out.println("HTTP/1.0 403 FORBIDDEN"); 
            out.println("Server: Bot");
		}

		out.flush();
    	
    	
    	

        
  	}
  	/**
  	 * The Post method append the body of the request to the file existing, if the file does not exist it sends an error code
  	 * @param path path of the file
  	 * @param socketOutputStream output stream socket
  	 * @param in Input stream to read the body of the request
  	 */
  	public void responsePost(String path, OutputStream socketOutputStream, BufferedReader in) {
  		
  		path = "/"+System.getProperty("user.dir")+"/src/ressources"+ path;
  		PrintStream out = new PrintStream(socketOutputStream);
  		File file = new File(path.substring(1));
  		
  		try {
			
			String ligne = "pain";
			int tailleCorps = 0;
			
			while(ligne != null) {
				
				ligne = in.readLine();
	    		System.out.println(ligne);
	    		
	    		if(ligne.contains("Content-Length")) {
	    			String[] ligneTailleRequete = ligne.split(": ");
	    			tailleCorps = Integer.parseInt(ligneTailleRequete[1]);
	    		}else if (!ligne.contains(": ") && tailleCorps!=0) {
	    			System.out.println("arret");
	    			break;
	    		}
	    		
	    	}
			
			int longueurRestante = tailleCorps;
			FileWriter myWriter;
			
			try {
				if(!file.exists()) {
		  			System.out.println("File-location: doesn't exists");
		  			out.println("File-location: doesn't exists");
		  		} else {
		  			
		  			myWriter = new FileWriter(path, true);
		  			BufferedWriter bw = new BufferedWriter(myWriter);
		  			
		  			while( longueurRestante>0 ) {
						System.out.println(ligne);
						ligne = in.readLine();
					    bw.write(ligne);
					    bw.newLine();
					    longueurRestante = longueurRestante - ligne.length() - 1;
		  			}
		  			bw.close();
		  			myWriter.close();
		        
		        	// Send the headers
		            out.println("HTTP/1.0 202 ACCEPTED"); 
		            out.println("Server: Bot");
		            out.println("File-location: "+ path);
		  		}
			} catch (IOException e) {
				e.printStackTrace();
				
	            out.println("HTTP/1.0 403 FORBIDDEN"); 
	            out.println("Server: Bot");
			}
			
			out.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	}
  	
  }





