import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println("Usage: java Client <adresse_ip_serveur> <numero_port_serveur>");
            System.exit(1);
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            // Établir la connexion avec le serveur
            Socket socket = new Socket(serverIP, serverPort);
            System.out.println("Connexion établie avec le serveur.");

            // Configurer les flux de sortie et d'entrée pour la communication
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Utiliser un Scanner pour lire les commandes du client depuis la console
            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Demander au client d'entrer une commande
                System.out.print("Entrez une commande (ou 'exit' pour quitter) : ");
                String commandeDuClient = scanner.nextLine();

                // Vérifier si le client veut quitter
                if (commandeDuClient.equalsIgnoreCase("exit")) {
                    break;
                }

                // Envoyer la commande au serveur
                out.println(commandeDuClient);

                // Lire et afficher la réponse du serveur ligne par ligne
                String reponse;
                
                while (true) {
                    System.out.println(in.readLine());

                    // reponse = in.readLine();
                    // System.out.println(reponse);
                    // if (reponse != null || !reponse.equals("")) {
                        
                    // }
                    // else {
                    //     System.out.println("break");
                    //     break;
                    // }
                    //System.out.println("ligne lue");
                    
                }
                //System.out.println("Fin de lecture");
            }

            // Fermer la connexion
            socket.close();
        } catch (IOException e) {
            System.err.println("Erreur lors de la connexion au serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
