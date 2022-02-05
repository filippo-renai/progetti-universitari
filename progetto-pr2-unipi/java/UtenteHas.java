import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class UtenteHas<E> { //la classe utente usata dall'implementazione con hashtable
	private String passw;
	private List<E> fPoss; //lista dei file posseduti dall'utente
	private List<E> fLett; //lista dei file che l'utente puo' solo leggere
	private List<E> fScritt; //lista dei file che l'utente puo' sia leggere che scrivere
	
	public UtenteHas(String passw) {
		this.passw = passw;
		fPoss = new ArrayList<>();
		fLett = new ArrayList<>();
		fScritt = new ArrayList<>();
	}
	
	public void insPoss(E file) { //aggiungo alla lista dei file posseduti dall'utente
		fPoss.add(file);
	}
	
	public void insLett(E file) { //aggiungo alla lista dei file di sola lettura
		fLett.add(file);
	}
	
	public void insScritt(E file) { //aggiungo alla lista dei file di lettura e scrittura
		fScritt.add(file);
	}
	
   public String getPassw(){ 
      return passw;
   }
  
   public int getSize(){ //la dimensione degli oggenti dell'utente
      return fPoss.size();
   }
   
   public boolean cercaPoss(E file) { //cerca se posseggo questo file
      int i=0; boolean findObj = false; //boolean per dire se ha trovato l'oggetto
      
      while(i<fPoss.size() && !findObj){
         if(fPoss.get(i).equals(file)) findObj = true; //oggetto trovato
         i++;
      }
      
      return findObj;    
   	
   }
   
   public boolean dirLett(E file) { //cerca se ho il diritto di lettura per questo file
   	if(this.dirScritt(file)) return true;
      int i=0; boolean findObj = false; //boolean per dire se ha trovato l'oggetto
      
      while(i<fLett.size() && !findObj){
         if(fLett.get(i).equals(file)) findObj = true; //oggetto trovato
         i++;
      }
      
      return findObj;  
   	
   }
   
   public boolean dirScritt(E file) { //cerca se ho il diritto di lettura e scrittura per questo file
   	if(this.cercaPoss(file)) return true;
      int i=0; boolean findObj = false; //boolean per dire se ha trovato l'oggetto
      
      while(i<fScritt.size() && !findObj){
         if(fScritt.get(i).equals(file)) findObj = true; //oggetto trovato
         i++;
      }
      
      return findObj;  
   }
   
   public Iterator<E> getIter(){
      return fPoss.iterator();
   }
   
   public boolean removeFile(E file){ 
		if (!this.cercaPoss(file)) // oggetto non trovato
			return false;

		fPoss.remove(file); // oggetto rimosso e ritorno true
		return true;
   }
}
