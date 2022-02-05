package Configurazione;

//Classe dove vengono memorizzate tutte le informazione utili per il progetto lato client
public class ConfigServer {
	public static String pathDataJson = "C:\\Users\\filip\\Desktop\\database.json"; //percorso database
	public static String pathDizionario = "C:\\Users\\filip\\Desktop\\Dizionario.txt"; //percorso dizionario
	public static int TCP_PORT = 1919;
	public static int RMI_PORT = 5001;
	public static String NAME_RMI = "regRmi";
	public static int N = 15; //numero di parole del dizionario
	public static int K = 5; //le k parole per il gioco
	public static int X = 5; //punti per risposta corretta
	public static int Y = 2; //punti penalita per risposta sbagliata
	public static int Z = 10; //punti extra per partita vinta
	public static long T2 = 60000; //intervallo partita
	public static int T1 = 5000; //intervallo massimo richiesta della partita
	public static String hostNameClient = "Localhost"; //ovviamente � tutto locale
	public static int portServerUDP = 1024; //la porta della socket udp del server
}
