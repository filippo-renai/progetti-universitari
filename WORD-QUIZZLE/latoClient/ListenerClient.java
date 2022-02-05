import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

//Thread Listener che si mette in attesa di inviti da parte di altri utenti inviati dal server via udp
public class ListenerClient extends Thread {
	private DatagramSocket clientSock; //dove verranno mandate le richieste
	private SupportClient sup; //appena accettero' la sfida chiamer� il metodo dedicato alla sfida di questa classe
	//booleano se l'utente sta giocando,comprende il caso in cui il thread listener ha richiamato la funzione di sfida
	private boolean inGame2; 
	//solo per vedere se fosse arrivata la richesta
	private boolean pacArriv;
	
	public ListenerClient(int port,SupportClient sup) {
		pacArriv = false;
		inGame2 = false;
		this.sup = sup;
		try {
			clientSock = new DatagramSocket(port);
			//setto un timer utilizzato nel caso in cui io accetto la sfida ma il pacchetto 
			//si perde quindi voglio la risposta di ritorno
			clientSock.setSoTimeout(ConfigClient.T1);
		} 
		catch (SocketException e) {System.out.printf("Errore udp client\n\n");}
	}
	
	public void run() {
		//ipotizzo che gli username non siano lunghissimi
		byte[] buffer = new byte[1000];
		DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
		try {
			while(true){
				try { 
					clientSock.receive(receivedPacket); 
					pacArriv = true;
				}
				catch(Exception e) {pacArriv = false;} //stadenza ricezione pacchetto
				if(pacArriv) {
					//sta gia'giocando,semplicemente non mando la richiesta di sfida, mentre sta giocando
					if(SupportClient.inGame || inGame2) { inGame2 = false;} 
					//puo' giocare
					else {
						long sTime = System.currentTimeMillis();
						String richiesta = new String(receivedPacket.getData(), 0, receivedPacket.getLength(), "US-ASCII");
						String rispPrec = MainClassWQClient.comando;
						System.out.printf("Utente " + richiesta + " ti sfida!\nAccetti [si/no]:\n\n");
						MainClassWQClient.occupato = true; //voglio l'input per la sfida
						String risposta;
						//ciclo per vedere se l'utente ha risposto alla richiesta di sfida
						while ((risposta = MainClassWQClient.comando).equals(rispPrec)) 
							Thread.sleep(800);
						
						//troppo tempo per rispondere
						if((System.currentTimeMillis() - sTime) >= ConfigClient.T1) { 
							MainClassWQClient.occupato = false;
							Thread.sleep(10);
							MainClassWQClient.comando = "finito";
							System.out.printf("Troppo tempo per rispondere!\n\n");
						}
						
						else {
							byte[] buffer2 = risposta.getBytes("US-ASCII");
							InetAddress address = InetAddress.getByName(ConfigClient.HOSTNAME_SERVER);
							DatagramPacket mypacket = new DatagramPacket(buffer2, buffer2.length, address, ConfigClient.portServerUDP);
							clientSock.send(mypacket);

							if(risposta.equals("si")) {
								try { 
									//aspetto la risposta che mi conferma che il pacchetto non è andato perduto
									clientSock.receive(receivedPacket); 
									inGame2 = true;
									//in modo che la lista delle parole venga inserita prima della sfida
									Thread.sleep(500); 
									sup.game(richiesta);						
								}
								catch(Exception e) {		
									MainClassWQClient.occupato = false;
									Thread.sleep(10); //solo per essere sicuro della sincronia
									MainClassWQClient.comando = "finito";
									System.out.printf("Risposta perduta!\n\n");
								} 
							}
							else {
								MainClassWQClient.occupato = false;
								Thread.sleep(10); //solo per essere sicuro della sincronia
								MainClassWQClient.comando = "finito";
								System.out.printf("Sfida rifiutata!\n\n");
							}
						}
					}
				}
			}
		}
		catch(Exception ex) {System.out.printf("Errore udp client\n\n");}
	}

}
