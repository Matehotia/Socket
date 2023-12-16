import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // Exemple d'utilisation
        // String requeteCreation = "CREE LA RELATION ÉTUDIANT : Nom=STRING,Âge=INT,DateNaissance=DATE";
        // String requeteCreation2 = "CREE LA RELATION ÉTUDIANTS : Nom=STRING,Âge=INT,DateNaissance=DATE";
        // String requeteCreation3 = "CREE LA RELATION HABITAT : Nom=STRING,Ville=STRING";


        // String requeteInsertion1 = "AJOUTE DANS ÉTUDIANT : Nom='Alice',Âge='25',DateNaissance='1998-05-15'";
        // String requeteInsertion2 = "AJOUTE DANS ÉTUDIANT : Nom='Bob',Âge='30',DateNaissance='1993-08-20'";
        // String requete = "PRENDS nom-datenaissance-Âge DANS ÉTUDIANT ou nom='Alice'&Âge='30'|nom='Bob'&Âge='25'|nom='Alice'";
        // String requete = "PRENDS TOUT DANS ÉTUDIANT ou DateNaissance<'1998-05-15'|nom='Alice'";


        //  String requeteInsertion3 = "AJOUTE DANS ÉTUDIANTS : Nom='Tsanta',Âge='18',DateNaissance='2005-05-15'";
        //  String requeteInsertion4 = "AJOUTE DANS ÉTUDIANTS : Nom='Lucas',Âge='19',DateNaissance='2003-08-20'";

        //  String requeteInsertion5 = "AJOUTE DANS HABITAT : Nom='Tsanta',Ville='Tana'";
        //  String requeteInsertion6 = "AJOUTE DANS HABITAT : Nom='Lucas',Ville='Fianarantsoa'";


        // String requete = "PRENDS TOUT DANS ÉTUDIANT×HABITAT";
        // executeTout(requeteCreation);
        // executeTout(requeteCreation2);
        // executeTout(requeteCreation3);
        // executeTout(requeteInsertion1);
        // executeTout(requeteInsertion2);
        // executeTout(requeteInsertion3);
        // executeTout(requeteInsertion4);
        // executeTout(requeteInsertion5);
        // executeTout(requeteInsertion6);
        // executeTout(requete);
        // Appliquer le produit cartésien

    


//         Relation relationR = new Relation("R",
//         new ArrayList<>(List.of("String", "String", "String")),
//         new ArrayList<>(List.of("A", "B", "C")),
//         new ArrayList<>(List.of(
//                 new ArrayList<>(List.of("a", "b", "c")),
//                 new ArrayList<>(List.of("d", "b", "c")),
//                 new ArrayList<>(List.of("b", "b", "f")),
//                 new ArrayList<>(List.of("c", "a", "d"))
//         )));

// Relation relationS = new Relation("S",
//         new ArrayList<>(List.of("String", "String", "String")),
//         new ArrayList<>(List.of("B", "C", "D")),
//         new ArrayList<>(List.of(
//                 new ArrayList<>(List.of("b", "c", "d")),
//                 new ArrayList<>(List.of("b", "c", "e")),
//                 new ArrayList<>(List.of("a", "d", "b"))
//         )));

// Relation resultatJointure = Relation.jointureNaturelle(relationR, relationS);
// resultatJointure.afficherRelationConsole();

// resultatJointure.afficherRelationConsole();
        // Afficher le résultat
        // System.out.println("Nom de la relation résultante : " + resultat.getNom());
        // System.out.println("Domaines : " + resultat.getDomaines());
        // System.out.println("Attributs : " + resultat.getAttributs());
        // System.out.println("Valeurs : " + resultat.getValeurs());

    //     Relation relationR = new Relation();
    //     relationR.setNom("R");
    //     relationR.setAttributs(new ArrayList<>(List.of("A", "B")));
    //     relationR.setValeurs(new ArrayList<>(List.of(
    //         new ArrayList<>(List.of("1", "a")),
    //         new ArrayList<>(List.of("1", "b")),
    //         new ArrayList<>(List.of("4", "a"))
    //     )));

    //     // Création de la relation S
    //     Relation relationS = new Relation();
    //     relationS.setNom("S");
    //     relationS.setAttributs(new ArrayList<>(List.of("A", "B", "D")));
    //     relationS.setValeurs(new ArrayList<>(List.of(
    //         new ArrayList<>(List.of("1", "a", "b")),
    //         new ArrayList<>(List.of("2", "c", "b")),
    //         new ArrayList<>(List.of("4", "a", "a"))
    //     )));

    //     // Test de la jointure naturelle
    //     Relation resultatJointure = Relation.cartesienne(relationR, relationS);

    //     // Affichage du résultat
    //     System.out.println("R ✶ S : ");
    //     resultatJointure.afficherRelationConsole();
    //  }

    Scanner scanner = new Scanner(System.in);

    System.out.println("Votre commande : ");
    String commande = scanner.nextLine();
    Relation.executeTout(commande);
    }
}
    

    
    


