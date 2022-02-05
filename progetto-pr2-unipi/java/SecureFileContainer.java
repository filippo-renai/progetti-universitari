import java.util.ArrayList;
import java.util.Iterator;
import eccezioni.IdErrorException;
import eccezioni.PasswWrongException;
import eccezioni.FileNotFoundException;

public interface SecureFileContainer<E> {
   /*
   	OVERVIEW: è un contenitore di oggetti di tipo E.

   	TYPICAL ELEMENT: 
   		{ <Id_0,passw_0,poss_0,lett_0,scritt_0>,..., 
   			  <Id_{dim-1},passw_{dim-1},poss_{dim-1},lett_{dim-1},scritt_{dim-1}> }
   		
     		dove Id_i != null,passw_i != null,
     		poss_i = poss_i_0, ..., poss_i_dimp_i-1 con poss_i_j file E posseduti dall'utente,
     		lett_i = lett_i_0, ..., lett_i_diml_i-1 con lett_i_j file E con diritto di lettura,
     		scritt_i = scritt_i_0, ..., scritt_i_dims_i-1 con scritt_i_j file E con diritto di lettura e scrittura
     		    
   */
	
   /** 
    * Crea l'identità un nuovo utente della collezione
    * 
    * @requires: Id != null && passw != null && forall i. 0<= i < dim -> Id != Id_i 
    * @modifies: this
    * @throws: se Id = null || passw = null lancia NullPointerException (eccezionein Java, unchecked)
    *          se exists i. 0<=i<dim -> Id = Id_i lancia IdErrorException
    *          (mia eccezione,checked)
    * @effects: aggiunge Id alla collezione
    */
	public void createUser(String Id, String passw) throws IdErrorException;
	

   /** 
    * Restituisce il numero dei file di un utente presenti nella collezione  
    * 
    * @requires: Owner != null && passw != null && exists i. 0<= i <dim -> Owner = Id_i &&  passw = passw_i
    * @throws: se Owner = null || passw = null lancia NullPointerException (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    * @effects: ritorna il numero di file posseduti da Owner
    */
	public int getSize(String Owner, String passw) throws IdErrorException,PasswWrongException;

   /** 
    * Inserisce il file nella collezione se vengono rispettati i controlli di identità
    * @requires: Owner != null && passw != null && file != null 
    * @modifies: this
    * @throws: se Owner = null || passw = null || file = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    * @effects: se exists i. 0<= i < dim -> Owner != Id_i 
    * 				 && passw = passw_i -> si aggiunge un file nella lista dei file posseduti da Owner
    *           altrimenti -> non inserisce il file e restituisce false
    */  
	public boolean put(String Owner, String passw, E file);

   /** 
    * Ottiene una copia del file nella collezione
	 * se vengono rispettati i controlli di identità
    * 
    * @requires: Owner != null && passw != null && file != null 
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    *           && exists j. 0<= j <dim_i -> file = poss_i_j
    * @throws: se Owner = null || passw = null || file = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    *          se forall j. 0<= j <dim_i -> file != poss_i_j lancia FileNotFoundException 
    *             (mia eccezione,checked)
    * @effects: ottiene la copia del file posseduto da Owner
   */ 
	public E get(String Owner, String passw, E file) 
			throws IdErrorException,PasswWrongException,FileNotFoundException;

	/** 
    * Rimuove il file dalla collezione
	 * se vengono rispettati i controlli di identità
    * 
    * @requires: Owner != null && passw != null && file != null 
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    *           && exists j. 0<= j <dim_i -> file = poss_i_j
    * @throws: se Owner = null || passw = null || file = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    *          se forall j. 0<= j <dim_i -> file != poss_i_j lancia FileNotFoundException 
    *             (mia eccezione,checked)
    * @effects: ottiene la copia del file posseduto da Owner ed lo elimina dalla lista dei file
    * 			 posseduti
   */ 
	public E remove(String Owner, String passw, E file) 
			throws IdErrorException,PasswWrongException,FileNotFoundException;
 

   /** 
	 * Crea una copia del file nella collezione
	 * se vengono rispettati i controlli di identità
    * 
    * @requires: Owner != null && passw != null && file != null 
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    *           && exists j. 0<= j <dim_i -> file = poss_i_j
    * @throws: se Owner = null || passw = null || file = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    *          se forall j. 0<= j <dim_i -> file != poss_i_j lancia FileNotFoundException 
    *             (mia eccezione,checked)
    * @effects: fa una copia del file posseduto e lo aggiunge alla lista dei file posseduti da Owner
   */ 
	public void copy(String Owner, String passw, E file) 
			throws IdErrorException,PasswWrongException,FileNotFoundException;

	
   /** 
    * Condivide in lettura il file nella collezione con un altro utente
	 * se vengono rispettati i controlli di identità
    * 
    * @requires: Owner != null && pass != null && Other != null && file != null 
    *           && Owner != Other
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    *           && exists k. 0<= k <dim -> (Other =  Id_k && i != k) 
    *           && exists j. 0<= j <dim_i -> file = poss_i_j
    * @modifies: this
    * @throws: se Owner = null || passw = null || Other = null | data = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i || Owner = Other
    *              lancia IdErrorException (mia eccezione,checked) 
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    *          se forall j. 0<= j <dim_i -> file != poss_i_j lancia FileNotFoundException 
    *             (mia eccezione,checked)
    * @effects: aggiunge un file posseduto da Owner nella lista di file con diritti di lettura di Other
    */
	public void shareR(String Owner, String passw, String Other, E file)
			throws IdErrorException,PasswWrongException,FileNotFoundException;

   /** 
    * Condivide in lettura e scritturail file nella collezione con un altro utente
	 * se vengono rispettati i controlli di identità
    * 
    * @requires: Owner != null && pass != null && Other != null && file != null 
    *           && Owner != Other
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    *           && exists k. 0<= k <dim -> (Other =  Id_k && i != k) 
    *           && exists j. 0<= j <dim_i -> file = poss_i_j
    * @modifies: this
    * @throws: se Owner = null || passw = null || Other = null | data = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i || Owner = Other
    *              lancia IdErrorException (mia eccezione,checked) 
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    *          se forall j. 0<= j <dim_i -> file != poss_i_j lancia FileNotFoundException 
    *             (mia eccezione,checked)
    * @effects: aggiunge un file posseduto da Owner nella lista di file con diritti di lettura
    * 			 e scrittura di Other
    */
	public void shareW(String Owner, String passw, String Other, E file)
			throws IdErrorException,PasswWrongException,FileNotFoundException;

   /**
    * restituisce un iteratore (senza remove) che genera tutti i file dell'utente in ordine arbitrario
    * se vengono rispettati i controlli di identità 
    * 
    * @requires: Owner != null && pass != null
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    * @throws: se Owner = null || passw = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i lancia PasswWrongException (mia eccezione,checked)
    * @effects: restituisce l'iteratore dei file posseduti da Owner
   */
	public Iterator<E> getIterator(String Owner, String passw)
			throws IdErrorException,PasswWrongException;

	// … altre operazione da definire a scelta
	
   /** 
    * Rimuove l'utente dalla collezione    
    * 
    * @requires: Id != null && passw != null && exists i. 0<= i <dim -> Id =  Id_i &&  passw = passw_i
    * @modifies: this
    * @throws: se Id = null || passw = null lancia NullPointerException (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Id != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i lancia PasswWrongException (mia eccezione,checked)
    * @effects: rimuove l'utente Id dalla collezione
    */
   public void removeUser(String Id, String passw) throws IdErrorException,PasswWrongException;
	
   /** 
	 * Legge il file dell'utente se sono rispettati tutti i controlli di identità e di diritti
    * 
    * @requires: Id != null && pass != null && file != null 
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    *           && exists j. 0<= j <dim_i -> (file = poss_i_j || file = lett_i_j || file = scritt_i_j)
    * @throws: se Owner = null || passw = null || file = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    *          se forall j. 0<= j <dim_i -> (file != poss_i_j && file != lett_i_j && file != scritt_i_j)
    *           lancia FileNotFoundException 
    *             (mia eccezione,checked)
    * @effects: legge il file 
   */ 
   public void readF(String Id, String passw,E file)
			 throws IdErrorException,PasswWrongException,FileNotFoundException;
	
   /** 
	 * Scrive nel file dell'utente se sono rispettati tutti i controlli di identità e di diritti
    * 
    * @requires: Id != null && pass != null && file != null && testo != null
    *           && exists i. 0<= i <dim -> Owner =  Id_i &&  passw = passw_i
    *           && exists j. 0<= j <dim_i -> (file = poss_i_j || file = scritt_i_j)
    * @throws: se Owner = null || passw = null || file = null || testo = null lancia NullPointerException 
    *              (eccezione in Java, unchecked)
    *          se forall i. 0<= i < dim -> Owner != Id_i lancia IdErrorException (mia eccezione,checked)
    *          se passw != passw_i non associata ad Owner lancia PasswWrongException (mia eccezione,checked)
    *          se forall j. 0<= j <dim_i -> (file != poss_i_j && file != scritt_i_j)
    *           lancia FileNotFoundException 
    *             (mia eccezione,checked)
    * @effects: Scrive nel file il testo 
   */ 
	public void writeF(String Id, String passw,E file,ArrayList<String> testo)
			 throws IdErrorException,PasswWrongException,FileNotFoundException;
	

}