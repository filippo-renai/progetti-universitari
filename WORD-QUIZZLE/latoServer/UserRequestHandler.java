import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import Configurazione.ConfigServer;
import Dati.DataBase;
import Dati.OnlineUtenti;
import Dati.UtenteSfida;


//Oggetto runnable che rappresenta il task eseguito dal threadpool. Si occupa di gestire le richieste dei clients
public class UserRequestHandler implements Runnable {
	
	private Socket connectionSocket; //Socket principale sulla quale si accettano le connessioni dei clients
	private DataBase d ; //database degli utenti del servizio
	private OnlineUtenti online; //struttura utile per la gestione della partita e degli utenti online
	private DatagramSocket serverUdpSocket; //socket udp dove avrò la risposta del client per un relativo invito
	
	public UserRequestHandler(Socket socket,DataBase d,OnlineUtenti online,DatagramSocket serverUdpSocket){	
		this.connectionSocket = socket;
		this.online = online;
		this.d=d;
		this.serverUdpSocket = serverUdpSocket;
	}
	
	public void run(){

		try {		
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
			while(true) {					
				//Stream in input ed output collegati alla socket principale per comunicare con il client
				
				//Leggo la stringa inviatami dal client per richiedere una particolare operazione
				String op = inFromClient.readLine();
				if(op != null) { 				
					String[] cmd = op.split(" ");
					//login
					if(cmd[0].equals("login")){
						String risposta = d.loginUtente(cmd[1], cmd[2]);
						//crenziali giuste e ancora non loggato
						if(risposta.equals("2")  && !online.isOnline(cmd[1])) {
							online.addOnline(cmd[1],Integer.parseInt(cmd[3]));
							risposta = "3";						
						}
						outToClient.write(risposta);
						outToClient.newLine();
						outToClient.flush();		
					}
					//logout
					else if(cmd[0].equals("logout")) {
						online.offline(cmd[1]);		
						outToClient.write("si");
						outToClient.newLine();
						outToClient.flush();				
					}
					//aggiungi_amico
					else if(cmd[0].equals("aggiungi_amico")) {
						//utente è sempre online
						//utente da aggiungere non registrato
						if(!d.isReg(cmd[2])) {
							outToClient.write("0");
							outToClient.newLine();
							outToClient.flush();
						}
						else {
							//utente da aggiungere è già un amico
							if(d.isAmico(cmd[1], cmd[2])) {
								outToClient.write("1");
								outToClient.newLine();
								outToClient.flush();								
							}
							//aggiungo l'utente
							else {
								d.addAmico(cmd[1], cmd[2]);
								outToClient.write("2");
								outToClient.newLine();
								outToClient.flush();	

							}
						}
					}
					//lista_amici
					else if(cmd[0].equals("lista_amici")) {
						List<String> amici = d.listAmici(cmd[1]);
						JSONArray array = new JSONArray();
						for(String a : amici)
							array.add(a);					
						//mando il json array come una stringa
						outToClient.write(array.toJSONString());
						outToClient.newLine();
						outToClient.flush();		
					}
					//mostra_punteggio
					else if(cmd[0].equals("mostra_punteggio")) {
						//invio sotto forma di stringa per semplicita
						String risposta = Integer.toString(d.getPunteggio(cmd[1]));
						outToClient.write(risposta);
						outToClient.newLine();
						outToClient.flush();	
						
					}
					//mostra_classifica
					else if(cmd[0].equals("mostra_classifica")) {
						//la chiave è la posizione della classifica il valore è un array con il nome dell'utente
						//e il suo punteggio
						JSONObject obj = new JSONObject ();
						List<String> clasUt = d.mostraClass(cmd[1]);
						
						for(int i=0;i<clasUt.size();i++) {
							JSONArray array = new JSONArray();
							array.add(clasUt.get(i)); //aggiungo prima il nome 
							array.add(new Integer((d.getPunteggio(clasUt.get(i))))); //aggiungo il punteggio
							obj.put(Integer.toString(i), array);
						}

						outToClient.write(obj.toJSONString());
						outToClient.newLine();
						outToClient.flush();	
					}	
					//richiesta di sfida
					else if(cmd[0].equals("richiesta_sfida")) {
						//lo sfidante non è amico 
						if(!d.isAmico(cmd[1], cmd[2])) {
							outToClient.write("0");
							outToClient.newLine();
							outToClient.flush();	
						}
						//lo sfidante è amico ma non online
						else if(!online.isOnline(cmd[2])) {
							outToClient.write("1");
							outToClient.newLine();
							outToClient.flush();	
						}
						else {
							//invio la richiesta
							byte[] buffer1 = cmd[1].getBytes("US-ASCII");
							InetAddress address = InetAddress.getByName(ConfigServer.hostNameClient);
							DatagramPacket myPacket = new DatagramPacket(buffer1, buffer1.length, address, 
									online.getPorta(cmd[2]));
							serverUdpSocket.send(myPacket);
							//aspetto fino ad un tot
							byte[] buffer2 = new byte[1000];
							DatagramPacket receivedPacket = new DatagramPacket(buffer2, buffer2.length);
							try {
								serverUdpSocket.receive(receivedPacket);		
								String risposta = new String(receivedPacket.getData(), 0, 
										receivedPacket.getLength(), "US-ASCII");
								//se vuole sfidarti o no o se sta giocando
								if(risposta.equals("si")){
									//entrambi voglio giocare allora gli do le stesse parole da tradurre
									List<String> sfida = listaParole();
									online.nuovaPartita(cmd[1], sfida);
									online.nuovaPartita(cmd[2], sfida);
									//mando la risposta al client che ho ricevuto l'esito positivo di sfida
									serverUdpSocket.send(myPacket);
									
									outToClient.write("4");
									outToClient.newLine();
									outToClient.flush();	
								}
								else{
									outToClient.write("3");
									outToClient.newLine();
									outToClient.flush();	
								}
							}
							//tempo scaduto
							catch(Exception e) {
								outToClient.write("2");
								outToClient.newLine();
								outToClient.flush();	
							}
							
						}
					}
					//sfida vera e propria
					else if(cmd[0].equals("sfida")) {
						//setto tutto per la partita
						UtenteSfida giocatore = online.utenteSfida(cmd[1]);
						UtenteSfida sfidante = online.utenteSfida(cmd[2]);
						List<String> parIt = giocatore.getParole();
						int punteggio = 0;
						int corrette = 0;
						int sbagliate = 0;
						int noRisp = 0;
						boolean fineTempo = false;

						long sTime = System.currentTimeMillis(); //inizio del tempo
						for(int i=0;i<ConfigServer.K;i++) {
							String domanda = "Challenge " + Integer.toString(i+1) + "/" +Integer.toString(ConfigServer.K) 
							+ ": " + parIt.get(i);
							outToClient.write(domanda);
							outToClient.newLine();
							outToClient.flush();	
							String risposta = inFromClient.readLine();
							//tempo scaduto
							if((System.currentTimeMillis() - sTime) >= ConfigServer.T2) {
								fineTempo = true;
								outToClient.write("fine tempo");
								outToClient.newLine();
								outToClient.flush();	
								break;
							}
							if(risposta != null) {
								String parEn = traduzione(parIt.get(i));
								//risposta esatta o sbaglia
								//se la traduzione da parte del servizio esterno 
								//non è riuscita la conto come se non fosse risposta
								if(risposta.equals(parEn)){
									corrette++;
									punteggio += ConfigServer.X;				
								}
								else if(!risposta.equals(parEn) && !parEn.equals("No traduzione")) {
									sbagliate++;
									punteggio -= ConfigServer.Y;
								}
							}		
						}
						noRisp = ConfigServer.K - (corrette+sbagliate);
						if(!fineTempo) {
							outToClient.write("fine partita");
							outToClient.newLine();
							outToClient.flush();	
						}
						giocatore.partitaFin(punteggio);
						//ASPETTO CHE LO SFIDANTE ABBIA FINITO
						while(!sfidante.isFinish())
							Thread.sleep(500);
						
						String esito = "";
						//ho vinto
						if(punteggio > sfidante.getPunteggio()) {
							int punteggioTot = punteggio + ConfigServer.Z;
							esito = Integer.toString(corrette) + " " + Integer.toString(sbagliate) + " "
							+ Integer.toString(noRisp) + " " +
							Integer.toString(punteggio) + " "+ Integer.toString(sfidante.getPunteggio()) + " vinto " + Integer.toString(ConfigServer.Z) + " " 
							+ Integer.toString(punteggioTot);
							
							d.changePunteggio(cmd[1], punteggioTot);
						}
						//ho perso
						else if(punteggio < sfidante.getPunteggio()) {
							esito = Integer.toString(corrette) + " " + Integer.toString(sbagliate) + " "
							+ Integer.toString(noRisp) + " "+
							Integer.toString(punteggio) + " "+ Integer.toString(sfidante.getPunteggio()) + " perso";
							
							d.changePunteggio(cmd[1], punteggio);
						}
						//se pareggio entrambi prendono il bonus
						else if(punteggio == sfidante.getPunteggio()) {
							int punteggioTot = punteggio + ConfigServer.Z;
							esito = Integer.toString(corrette) + " " + Integer.toString(sbagliate) + " "
							+ Integer.toString(noRisp) + " "+
							Integer.toString(punteggio) + " "+ Integer.toString(sfidante.getPunteggio()) 
							+ " pareggiato " + Integer.toString(ConfigServer.Z) + " " 
							+ Integer.toString(punteggioTot);
							
							d.changePunteggio(cmd[1], punteggioTot);

						}
						
						outToClient.write(esito);
						outToClient.newLine();
						outToClient.flush();	

					}
					
				}
			}
		}
		catch (Exception e) { System.out.println("Client disconnesso");} 
	}	

	//metodo privato utilizzato per prelevare le k parole dal dizionario
	private	List<String> listaParole(){
		Random r = new Random();
		List<String> parIt = new ArrayList<>();
		int contatore = 0;
		int numDiz = ConfigServer.N;
		String parola;
		try (BufferedReader br =
				new BufferedReader(new FileReader(ConfigServer.pathDizionario))) {
			while((parola=br.readLine())!=null)  {  			
				//aggiungere fino ad arrivare k parole,le parole saranno scelte in base ad una probabilità(1/4)
				//se le parole rimanenti da scegliere sono uguali alle parole che ci occorrono si aggiungono
				//senza probabilità
				if(contatore < ConfigServer.K && (numDiz <= (ConfigServer.K-contatore) || r.nextInt(4) == 0)) {
					parIt.add(parola);
					contatore++;
				}
				numDiz--;
			} 
		}
		catch(Exception e) {System.out.println("errore dizionario");}
		return parIt;
	}
	
	//metodo che presa la parola in italiano, restituisce, se possibile, la traduzione
	public String traduzione(String parola) {
		String parolaTradotta = "No traduzione";
		try {
			//utiliziamo il servizio esterno per la traduzione
			String link = "https://api.mymemory.translated.net/get?q=" + parola + "&langpair=it|en";
			URL url=new URL(link);
			URLConnection uc=url.openConnection();
			uc.connect();
			BufferedReader in=new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String line=null;
			StringBuffer sb=new StringBuffer();
			while((line=in.readLine())!=null)
				sb.append(line);

			//convertiamo la stringa nel json e troviamo la traduzione
			JSONObject jsonob=(JSONObject) new JSONParser().parse(sb.toString());
			parolaTradotta = ((JSONObject) jsonob.get("responseData")).get("translatedText").toString();

		}
		//se c'è qualche errore allora scrivera no traduzione
		catch (Exception e) {}
		//restituisce la traduzione oppure la parola no traduzione se non è stato possibile tradurla
		return parolaTradotta;
	}
						
}
		
