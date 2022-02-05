import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eccezioni.FileNotFoundException;
import eccezioni.IdErrorException;
import eccezioni.PasswWrongException;

public class ArrayListImpl<E> implements SecureFileContainer<E> {
   /*
      funzione d'astrazione: 
          a(utenti) = utenti.get(0), ..., utenti.get(utenti.size()-1) 
    
      invariante di rappresentazione:
          utenti != null && forall i. 0 <= i < utenti.size() ==> ( utenti.get(i) != null &&
          			utenti.get(i).getId() != null && utenti.get(i).getPassw() != null )

   */
	
	List<UtenteAr> utenti;
	
	public ArrayListImpl() { //costruttore
		utenti = new ArrayList<>();
	}
	
	@Override
	public void createUser(String Id, String passw) throws IdErrorException { 
		// eccezione se Id = null oppure passw = null
		if (Id == null || passw == null) throw new NullPointerException();
		// vedo se esiste un utente con lo stesso id di quello che voglio creare
		boolean verUtente = false; // se true allora l'utente e' presente nella collezione
		int i = 0;
		while (i < utenti.size() && !verUtente) {
			if (utenti.get(i).getId().equals(Id)) verUtente = true;
			else i++;
		}
		
		// utente trovato lancio la mia eccezione
		if (verUtente) throw new IdErrorException("utente " + Id + " gia' presente nella collezione");

		UtenteAr u = new UtenteAr(Id, passw);
		utenti.add(u);
		
	}

	@Override
   public int getSize(String Owner, String passw) throws IdErrorException,PasswWrongException{ 
		// eccezione se Owner = null oppure passw = null
		if (Owner == null || passw == null) throw new NullPointerException();

		int i = 0;
		boolean verUtente = false; // se true allora l'utente e' presente nella collezione

		// ciclo per controllare se l'utente e' presente nella collezione
		while (i < utenti.size() && !verUtente) {
			if (utenti.get(i).getId().equals(Owner))
				verUtente = true;
			else
				i++;
		}
		if (!verUtente) // utente non trovato lancio la mia eccezione
			throw new IdErrorException("utente " + Owner + " non e' presente nella collezione");

		if (!utenti.get(i).getPassw().equals(passw)) // la password non e' corretta lancio eccezione
			throw new PasswWrongException("la password dell'utente " + Owner + " non e' sbagliata");

		// utente trovato e password trovata ritorno la dimensione dei suoi oggetti
		return utenti.get(i).getSize();
  }

	@Override
	public boolean put(String Owner, String passw, E file) { //OK
		if (Owner == null || passw == null || file == null)
			throw new NullPointerException();

		int i = 0;
		boolean verUtente = false; // se true allora l'utente e' presente nella collezione

		// ciclo per controllare se l'utente e' presente nella collezione
		while (i < utenti.size() && !verUtente) {
			if (utenti.get(i).getId().equals(Owner)) verUtente = true;
			else i++;
		}

		// utente non trovato o password sbagliata operazione non possibile ritorno
		// false
		if (!verUtente || !utenti.get(i).getPassw().equals(passw))
			return false;

		// aggiungo l'elemento nella lista della collezione
		utenti.get(i).insPoss(file);
		return true; // operazione e' andata a buon fine
	}

	@Override
	public E get(String Owner, String passw, E file) throws IdErrorException,PasswWrongException,FileNotFoundException{
		if (Owner == null || passw == null || file == null) 
			throw new NullPointerException();

		int i = 0;
		boolean verUtente = false; // se true allora l'utente e' presente nella collezione

		// ciclo per controllare se l'utente e' presente nella collezione
		while (i < utenti.size() && !verUtente) {
			if (utenti.get(i).getId().equals(Owner))
				verUtente = true;
			else
				i++;
		}
		if (!verUtente) // utente non trovato lancio la mia eccezione
			throw new IdErrorException("utente " + Owner + " non e' presente nella collezione");

		if (!utenti.get(i).getPassw().equals(passw)) // la password non e' corretta lancio eccezione
			throw new PasswWrongException("la password dell'utente " + Owner + " non e' sbagliata");

		if (utenti.get(i).cercaPoss(file)) { // oggetto appartiene all'utente e lo restituisco
			return file;
		} else // file non appartiene all'utente lancio eccezione
			throw new FileNotFoundException("il file " + file.toString() + " non presente nella " + "collezione dell'utente " + Owner);
	}

	@Override
	public E remove(String Owner, String passw, E file) 
         throws IdErrorException,PasswWrongException,FileNotFoundException{
    	
		if (Owner == null || passw == null || file == null)
			throw new NullPointerException();

		int i = 0;
		boolean verUtente = false; // se true allora l'utente e' presente nella collezione

		// ciclo per controllare se l'utente e' presente nella collezione
		while (i < utenti.size() && !verUtente) {
			if (utenti.get(i).getId().equals(Owner)) verUtente = true;
			else i++;
		}
		if (!verUtente) // utente non trovato lancio la mia eccezione
			throw new IdErrorException("utente " + Owner + " non e' presente nella collezione");

		if (!utenti.get(i).getPassw().equals(passw)) // la password non e' corretta lancio eccezione
			throw new PasswWrongException("la password dell'utente " + Owner + " non e' sbagliata");

		if (utenti.get(i).removeFile(file)) // file trovato ed eliminato restituisce l'oggetto
			return file;
		else // file non trovato lancia eccezione
			throw new FileNotFoundException("il file " + file.toString() + " non presente nella " + "collezione dell'utente " + Owner);

	}

	@Override
	public void copy(String Owner, String passw, E file) 
         throws IdErrorException,PasswWrongException,FileNotFoundException{

		if (Owner == null || passw == null || file == null)
			throw new NullPointerException();

		int i = 0;
		boolean verUtente = false; // se true allora l'utente e' presente nella collezione

		// ciclo per controllare se l'utente e' presente nella collezione
		while (i < utenti.size() && !verUtente) {
			if (utenti.get(i).getId().equals(Owner))
				verUtente = true;
			else
				i++;
		}
		if (!verUtente) // utente non trovato lancio la mia eccezione
			throw new IdErrorException("utente " + Owner + " non e' presente nella collezione");

		if (!utenti.get(i).getPassw().equals(passw)) // la password non e' corretta lancio eccezione
			throw new PasswWrongException("la password dell'utente " + Owner + " non e' sbagliata");

		if (!utenti.get(i).cercaPoss(file)) // file non presente nella collezione dell'utente
			throw new FileNotFoundException("il file " + file.toString() +" non presente nella " + "collezione dell'utente " + Owner);
		else // file presente allora lo copio nella collezione dell'utente
			utenti.get(i).insPoss(file);
		
	}

	@Override
	public void shareR(String Owner, String passw, String Other, E file) 
         throws IdErrorException,PasswWrongException,FileNotFoundException{

     //eccezione se Owner = null oppure passw = null oppure Other
     if( Owner == null || passw == null || Other == null  || file == null) throw new NullPointerException();
     
     //controllare se l'utente che condivide e' lo stesso che condivide,se si lancio eccezione
     if(Owner.equals(Other))
         throw new IdErrorException("l'utente (" + Owner +") che condivide e' lo stesso che riceve");

     int i=0,j=0; 
     boolean verUtente = false; 
     
     //ciclo per controllare se utente che condivide e' presente nella collezione
     while(i< utenti.size() && !verUtente){            
         if(utenti.get(i).getId().equals(Owner)) verUtente = true;  
         else i++;
     }
     if(!verUtente) //utente non trovato lancio la mia eccezione
         throw new IdErrorException("utente " + Owner + " non e' presente nella collezione");
     
     //ciclo per controllare se utente che riceve e' presente nella collezione 
     verUtente = false;
     while(j< utenti.size() && !verUtente){            
         if(utenti.get(j).getId().equals(Other) && j != i) verUtente = true;  
         else j++;
     }
     if(!verUtente) //utente non trovato lancio la mia eccezione
         throw new IdErrorException("utente " + Other + " non e' presente nella collezione");

     if(!utenti.get(i).getPassw().equals(passw)) //la password non e' corretta lancio eccezione
         throw new PasswWrongException("la password dell'utente " + Owner + " non e' sbagliata");
     
     if(!utenti.get(i).cercaPoss(file)) //file non presente nella collezione dell'utente
         throw new FileNotFoundException("file " + file.toString() + " non presente nella "
                 + "collezione dell'utente " + Owner);
     else 
         utenti.get(j).insLett(file); //aggiungo il file nella lista dei file in lettura
		
	}

	@Override
	public void shareW(String Owner, String passw, String Other, E file) 
         throws IdErrorException,PasswWrongException,FileNotFoundException{

     //eccezione se Owner = null oppure passw = null oppure Other
     if( Owner == null || passw == null || Other == null  || file == null) throw new NullPointerException();
     
     //controllare se l'utente che condivide e' lo stesso che condivide,se si lancio eccezione
     if(Owner.equals(Other))
         throw new IdErrorException("l'utente (" + Owner +") che condivide e' lo stesso che riceve");

     int i=0,j=0; 
     boolean verUtente = false; 
     
     //ciclo per controllare se utente che condivide e' presente nella collezione
     while(i< utenti.size() && !verUtente){            
         if(utenti.get(i).getId().equals(Owner)) verUtente = true;  
         else i++;
     }
     if(!verUtente) //utente non trovato lancio la mia eccezione
         throw new IdErrorException("utente " + Owner + " non e' presente nella collezione");
     
     //ciclo per controllare se utente che riceve e' presente nella collezione 
     verUtente = false;
     while(j< utenti.size() && !verUtente){            
         if(utenti.get(j).getId().equals(Other) && j != i) verUtente = true;  
         else j++;
     }
     if(!verUtente) //utente non trovato lancio la mia eccezione
         throw new IdErrorException("utente " + Other + " non e' presente nella collezione");

     if(!utenti.get(i).getPassw().equals(passw)) //la password non e' corretta lancio eccezione
         throw new PasswWrongException("la password dell'utente " + Owner + " non e' sbagliata");
     
     if(!utenti.get(i).cercaPoss(file)) //file non presente nella collezione dell'utente
         throw new FileNotFoundException("file " + file.toString() + " non presente nella "
               + "collezione dell'utente " + Owner);
     else 
         utenti.get(j).insScritt(file); //aggiungo il file nella lista dei file sia in lettura che in scrittura
		
		
	}

	@Override
	public Iterator<E> getIterator(String Owner, String passw) 
      throws IdErrorException,PasswWrongException{
		// eccezione se Owner = null oppure passw = null oppure Other
		if (Owner == null || passw == null)
			throw new NullPointerException();

		int i = 0;
		boolean verUtente = false; // se true allora l'utente e' presente nella collezione

		// ciclo per controllare se l'utente e' presente nella collezione
		while (i < utenti.size() && !verUtente) {
			if (utenti.get(i).getId().equals(Owner)) verUtente = true;
			else i++;
		}
		if (!verUtente) // utente non trovato lancio la mia eccezione
			throw new IdErrorException("utente " + Owner + " non e' presente nella collezione");

		if (!utenti.get(i).getPassw().equals(passw)) // la password non e' corretta lancio eccezione
			throw new PasswWrongException("la password dell'utente " + Owner + " non e' sbagliata");

		Iterator<E> itr = utenti.get(i).getIter();

		return itr;
	}
	
	//miei metodi!
	
	@Override
   public void removeUser(String Id, String passw) throws IdErrorException,PasswWrongException{
      //eccezione se Id = null oppure passw = null
      if( Id == null || passw == null) throw new NullPointerException();
      
      int i=0;
      boolean verUtente = false; //se true allora l'utente e' presente nella collezione
      
      //ciclo per controllare se l'utente e' presente nella collezione
      while(i< utenti.size() && !verUtente){
          if(utenti.get(i).getId().equals(Id))  verUtente = true;            
          else i++;
      }
      if(!verUtente) //utente non trovato lancio la mia eccezione
          throw new IdErrorException("l'utente " + Id + " non e' presente nella collezione");
      
      if(!utenti.get(i).getPassw().equals(passw)) //la password non e' corretta lancio eccezione
          throw new PasswWrongException("la password dell'utente " + Id + " non e' sbagliata");
      
      utenti.remove(i);
  }

	@Override
	public void readF(String Id, String passw, E file)
			throws IdErrorException,PasswWrongException,FileNotFoundException{
		if (Id == null || passw == null || file == null) throw new NullPointerException();
		
      int i=0;
      boolean verUtente = false; //se true allora l'utente e' presente nella collezione
      
      //ciclo per controllare se l'utente e' presente nella collezione
      while(i< utenti.size() && !verUtente){
          if(utenti.get(i).getId().equals(Id))  verUtente = true;            
          else i++;
      }
      if(!verUtente) //utente non trovato lancio la mia eccezione
          throw new IdErrorException("utente " + Id + " non e' presente nella collezione");
      
      if(!utenti.get(i).getPassw().equals(passw)) //la password non e' corretta lancio eccezione
          throw new PasswWrongException("la password dell'utente " + Id + " non e' sbagliata");
      
      //file non posseduto dall'utente e senza autorizzazione di lettura    
      if(!utenti.get(i).dirLett(file)) 
         throw new FileNotFoundException("l'utente " + Id +
               " non ha il diritto di lettura per il file " + file.toString());
      else{
      	try{this.lettura(file);}
      	catch(Exception e){throw new FileNotFoundException("file: " + file + " non presente");}
      }
	}

	@Override
	public void writeF(String Id, String passw, E file,ArrayList<String> testo) 
			throws IdErrorException,PasswWrongException,FileNotFoundException{
		
		if (Id == null || passw == null || file == null) throw new NullPointerException();
		
      int i=0;
      boolean verUtente = false; //se true allora l'utente e' presente nella collezione
      
      //ciclo per controllare se l'utente e' presente nella collezione
      while(i< utenti.size() && !verUtente){
          if(utenti.get(i).getId().equals(Id))  verUtente = true;            
          else i++;
      }
      if(!verUtente) //utente non trovato lancio la mia eccezione
          throw new IdErrorException("utente " + Id + " non e' presente nella collezione");
      
      if(!utenti.get(i).getPassw().equals(passw)) //la password non e' corretta lancio eccezione
          throw new PasswWrongException("la password dell'utente " + Id + " non e' sbagliata");
      
      //file non posseduto dall'utente e senza autorizzazione di scrittura    
      if(!utenti.get(i).dirScritt(file)) 
         throw new FileNotFoundException("l'utente " + Id +
               " non ha il diritto di scrittura per il file " + file.toString());
      else{
      	try{this.scrittura(file,testo);}
      	catch(Exception e){throw new FileNotFoundException("file: " + file + " non presente nel computer");}
      }
	}
	
	
	//metodi privati
	
	private void lettura(E file) throws FileNotFoundException { //stampa tutto il testo del file
		String path = "C:\\Users\\filip\\Desktop\\"; // mettere il percorso del file
		String nFile = (String) file; //nome del file (diamo per certo che E è String)
		try (FileChannel inChannel = FileChannel.open(Paths.get(path+nFile),
				StandardOpenOption.READ)) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
			int bytesRead;
			while ((bytesRead = inChannel.read(buffer)) != -1) {
				buffer.flip();
				while (buffer.hasRemaining()) {
					String t = StandardCharsets.UTF_8.decode(buffer).toString();
					System.out.println(t);
				}
				buffer.clear();
			}

		} 
		catch (IOException e) { throw new FileNotFoundException();} 
	}
	
	private void scrittura(E file,ArrayList<String> testo) throws FileNotFoundException{ //scrive nel file il testo
		
		String path = "C:\\Users\\filip\\Desktop\\"; // mettere il percorso del file
		String nFile = (String) file; //nome del file (diamo per certo che E è String)
		try (FileChannel inChannel = FileChannel.open(Paths.get(path+nFile),
				StandardOpenOption.WRITE)) {			
			for(int i=0;i<testo.size();i++) {
			    byte[] strBytes = testo.get(i).getBytes();
			    ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
			    buffer.put(strBytes);
			    buffer.flip();
			    inChannel.write(buffer);
			    buffer.flip();
			}

		}
		catch (IOException e) { throw new FileNotFoundException();} 
		
	}

}
