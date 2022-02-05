import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

//MainClass del client che utilizzera il servizio WQ 
public class MainClassWQClient {
	//se loggato = false l'unici comandi possibili sono la registrazione e il login
	private static boolean loggato; 
	public static Scanner scanIn; //utilizzato per la linea di comando
	public static String comando;
	//l'input della tastiera è occupato per la sfida,visto che non ci posso essere più scanner attivi contemporaneamente
	public static boolean occupato; 
	//supporto che mi servirà per ogni richiesta
	public static SupportClient c;
	//in seguito al login attivo il thread dedicato ad ascoltare possibili richieste di gioco
	private static ListenerClient lc; 
	
	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
		c = new SupportClient();
		scanIn = new Scanner(System.in);
		loggato = false;
		occupato = false;
		System.out.printf("Benvenuto al servizio WQ\nDigitare wq --help per vedere i comandi\n\n");
		while(true) {
			if(!occupato) comando = scanIn.nextLine();
				//si e no riguarda solo l'accettazione della sfida
			if(!comando.equals("si") && !comando.equals("no") && !comando.equals("finito")) parser(comando);
			else Thread.sleep(5000); //aspetto se l'input serve per il gioco o no

			//il logout lo intendo proprio con la fine del servizio 
			if(comando.equals("logout")) {
				//faccio vedere la risposta per 3 secondi poi chiudo il programma
				Thread.sleep(3000);
				System.exit(0);
			}
		}
	}
	
	//riconosce il comando della linea di comando
	//e chiama metodi dedicati per lo specifico comando
	public static void parser(String msg) {
		try {
			String[] cmd = msg.split(" ");

			//lista di comandi
			if(msg.equals("wq --help")) {
				System.out.println("usage : COMMAND [ ARGS ...]");
				System.out.println("Commands:");
				System.out.println("registra_utente <nickUtente > <password > registra l' utente");
				System.out.println("login <nickUtente > <password > effettua il login");
				System.out.println("logout effettua il logout");
				System.out.println("aggiungi_amico <nickAmico> crea relazione di amicizia con nickAmico");
				System.out.println("lista_amici mostra la lista dei propri amici");
				System.out.println("sfida <nickAmico > richiesta di una sfida a nickAmico");
				System.out.println("mostra_punteggio mostra il punteggio dell’ utente");
				System.out.println("mostra_classifica mostra una classifica degli amici dell’utente (incluso l’ utente stesso)");
				System.out.println();
			}
			//registra utente
			else if(cmd[0].equals("registra_utente") && cmd.length==3) {
				int esito = SupportClient.registrazione(cmd[1],cmd[2]);
				if(esito == 0) System.out.printf("Errore nella compilazione del comando.\n\n");
				else if(esito == 1) System.out.printf("Utente già registrato.\n\n");
				else if(esito == 2) System.out.printf("Registrazione eseguita con successo.\n\n");
				else if(esito == -1) System.out.printf("Errore con la connesione al server.\n\n");
			}
			//login
			else if(cmd[0].equals("login") && cmd.length==3) {
				//se il login andato a buon fine,attivo il thread relativo all'ascolto di richieste di sfida 
				int portUDP = getPortaLibera();
				if(loggato) System.out.printf("Login errore,gia' un utente collegato al servizio.\n\n");
				else {
					if(c.login(cmd[1], cmd[2], portUDP)) {
						lc = new ListenerClient(portUDP,c);
						lc.start();
						loggato = true; 
					}
				}
				
			}
			//logout
			else if(msg.equals("logout")) {
				if(!loggato) System.out.printf("Errore devi effettuare il login.\n\n");
				else {
					c.logout();
					loggato = false; //inutile ma solo per chiarezza
				}
			}
			//aggiungi amico
			else if(cmd[0].equals("aggiungi_amico") && cmd.length==2) {
				if(!loggato) System.out.printf("Errore devi effettuare il login.\n\n");
				else c.aggiungi_amico(cmd[1]);
			}		
			//lista di amici
			else if(msg.equals("lista_amici")) {
				if(!loggato) System.out.printf("Errore devi effettuare il login.\n\n");
				else c.lista_amici();
			}
			//sfida
			else if(cmd[0].equals("sfida") && cmd.length==2) {
				if(!loggato) System.out.printf("Errore devi effettuare il login.\n\n");
				else c.richiesta_sfida(cmd[1]);

			}
			//mostra punteggio
			else if(msg.equals("mostra_punteggio")) {
				if(!loggato) System.out.printf("Errore devi effettuare il login.\n\n");
				else c.mostra_punteggio();
			}
			//mostra classifica
			else if(msg.equals("mostra_classifica")) {
				if(!loggato) System.out.printf("Errore devi effettuare il login.\n\n");
				else c.mostra_classifica();
			}
			//caso in cui il comando è sbagliato
			else 
				System.out.printf("Comando sbagliato!\ndigitare wq --help per vedere la lista completa dei comandi\n\n");
		}
		catch(Exception e) { System.out.printf("Errore connessione con il server\n\n");}
	}
	
	
	//metodo per trovate la porta udp libera da associare alla socket udp dedicata ad ascoltare le richieste
	public static int getPortaLibera() {
		int portaLibera = 2500;
		for (int i=1025; i< 2000; i++){
			try(DatagramSocket s =new DatagramSocket(i)){
				portaLibera = i;
				break;
			}
			//vuol dire che la porta non è libera
			catch (Exception e) {}
		}	
		return portaLibera;
	}

	
}
