/*
 * socketWorker.java ha il compito di gestire la connessione al socket da parte di un Client.
 * Elabora il testo ricevuto che in questo caso viene semplicemente mandato indietro con l'aggiunta 
 * di una indicazione che e' il testo che viene dal Server.
 */
import java.net.*;
import java.io.*; //importazione delle librerie necessarie al funzionamento del programma

/**
 *
 * @author Prof. Matteo Palitto
 */
class SocketWorker implements Runnable {
  private Socket client;  //variabile Socket privata
  public String nickname;  //variabile Stringa pubblica

    //Constructor: inizializza le variabili
    SocketWorker(Socket client) {
        this.client = client;
        System.out.println("Connesso con: " + client);
    }

    // Questa e' la funzione che viene lanciata quando il nuovo "Thread" viene generato
    public void run(){
        
        BufferedReader in = null;
        PrintWriter out = null;
        boolean nickPresente = false; // Booleano che verifica se il nick è già stato inserito o meno
        
        try{
          // connessione con il socket per ricevere (in) e mandare(out) il testo
          in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Stream di input, che viene RICEVUTO dal server, quindi spedito dal client
          out = new PrintWriter(client.getOutputStream(), true); // Stream di output, che ESCE dal server
        } catch (IOException e) {
          System.out.println("Errore: in|out fallito");
          System.exit(-1);
        }

        String line = "";
      
        int clientPort = client.getPort(); // Questa è la porta del socket del client. 
      
        while(line != null){ // Finchè line (stringa che viene riempita con l'input inserito dal client) è diverso da null, allora
          try{ 
           
            // Se l'utente client, digita: /listautenti, allora verrà mostrata tutta la lista degli utenti connessi
              if(line.equals("/listautenti") || line.equals("/listaUtenti")) 
              {
                  for(int i = 0; i < ServerTestoMultiThreaded.listaUtenti.size(); i++)
                  {
                      out.println(ServerTestoMultiThreaded.listaUtenti.get(i).nickname); // Per ogni indice, viene preso il nickname dalla lista utenti
                  }
              }
              
              if(nickPresente == false)  // Condizione booleana, verifica se il nickname è stato già inserito o meno
              {
                  nickname = in.readLine(); // Legge l'input dal clinet e lo assegna alla variabile nickname
                
                  out.println("-------------------------------");
                  out.println("Benvenuto: " + nickname); // Da un messaggio di benvenuto
                  out.println("-------------------------------");
                  nickPresente = true; // Il nickname è stato inserito quindi la variabile diventa true
              }
              
              else // Altrimenti
              {
                line = in.readLine(); // Leggi l'input dal client
                
                //Manda lo stesso messaggio appena ricevuto con in aggiunta il "nome" del client
                out.println("Server-->" + nickname + ">> " + line);
                
                out.println("--------------------------------");
                
                //scrivi messaggio ricevuto su terminale
                System.out.println(nickname + ">> " + line);
                
                out.println("-------------------------------");
              }
            
           } 
          
            catch (IOException e) {
             System.out.println("lettura da socket fallito");
             System.exit(-1);
           }
        }
      
        try //se la lettura col socket fallisce termina la connessione 
        {
            client.close();
            out.println("-------------------------------");
            System.out.println("connessione con client: " + client + " terminata!"); 
            out.println("-------------------------------");
        } 
      
        catch (IOException e) //nel caso non si potesse terminare correttamente la connessione, da' un errore all'utente
        {
            out.println("-------------------------------");
            System.out.println("Errore connessione con client: " + client);
            out.println("-------------------------------");
        }
        
        if(client.isClosed())
        {
            for(int i = 0; i < ServerTestoMultiThreaded.listaUtenti.size(); i++)
            {
                if(ServerTestoMultiThreaded.listaUtenti.get(i).nickname.equals(nickname))
                {
                    ServerTestoMultiThreaded.listaUtenti.remove(i);
                }
            }
        }
    }
}
