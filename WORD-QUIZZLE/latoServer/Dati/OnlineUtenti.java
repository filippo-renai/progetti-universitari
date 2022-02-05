package Dati;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//classe che gestisce gli utenti online 
public class OnlineUtenti {
	//per problemi di concorrenza
	private ConcurrentHashMap<String,UtenteSfida> d;
	
	public OnlineUtenti() {
		d = new ConcurrentHashMap<>();
	}
	
	public void addOnline(String name,int port) {
		UtenteSfida u = new UtenteSfida(port,null);
		d.put(name, u);
	}
	
	public boolean isOnline(String name) {
		return d.containsKey(name);
	}
	
	public void offline(String name) {
		d.remove(name);
	}
	
	public UtenteSfida utenteSfida(String name) {
		return d.get(name);
	}
	
	//porta udp dove la socket udp del client è in ascolto
	public int getPorta(String name) {
		return d.get(name).getPorta();
	}
	
	public void nuovaPartita(String name,List<String> parole) {
		d.get(name).nuovaPartita(parole);
	}
	
}
