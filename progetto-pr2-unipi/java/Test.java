import java.util.ArrayList;
import java.util.Iterator;
import eccezioni.FileNotFoundException;
import eccezioni.IdErrorException;
import eccezioni.PasswWrongException;

public class Test {

    public static void main(String[] args) {
       String[] utenti = {"Marco97","LauraCosp96","Gersi96","Filippo96","Carmen95"};
       String[] password = {"segreto","infum","433","prog96","124"};
       String tmp;
       long sTime,fTime; //per vedere la differenza di velocita' delle due implementazioni

       /* TESTO PRIMA IMPLEMENTAZIONE (VECTOR) */
       sTime = System.currentTimeMillis();//inizio tempo
       SecureFileContainer<String> a = new ArrayListImpl<>();
       System.out.printf("\t\tPRIMA IMPLEMENTAZIONE ARRAYLIST\n\n");

    //test funzione createUser
        System.out.printf("Test createUser:\n");
        for(int i=0;i<5;i++){
            try{
                a.createUser(utenti[i],password[i]);
            }
            catch(IdErrorException e){
                System.out.printf("\t"+ e.getMessage() +"\n");
            }
        }
        //sbaglio aggiungendo un utente con un Id gia' usato da un utente presente nella collezione
        try{
            a.createUser(utenti[0],password[0]);
        }
        catch(IdErrorException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

    //test removeUser
        System.out.printf("Test removeUser:\n");
        try{
            a.removeUser(utenti[1],password[1]);
            System.out.printf("\tl'utente " + utenti[1] + " e' stato eliminato\n");
        }
        catch(IdErrorException | PasswWrongException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testare il comportamento
        try{
            a.removeUser(utenti[1],password[1]);
            System.out.printf("\tl'utente " + utenti[1] + " e' stato eliminato\n");
        }
        catch(IdErrorException | PasswWrongException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

    //test put se entra nell'if vuol dire che l'operazione non e' stata eseguita per problemi
        System.out.printf("Test put:\n");
        if(!a.put(utenti[0], password[0], "provaLet.txt"))
            System.out.printf("\toperazione di put dell'utente " + utenti[0] + " non andata a buon fine\n");
        else System.out.printf("\til file provaLet.txt e' stato aggiunto nella collezione dell'utente "
                + utenti[0] +"\n");
        
        if(!a.put(utenti[0], password[0], "provaScr.txt"))
            System.out.printf("\toperazione di put dell'utente " + utenti[0] + " non andata a buon fine\n");
        else System.out.printf("\til file provaScr.txt e' stato aggiunto nella collezione dell'utente "
            + utenti[0] +"\n");

        if(!a.put(utenti[0], password[0], "studio.txt"))
            System.out.printf("\toperazione di put dell'utente " + utenti[0] + " non andata a buon fine\n");
        else System.out.printf("\til file studio.txt e' stato aggiunto nella collezione dell'utente "
            + utenti[0] +"\n");

        if(!a.put(utenti[2], password[2], "recensione.doc"))
            System.out.printf("\toperazione di put dell'utente " + utenti[2] + " non andata a buon fine\n");
        else System.out.printf("\til file recensioni.doc e' stato aggiunto nella collezione dell'utente "
            + utenti[2] +"\n");

        if(!a.put(utenti[2], password[2], "albergo.txt"))
            System.out.printf("\toperazione di put dell'utente " + utenti[2] + " non andata a buon fine\n");
        else System.out.printf("\til file albergo.txt e' stato aggiunto nella collezione dell'utente "
            + utenti[2] +"\n");

        //sbaglio per testare il comportamento
        if(!a.put(utenti[1], password[1], "errorePut.txt"))
            System.out.printf("\toperazione di put dell'utente " + utenti[1] + " non andata a buon fine\n");
        else System.out.printf("\til file errorePut.txt e' stato aggiunto nella collezione dell'utente "
            + utenti[1] +"\n");

    //test get
        System.out.printf("Test get:\n");
        try{
        		tmp = a.get(utenti[0], password[0],"provaLet.txt");
            System.out.printf("\tottengo file " +  tmp +
                    " dall'utente " + utenti[0] + "\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testate il comportamento
        try{
        		tmp = a.get(utenti[0], password[0],"recensione.doc");
            System.out.printf("\tottengo il file " + tmp + "\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

    //test copy
        System.out.printf("Test copy:\n");
        try{
            a.copy(utenti[0], password[0], "provaLet.txt");
            System.out.printf("\til file provaLet.txt e' stato copiato nell'utente " + utenti[0] +"\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testare il comportamento
        try{
            a.copy(utenti[0], password[0], "erroreCop.txt");
            System.out.printf("\til file erroreCop.txt e' stato copiato nell'utente " + utenti[0] +"\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

    //test remove
        System.out.printf("Test remove:\n");
        try{
            a.remove(utenti[0], password[0], "studio.txt");
            System.out.printf("\tl'utente " + utenti[0] + " ha rimosso il file studio.txt\n");

        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testare il comportamento
        try{
            a.remove(utenti[0], password[0], "erroreRem.mp3");
            System.out.printf("\tl'utente " + utenti[0] + " ha rimosso il file erroreRem.txt\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

    //test shareR
        System.out.printf("Test shareR:\n");
        try{
            a.shareR(utenti[0], password[0], utenti[2], "provaLet.txt");
            System.out.printf("\tl'utente " + utenti[0] + " autorizza in lettura "+ utenti[2]+
                    " per il file provaLet.txt\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testare il comportamento
        try{
            a.shareR(utenti[0], password[0], utenti[0],"erroreSh.doc");
            System.out.printf("\tl'utente " + utenti[0] + " autorizza in lettura "+ utenti[2]+
                  " per il file erroreSh.doc\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
     
    //test readF (avvertenze il file deve essere presente nel Desktop,sennò darà errore)
        System.out.printf("Test readF:\n");
        try{
            a.readF(utenti[2], password[2],"provaLet.txt");
            System.out.printf("\tl'utente " + utenti[2] + " legge il file provaLet.txt\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testare il comportamento
        try{
           a.readF(utenti[2], password[2],"erroreLet.txt");
           System.out.printf("\tl'utente " + utenti[2] + " legge il file erroreLet.txt\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        
    //test shareW
        System.out.printf("Test shareW:\n");
        try{
            a.shareW(utenti[0], password[0], utenti[2], "provaScr.txt");
            System.out.printf("\tl'utente " + utenti[0] + " autorizza per la scrittura e lettura "+ utenti[2]+
                    " per il file provaScr.txt\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testare il comportamento
        try{
            a.shareW(utenti[0], password[0], utenti[0],"erroreSh.doc");
            System.out.printf("\tl'utente " + utenti[0] + " autorizza per la scrittura e lettura "+ utenti[0]+
                  " il file erroreSh.doc\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        
    //test writeF (avvertenze il file deve essere presente nel Desktop,sennò darà errore)
        System.out.printf("Test writeF:\n");
  	     ArrayList<String> testo = new ArrayList<>();
  	     testo.add("Scrivo nel file" + System.getProperty("line.separator"));
  	     testo.add("provaScr.txt");
        try{
            a.writeF(utenti[2], password[2],"provaScr.txt",testo);
            System.out.printf("\tl'utente " + utenti[2] + " scrive il suo testo nel file provaScr.txt\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }
        //sbaglio per testare il comportamento provaLet.txt ha solo l'autorizzazione della lettura
        try{
      	  a.writeF(utenti[2], password[2],"provaLet.txt",testo);
           System.out.printf("\tl'utente " + utenti[2] + " scrive il suo testo provaLet.txt\n");
        }
        catch(IdErrorException | PasswWrongException | FileNotFoundException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

    //test getIterator
        System.out.printf("Test getIterator:\n");
        try{
            Iterator<String> itr = a.getIterator(utenti[0], password[0]);
            System.out.printf("\ti dati dell'utente " + utenti[0] + " : ");
            while(itr.hasNext()) 
                System.out.print(itr.next() + " ");
            System.out.println("");
        }
        catch(IdErrorException | PasswWrongException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

    //test getSize
        System.out.printf("Test getSize:\n");
        try{
            System.out.printf("\tl'utente " + utenti[0] + " ha " + a.getSize(utenti[0], password[0]) +
                    " file\n\n");
        }
        catch(IdErrorException | PasswWrongException e){
            System.out.printf("\t"+ e.getMessage() +"\n");
        }

        fTime = System.currentTimeMillis() - sTime;//tempo finale
        System.out.printf("TEMPO IMPIEGATO PRIMA IMPLEMENTAZIONE (ARRAYLIST) " +
               fTime + " millisecondi\n");

        //divisorio
        System.out.println("-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/");

        /* TEST SECONDA IMPLEMENTAZIONE (HASHMAP) */
        sTime = System.currentTimeMillis();//inizio tempo
        SecureFileContainer<String> h = new HashMapImpl<>();

        System.out.printf("\t\tSECONDA IMPLEMENTAZIONE HASHMAP\n\n");

     //test funzione createUser
         System.out.printf("Test createUser:\n");
         for(int i=0;i<5;i++){
             try{
                 h.createUser(utenti[i],password[i]);
             }
             catch(IdErrorException e){
                 System.out.printf("\t"+ e.getMessage() +"\n");
             }
         }
         //sbaglio aggiungendo un utente con un Id gia' usato da un utente presente nella collezione
         try{
             h.createUser(utenti[0],password[0]);
         }
         catch(IdErrorException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }

     //test removeUser
         System.out.printf("Test removeUser:\n");
         try{
             h.removeUser(utenti[1],password[1]);
             System.out.printf("\tl'utente " + utenti[1] + " e' stato eliminato\n");
         }
         catch(IdErrorException | PasswWrongException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testare il comportamento
         try{
             h.removeUser(utenti[1],password[1]);
             System.out.printf("\tl'utente " + utenti[1] + " e' stato eliminato\n");
         }
         catch(IdErrorException | PasswWrongException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }

     //test put se entra nell'if vuol dire che l'operazione non e' stata eseguita per problemi
         System.out.printf("Test put:\n");
         if(!h.put(utenti[0], password[0], "provaLet.txt"))
             System.out.printf("\toperazione di put dell'utente " + utenti[0] + " non andata a buon fine\n");
         else System.out.printf("\til file provaLet.txt e' stato aggiunto nella collezione dell'utente "
                 + utenti[0] +"\n");
         
         if(!h.put(utenti[0], password[0], "provaScr.txt"))
             System.out.printf("\toperazione di put dell'utente " + utenti[0] + " non andata a buon fine\n");
         else System.out.printf("\til file provaScr.txt e' stato aggiunto nella collezione dell'utente "
             + utenti[0] +"\n");

         if(!h.put(utenti[0], password[0], "studio.txt"))
             System.out.printf("\toperazione di put dell'utente " + utenti[0] + " non andata a buon fine\n");
         else System.out.printf("\til file studio.txt e' stato aggiunto nella collezione dell'utente "
             + utenti[0] +"\n");

         if(!h.put(utenti[2], password[2], "recensione.doc"))
             System.out.printf("\toperazione di put dell'utente " + utenti[2] + " non andata a buon fine\n");
         else System.out.printf("\til file recensioni.doc e' stato aggiunto nella collezione dell'utente "
             + utenti[2] +"\n");

         if(!h.put(utenti[2], password[2], "albergo.txt"))
             System.out.printf("\toperazione di put dell'utente " + utenti[2] + " non andata a buon fine\n");
         else System.out.printf("\til file albergo.txt e' stato aggiunto nella collezione dell'utente "
             + utenti[2] +"\n");

         //sbaglio per testare il comportamento
         if(!h.put(utenti[1], password[1], "errorePut.txt"))
             System.out.printf("\toperazione di put dell'utente " + utenti[1] + " non andata a buon fine\n");
         else System.out.printf("\til file errorePut.txt e' stato aggiunto nella collezione dell'utente "
             + utenti[1] +"\n");

     //test get
         System.out.printf("Test get:\n");
         try{
         	 tmp = h.get(utenti[0], password[0],"provaLet.txt");
             System.out.printf("\tottengo file " +  tmp +
                     " dall'utente " + utenti[0] + "\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testate il comportamento
         try{
         	 tmp = h.get(utenti[0], password[0],"recensione.doc");
             System.out.printf("\tottengo il file " + tmp + "\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }

     //test copy
         System.out.printf("Test copy:\n");
         try{
             h.copy(utenti[0], password[0], "provaLet.txt");
             System.out.printf("\til file provaLet.txt e' stato copiato nell'utente " + utenti[0] +"\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testare il comportamento
         try{
             h.copy(utenti[0], password[0], "erroreCop.txt");
             System.out.printf("\til file erroreCop.txt e' stato copiato nell'utente " + utenti[0] +"\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }

     //test remove
         System.out.printf("Test remove:\n");
         try{
             h.remove(utenti[0], password[0], "studio.txt");
             System.out.printf("\tl'utente " + utenti[0] + " ha rimosso il file studio.txt\n");

         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testare il comportamento
         try{
             h.remove(utenti[0], password[0], "erroreRem.mp3");
             System.out.printf("\tl'utente " + utenti[0] + " ha rimosso il file erroreRem.txt\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }

     //test shareR
         System.out.printf("Test shareR:\n");
         try{
             h.shareR(utenti[0], password[0], utenti[2], "provaLet.txt");
             System.out.printf("\tl'utente " + utenti[0] + " autorizza in lettura "+ utenti[2]+
                     " per il file provaLet.txt\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testare il comportamento
         try{
             h.shareR(utenti[0], password[0], utenti[0],"erroreSh.doc");
             System.out.printf("\tl'utente " + utenti[0] + " autorizza in lettura "+ utenti[2]+
                   " per il file erroreSh.doc\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
      
     //test readF (avvertenze il file deve essere presente nel Desktop,sennò darà errore)
         System.out.printf("Test readF:\n");
         try{
             h.readF(utenti[2], password[2],"provaLet.txt");
             System.out.printf("\tl'utente " + utenti[2] + " legge il file provaLet.txt\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testare il comportamento
         try{
            h.readF(utenti[2], password[2],"erroreLet.txt");
            System.out.printf("\tl'utente " + utenti[2] + " legge il file erroreLet.txt\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         
     //test shareW
         System.out.printf("Test shareW:\n");
         try{
             h.shareW(utenti[0], password[0], utenti[2], "provaScr.txt");
             System.out.printf("\tl'utente " + utenti[0] + " autorizza per la scrittura e lettura "+ utenti[2]+
                     " per il file provaScr.txt\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testare il comportamento
         try{
             h.shareW(utenti[0], password[0], utenti[0],"erroreSh.doc");
             System.out.printf("\tl'utente " + utenti[0] + " autorizza per la scrittura e lettura "+ utenti[0]+
                   " il file erroreSh.doc\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         
     //test writeF (avvertenze il file deve essere presente nel Desktop,sennò darà errore)
         System.out.printf("Test writeF:\n");
         try{
             h.writeF(utenti[2], password[2],"provaScr.txt",testo);
             System.out.printf("\tl'utente " + utenti[2] + " scrive il suo testo nel file provaScr.txt\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
         //sbaglio per testare il comportamento provaLet.txt ha solo l'autorizzazione della lettura
         try{
       	   h.writeF(utenti[2], password[2],"provaLet.txt",testo);
            System.out.printf("\tl'utente " + utenti[2] + " scrive il suo testo provaLet.txt\n");
         }
         catch(IdErrorException | PasswWrongException | FileNotFoundException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }

     //test getIterator
         System.out.printf("Test getIterator:\n");
         try{
             Iterator<String> itr = h.getIterator(utenti[0], password[0]);
             System.out.printf("\ti dati dell'utente " + utenti[0] + " : ");
             while(itr.hasNext()) 
                 System.out.print(itr.next() + " ");
             System.out.println("");
         }
         catch(IdErrorException | PasswWrongException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }

     //test getSize
         System.out.printf("Test getSize:\n");
         try{
             System.out.printf("\tl'utente " + utenti[0] + " ha " + h.getSize(utenti[0], password[0]) +
                     " file\n\n");
         }
         catch(IdErrorException | PasswWrongException e){
             System.out.printf("\t"+ e.getMessage() +"\n");
         }
 

        fTime = System.currentTimeMillis() - sTime;//tempo finale
        System.out.printf("TEMPO IMPIEGATO SECONDA IMPLEMENTAZIONE (HASHMAP) " +
               fTime + " millisecondi\n");
    }

}
