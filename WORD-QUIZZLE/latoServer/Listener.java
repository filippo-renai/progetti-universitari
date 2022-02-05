import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

import Dati.DataBase;
import Dati.OnlineUtenti;

//Thread Listener che si mette in attesa di connessioni da parte dei clients.
public class Listener extends Thread {

	private DataBase d ; //database degli utenti del servizio
	private ThreadPoolExecutor pool; 
	//Socket principale sulla quale si accettano le connessioni dei clients
	private ServerSocket welcomeSocket; 
	private OnlineUtenti online;
	private DatagramSocket serverUdpSocket;
	
	public Listener(DataBase d,ServerSocket welcomeSocket,ThreadPoolExecutor pool,OnlineUtenti online,
			DatagramSocket serverUdpSocket) {
		this.online = online;
		this.d=d;
		this.welcomeSocket = welcomeSocket;
		this.pool = pool;
		this.serverUdpSocket =serverUdpSocket;
	}
	
	public void run() {
		
		while (true) {	
			Socket connectionSocket = null;
			try {
				connectionSocket = welcomeSocket.accept();
				System.out.println("Connessione con un client");
				//Task eseguito dal threadpool
				UserRequestHandler task = new UserRequestHandler(connectionSocket,d,online,serverUdpSocket);
				pool.execute(task);
				
			}
			catch (IOException e) { System.out.printf("Problema connessione tcp con i clients\n\n");}			
		}
	}

}
