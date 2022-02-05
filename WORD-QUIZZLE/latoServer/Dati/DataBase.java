package Dati;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//classe che struttura l'intero database e si preoccupa della sua persistenza
public class DataBase {
	//hashmap dove la chiave � una stringa ovvero il nickname dell'utente
	//il tipo � una classe di appoggio che ricorda le sue informazioni	
	private ConcurrentHashMap<String,UtenteApp> d;
	
	//il costruttore prende il path del database json e se esiste e ha informazioni le inserisco
	public DataBase(String pathJson) {
		d = new ConcurrentHashMap<>();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(pathJson));
			JSONObject jObject = (JSONObject) obj;
			Set entrySet = jObject.keySet();
			Iterator it = entrySet.iterator();
			while(it.hasNext()){
				String key = (String) it.next();
				popJson(key, jObject);
		   }
		}
		//se non trovo il file semplicemente non faccio nulla
		catch (FileNotFoundException e) {}
		catch (IOException e) { System.out.printf("problema con la persistenza del database\n\n"); }
		catch (ParseException e) { System.out.printf("problema con il formato json\n\n");}
	}
	
	//aggiunge l'utente se c'� qualche problema restituisce 
	//0 se o manca qualche campo,1 se l'utente gi� registrato,2 utente registrato
	public int addUtente(String nome,String passw,int punteggio) {
		//si verifica un errore
		if(nome == null || passw == null) return 0;
		//se l'utente � gi� presente 
		if(d.containsKey(nome)) return 1;
		
		UtenteApp ut = new UtenteApp(passw,punteggio);
		d.put(nome, ut);
		//nuovo utente registrato
		return 2;
	}
	
	//metodo per vedere se questo utente � registrato
	public boolean isReg(String utente) {
		return d.containsKey(utente);
	}
	
	//aggiungi un amico all'utente restituisce false se ci sono problemi
	//altrimenti true
	public boolean addAmico(String utente,String amico) {
		//errore nel caso in cui l'amico o l'utente non � presente nel servizio
		if(!d.containsKey(utente) || !d.containsKey(amico)) return false;
		//aggiunge l'amico
		d.get(utente).addAmico(amico);
		//l'amico aggiunge l'utente
		d.get(amico).addAmico(utente);
		return true;
	}
	
	//login dell'utente se credenziali sono giuste
	//restituisce un codice in base all'evento
	//0 utente manca,1 password sbagliata
	public String loginUtente(String utente,String passw) {
		if(!d.containsKey(utente)) return "0";
		//controllo che sia la stessa password
		if(!d.get(utente).getPassw().equals(passw)) return "1";
		return "2";
	}
	
	//restituisco la lista amici dell'utente se l'utente non � presente restituisco null
	public List<String> listAmici(String utente){
		if(!d.containsKey(utente)) return null;
		return d.get(utente).getListaAmici();	
	}
	
	//restituisco il punteggio dell'utente se l'utente non � presente restitusco -1
	public int getPunteggio(String utente) {
		if(!d.containsKey(utente)) return -1;
		return d.get(utente).getPunteggio();
	}
	
	public boolean changePunteggio(String utente,int punteggio) {
		if(!d.containsKey(utente)) return false;
		d.get(utente).changePunt(punteggio);
		return true;
	}
	
	//mostra la classifica del migliore punteggio dell'utente e dei suoi amici
	//se l'utente non ha amici allora � l'unico nella classifica
	public List<String> mostraClass(String utente){
		if(!d.containsKey(utente)) return null;
		//prendo tutti gli amici 
		List<String> classifica = new ArrayList<>();
		List<String> amici = d.get(utente).getListaAmici();
		List<Integer> puntAmici = new ArrayList<>();
		//per la classifica ovviamente conto anche l'utente stesso
		amici.add(utente);
		//ora segno tutti i punteggi degli amici
		for(String a :amici) 
			puntAmici.add(new Integer(d.get(a).getPunteggio()));
		//ora i punteggi sono in ordine decrescente
		Collections.sort(puntAmici,Collections.reverseOrder());
		
		//ora in base al punteggio ordinato vedo se � associato alla persona 
		//se si allora la metto nella lista in maniera ordinata per i punteggi
		
		for(Integer p: puntAmici) {
			for(String a : amici) {	
				//l'utente ha lo stesso punteggio allora lo metto nella classifica,solo se non � gi�
				//nella classifica
				if(p.intValue() == d.get(a).getPunteggio() && !classifica.contains(a)) {
					classifica.add(a);
					break;
				}
			}
		}
		//problema di aliasing quindi tolgo se stesso dalla lista degli amici
		amici.remove(utente);
		return classifica;
	}
	
	//restituisce se utente e l'altra persona sono amici 
	//se si restituisce true altrimenti false
	public boolean isAmico(String utente,String amico) {
		if(d.get(utente).isAmico(amico)) return true;
		else return false;
	}
	
	//trascrive il database nel file json per avere una persistenza nel servizio
	public void persJson(String pathJson) {
		Set entrySet = d.entrySet();
		Iterator it = entrySet.iterator();
		JSONObject obj = new JSONObject ();
		while(it.hasNext()){
	       Map.Entry me = (Map.Entry)it.next();
	       String key = (String) me.getKey();
	       obj.put(key, d.get(key).toJson());
	   }
		//ora che ho il file json lo trascrivo nel file
		try {
			File file=new File(pathJson);
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(obj.toJSONString());
			fileWriter.flush();
			fileWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//metodo privato utilizzato quando avvio il server e carico tutte le informazioni del  
	//singolo utente nel json
	private void popJson(String key,JSONObject obj) {
		JSONArray array = (JSONArray) obj.get(key);
		String passw = (String) array.get(0);
		Number punteggio = (Number) array.get(1);
		addUtente(key,passw,punteggio.intValue());
		
		for(int i=2;i<array.size();i++)
			d.get(key).addAmico((String) array.get(i));		
	}
	
}
