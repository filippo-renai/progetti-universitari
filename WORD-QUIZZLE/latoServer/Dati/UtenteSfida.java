package Dati;
import java.util.List;

//struttura d'appoggio per OnlineUtenti per ricordarsi la sessione di gioco dell'utente e la relativa porta dove 
//mandare la richiesta d'invito
public class UtenteSfida {
	private int portUDP; //saprò a chi mandare la richiesta di sfida 
	private int punteggio; //punteggio fine partita
	private boolean finePartita;
	private List<String> listParole; //le parole che dovrò tradurre nella lista di gioco
	
	public UtenteSfida(int portUDP,List<String> parole) {
		this.portUDP = portUDP;
		nuovaPartita(parole);
	}
	
	public int getPorta() {
		return portUDP;
	}
	public int getPunteggio() {
		return punteggio;
	}
	
	public void partitaFin(int puntFinale) {
		punteggio = puntFinale;
		finePartita = true;
	}
	
	public void nuovaPartita(List<String> parole) {
		punteggio = 0;
		finePartita = false;
		listParole = parole;
	}
	
	public boolean isFinish() {
		return finePartita;
	}
	
	//restituisce le parole che dovrà tradurre
	public List<String> getParole(){
		return listParole;
	}
}
