import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import Configurazione.ConfigServer;
import Dati.DataBase;
import Dati.OnlineUtenti;

//classe principale del server dove viene attivato,e create tutte le compenenti utili per il servizio
public class MainClassWQServer {
	
	private static DataBase d ; //database degli utenti del servizio
	private static ThreadPoolExecutor pool; 
	//Socket principale sulla quale si accettano le connessioni dei clients
	private static ServerSocket welcomeSocket; 
	//per mandare richieste di gioco agli utenti sfida via udp
	private static DatagramSocket serverUdpSocket;
	private static OnlineUtenti online;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.printf("Servizio Word Quizzle attivo!\n\n");
		
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		welcomeSocket = new ServerSocket(ConfigServer.TCP_PORT);
		serverUdpSocket = new DatagramSocket(ConfigServer.portServerUDP);
		//intervello di tempo che il client deve rispondermi nella comunicazione udp
		serverUdpSocket.setSoTimeout(ConfigServer.T1);
		d = new DataBase(ConfigServer.pathDataJson);
		online = new OnlineUtenti();
      //utilizzo il metodo statico per far i modo di avere le registrazioni degli utenti
		regRmi(d);
		
		//attivo il thread dedicato all'ascolto delle richieste
		Listener listenerThread = new Listener(d,welcomeSocket,pool,online,serverUdpSocket); 
		listenerThread.start();

		//ciclo utilizzato per aggiornare possibili cambiamenti nel database che sovrascrivera' il file json
		while (true) {
			Thread.sleep(1000);
			//persistenza dei dati
			d.persJson(ConfigServer.pathDataJson);
		}
	}

	//metodo utilizzato per creare l'oggetto remoto che servirà per la registrazione degli utenti
	public static void regRmi(DataBase b) {
		try {
			RegistrationImpl reg = new RegistrationImpl(b);
			RegistrationInterface stub = (RegistrationInterface) UnicastRemoteObject.exportObject(reg, 0);
			LocateRegistry.createRegistry(ConfigServer.RMI_PORT);
			Registry registry = LocateRegistry.getRegistry(ConfigServer.RMI_PORT);
			registry.rebind(ConfigServer.NAME_RMI, stub);
		} 
		catch (RemoteException e) {System.out.println("Problema con la RMI");}
	}

}
