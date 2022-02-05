package Dati;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;

//classe d'appoggio per il database dove sono segnate tutte le informazioni utili del singolo utente
public class UtenteApp{
	private String passw;
	private int punteggio;
	//la lista degli amici è semplicemente solo il nome dell'amico
	private List<String> listaAmici;
	
	public UtenteApp(String passw,int punteggio){
		this.passw = passw;
		this.punteggio = punteggio;
		listaAmici = new ArrayList<>();
	}
	
	public int getPunteggio() {
		return punteggio;
	}
	
	public String getPassw() {
		return passw;
	}
	
	public List<String> getListaAmici(){
		return listaAmici;
	}
	
	//restituisce true se è suo amico 
	//altrimente false
	public boolean isAmico(String amico) {
		for(String a : listaAmici)
			if(a.equals(amico)) return true;
		return false;
	}
	
	
	//se l'amico è già presente,non sollevo una eccezione,ma semplicemente non lo aggiungo
	public void addAmico(String amico) {
		for(String am : listaAmici) 
			if(amico.equals(am)) return;
		//se siamo arrivati a questo punto l'utente non è ancora amico e l'aggiungo
		listaAmici.add(amico);
	}
	
	public void changePunt(int punt) {
		punteggio += punt;
	}
	
	
	/*
	 	converte l'oggetto utenteapp un oggetto json
		un array dove i primi due elementi sono passw e punteggio e in seguito i suoi amici 
		la chiava nickname sarà aggiunta nel database
	*/
	public JSONArray toJson() {
		JSONArray array = new JSONArray();
		array.add(passw);
		array.add(new Integer(punteggio));
		//aggiungo tutti gli amici nell'array
		for(String amico : listaAmici) 
			array.add(amico);
		
		return array;
		
	}
}
