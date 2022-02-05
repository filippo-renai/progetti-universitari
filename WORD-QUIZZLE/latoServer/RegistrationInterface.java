import java.rmi.Remote;
import java.rmi.RemoteException;

//interfaccia per la registrazione degli utenti
public interface RegistrationInterface extends Remote{
	public int register(String nome, String password) throws RemoteException;
}

