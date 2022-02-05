import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import Dati.DataBase;

//classe che implementa l'interfaccia per la registrazione
public class RegistrationImpl extends RemoteObject implements RegistrationInterface {
	private static final long serialVersionUID = 1L;
	private DataBase db;
    
    public RegistrationImpl(DataBase db){
        this.db=db;
    }

    @Override
    public int register(String nome, String password) throws RemoteException {
       int esito = db.addUtente(nome,password,0);
       return esito;
    }
    
}