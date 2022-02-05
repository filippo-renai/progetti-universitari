import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//classi importate per quanto riguarda il formato json
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


//classe di supporto per le operazioni richieste dal client e stampa l'esito
public class SupportClient {
	
	private Socket socket;
	private BufferedWriter outToServer;
	private BufferedReader inFromServer;
	private String nomeLog; //nome utente loggato in questo momento
	public static boolean inGame; // variabile utile per il listener di inviti per dire che sto giocando
	
	//mi connetto subito tanto non c'è problema visto che finche' non sarà loggato non posso contattare il server per
	//altri servizi
	public SupportClient() throws UnknownHostException, IOException {
		inGame = false;
		socket = new Socket(ConfigClient.HOSTNAME_SERVER, ConfigClient.TCP_PORT);
		outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	//registrazione dell'utente tramite RMI statico
	public static int registrazione(String nome,String passw) {
		//se ci sono problemi proprio con la connesione con il server ritornerà -1
		int esito = -1; 
		RegistrationInterface registra = null;
		Remote remoteObj;
		try {
			Registry registry = LocateRegistry.getRegistry(ConfigClient.RMI_PORT);
			remoteObj = registry.lookup(ConfigClient.NAME_RMI);
			registra = (RegistrationInterface) remoteObj;
			esito = registra.register(nome, passw);
		}
		//se trovo delle eccezioni semplicemente restituirà -1
		catch (RemoteException e) {}
		catch (NotBoundException e) {}
		return esito;
	}

	//operazione di login restituisce true se è stato loggato
	public boolean login(String nome,String passw,int port) {
		String msg = "login" + " " + nome + " " + passw + " " + Integer.toString(port);
		nomeLog = nome;
		try {			
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();			
			String risp = inFromServer.readLine();
			if(risp != null) {
				if(risp.equals("0")) System.out.printf("Login errore,utente non registrato.\n\n");
				else if(risp.equals("1")) System.out.printf("Login errore,password sbagliata.\n\n");
				else if(risp.equals("2")) System.out.printf("Login errore,utente gia' loggato.\n\n");
				else if(risp.equals("3")) { 
					System.out.printf("Login eseguito con successo.\n\n");
					return true;
				}
			}
		} 
		catch (IOException ex){ System.out.printf("Errore con la connesione al server.\n\n");}
		return false;
	}
	
	//il metodo logout è visto si come la fine della sessione dell'utente e la fine del servizio stesso
	public void logout() {
		String msg = "logout " + nomeLog;
		try {			
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();			
			String risp = inFromServer.readLine();
			if(risp != null) {
				if(risp.equals("si")) System.out.printf("Logout eseguito con successo.\n\n");
				//non dovrebbe avvenire
				else System.out.printf("Logout errore.\n\n");
			}
		} 
		catch (IOException ex){ System.out.printf("Errore con la connesione al server.\n\n");}
	}
	
	//metodo per aggiungere l'amico se è possibile
	public void aggiungi_amico(String amico) {
		String msg = "aggiungi_amico " + nomeLog + " " + amico;
		try {			
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();			
			String risp = inFromServer.readLine();
			if(risp != null) {
				if(risp.equals("0")) System.out.printf("Aggiungi_amico errore,amico non registrato.\n\n");
				else if(risp.equals("1")) System.out.printf("Aggiungi_amico errore,utente amico e' gia' nella lista amici.\n\n");
				else if(risp.equals("2")) System.out.printf("Amicizia " + nomeLog +"-" + amico + " creata.\n\n");
			}
		} 
		catch (IOException ex){ System.out.printf("Errore con la connesione al server.\n\n");}	
	}
	
	//metodo per mostrare il punteggio dell'utente
	public void mostra_punteggio() {
		String msg = "mostra_punteggio " + nomeLog;
		try {			
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();			
			String risp = inFromServer.readLine();
			if(risp != null) {
				//tanto se il numero è String o int non importa 
				System.out.printf("Punteggio: " + risp + "\n\n");
			}
		} 
		catch (IOException ex){ System.out.printf("Errore con la connesione al server.\n\n");}	
	}
	
	//metodo per vedere gli amici dell'utente
	public void lista_amici() {
		String msg = "lista_amici " + nomeLog;
		try {			
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();		
			String risp = inFromServer.readLine();
			if(risp != null) {
				String amici = "";
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray) parser.parse(risp);
				//prendo l'array json e semplicemente li prendo e li metto nella stringa amici che stampo
				for(int i=0; i<array.size();i++) {
					if(i!=array.size()-1) amici += ((String) array.get(i) + ", ");
					else amici += (String) array.get(i);
				}
				if(array.size() == 0) System.out.printf("Nessun amico.\n\n");
				else System.out.printf(amici + "\n\n");
			}	
		} 
		catch(ParseException ex) {System.out.printf("Errore invio del messaggio del server.\n\n");}
		catch(IOException ex){ System.out.printf("Errore con la connesione al server.\n\n");}	
	}
	
	//metodo che mostra la classifica
	public void mostra_classifica() {
		String msg = "mostra_classifica " + nomeLog;
		try {			
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();		
			String risp = inFromServer.readLine();
			if(risp != null) {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(risp);
				System.out.printf("Classifica: ");
				for(int i=0;i<jsonObject.size();i++) {
					JSONArray array = (JSONArray) jsonObject.get(Integer.toString(i));
					//array[0] -> nome utente, array[1] -> punteggio
					System.out.printf(array.get(0).toString() + " " + array.get(1).toString());
					if(i != jsonObject.size()-1) System.out.printf(", ");
				}
				System.out.printf("\n\n");
			}	
		} 
	   catch(ParseException ex) {System.out.printf("Errore invio del messaggio del server.\n\n");}
		catch(IOException ex){ System.out.printf("Errore con la connesione al server.\n\n");}	
	}
	
	//dove chiedo la sfida allo sfidante
	public void richiesta_sfida(String sfidante) {
		String msg = "richiesta_sfida " + nomeLog + " " + sfidante;
		try {
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();	
			System.out.printf("Sfida a " + sfidante + " inviata. In attesa di accettazione.\n");
			String risp = inFromServer.readLine();
			if(risp != null) {
				if(risp.equals("0")) System.out.printf("Sfida errore,sfidante non è amico.\n\n");
				if(risp.equals("1")) System.out.printf("Sfida errore,sfidante non è online.\n\n");
				if(risp.equals("2")) System.out.printf("Sfida errore,sfidante non ha risposto o perchè sta giocando o risposta perduta.\n\n");
				if(risp.equals("3")) System.out.printf("Sfida errore,sfidante non ha accettato la sfida.\n\n");
				if(risp.equals("4")) {
					System.out.printf("Sfida accetata!\n\n");
					game(sfidante);
				}		
			}
		}
		catch(Exception ex) {System.out.printf("Errore con la connesione al server\n\n");}
	}
	
	//sfida vera e propria 
	public void game(String sfidante) {
		inGame = true;
		System.out.println("Sfida contro "  + sfidante +"!");
    
		String msg = "sfida " + nomeLog + " " + sfidante;
		try {
			outToServer.write(msg);
			outToServer.newLine();
			outToServer.flush();	
			while(true) {
				try {					
					String risp = inFromServer.readLine();
					if(risp != null) {
						System.out.println(risp);
						//servirà per il listener
						//tempo o partita finita esco
						if(risp.equals("fine tempo") || risp.equals("fine partita")) break;
						outToServer.write(MainClassWQClient.scanIn.nextLine());
						outToServer.newLine();
						outToServer.flush();	
					}
				} 
				catch(Exception ex) {System.out.printf("Errore con la sfida.\n\n");break;}
			}
			//finito il gioco mi da il resoconto
			String risp = inFromServer.readLine();
			if(risp != null) {
				String punteggi[] = risp.split(" ");
				System.out.println("Hai tradotto correttamente "+ punteggi[0] + " parole, ne hai sbagliate " +
						punteggi[1] + " e non risposto a " + punteggi[2] + ".");
				System.out.println("Hai totalizzato "+ punteggi[3] +" punti.");
				System.out.println("Il tuo avversario ha totalizzato "+ punteggi[4] +" punti.");
				//vinto o perso
				if(punteggi[5].equals("vinto")) 
					System.out.printf("Congratulazioni, hai vinto! Hai guadagnato "+ punteggi[6] + " punti extra, per un totale di "+ 
							punteggi[7]+ " punti!\n\n");
				else if(punteggi[5].equals("perso"))
					System.out.printf("Mi spiace, hai perso! Non hai guadagnato punti bonus\n\n");
				else if(punteggi[5].equals("pareggiato"))
					System.out.printf("Parita', hai pareggiato! Hai guadagnato comunque "+ punteggi[6] + " punti extra, per un totale di "+ 
							punteggi[7]+ " punti!\n\n");
					
			}

		}
		catch(Exception e) {System.out.printf("Errore con la connesione al server\n\n");}	
		//non ho più bisogno della tastiera, e non sto più giocando posso ricevere richieste di sfide
		
		MainClassWQClient.occupato = false;
		MainClassWQClient.comando = "finito";
		inGame = false;

	}

}
