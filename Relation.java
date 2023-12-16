import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Relation implements Serializable{
    private String nom;
    private ArrayList<String> domaines;
    private ArrayList<String> attributs;
    private ArrayList<ArrayList<String>> valeurs;






                                        /* CONSTRUCTEURS */






    public Relation(String nom, ArrayList<String> domaines, ArrayList<String> attributs, ArrayList<ArrayList<String>> valeurs) {
        this.nom = nom;
        this.domaines = domaines;
        this.attributs = attributs;
        this.valeurs = valeurs;
    }

    public Relation() {
        nom = "";
        domaines = new ArrayList<>();
        attributs = new ArrayList<>();
        valeurs = new ArrayList<>();
    }




    

                                    /* SETTERS ET GETTERS */







    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ArrayList<String> getDomaines() {
        return domaines;
    }

    public void ajouterDomaine(String domaine) {
        domaines.add(domaine);
    }

    public void setDomaines(ArrayList<String> dom) {
        this.domaines = dom;
    }

    public ArrayList<String> getAttributs() {
        return attributs;
    }

    public void ajouterAttribut(String attribut) {
        attributs.add(attribut);
    }

    public ArrayList<ArrayList<String>> getValeurs() {
        return valeurs;
    }

    public void setValeurs(ArrayList<ArrayList<String>> val)
    {
        this.valeurs=val;
    }

    public void ajouterValeur(ArrayList<String> valeur) {
        valeurs.add(valeur);
    }

    private void setAttributs(ArrayList<String> attributs2) {
        this.attributs=attributs2;
    }





                                /* CREATION DE RELATION */







    public static Vector requeteCreation(String requete) throws Exception {
        requete = requete.toLowerCase();
        String[] mots = requete.split(" ");

        if (mots.length < 6 || !mots[0].equals("cree") || !mots[1].equals("la") || !mots[2].equals("relation")
                || !mots[4].equals(":")) {
            throw new Exception("Structure de requête invalide");
        }

        String domaineAttributsPart = requete.substring(requete.indexOf(':') + 1).trim();


        if (domaineAttributsPart.contains(" ")) {
            throw new Exception("Erreur : espaces entre les attributs et noms d'attributs");
        }

        String[] domaineAttributsPartSplit = domaineAttributsPart.split(",");
        ArrayList<String> typesAttributs = new ArrayList<>();
        ArrayList<String> nomsAttributs = new ArrayList();

        for (String domaineAttribut : domaineAttributsPartSplit) {
            if (!domaineAttribut.contains("=")) {
                throw new Exception("Erreur : '=' manquant dans la définition d'attributs");
            }

            domaineAttribut = domaineAttribut.trim();
            String[] domaineAttributSplit = domaineAttribut.split("=");

            if (domaineAttributSplit.length != 2) {
                throw new Exception("Erreur de syntaxe dans la définition d'attributs= manque de '='");
            }

            String nomAttribut = domaineAttributSplit[0].trim();
            String typeAttribut = domaineAttributSplit[1].trim();

            if (!typeAttribut.matches("(?i)int|double|float|boolean|string|date")) {
                throw new Exception("Type d'attribut invalide : " + typeAttribut);
            }

            typesAttributs.add(typeAttribut);
            nomsAttributs.add(nomAttribut);
        }

        Vector resultat = new Vector();
        resultat.add(mots[3]); // Nom de la table
        resultat.add(typesAttributs); // Types d'attributs
        resultat.add(nomsAttributs); // Noms d'attributs
        return resultat;

    }

    public Relation(String requete) throws Exception
    {
        try
        {
            Vector v = requeteCreation(requete);
            this.nom = (String) v.get(0);
            this.domaines = (ArrayList<String>) v.get(1);
            this.attributs = (ArrayList<String>) v.get(2);
            this.valeurs = new ArrayList<>();

            AccesFichier.enregistrerObjetDansFichier(this, ((String) v.get(0))+".txt");
            System.out.println("Insertion reussie !");
            
        }
        catch(Exception e)
        {
            throw e;
        }
    }





                            /* INSERTION DE DONNEES  */






    public static Vector requeteInsertion(String requete) throws Exception 
    {
        String[] mots = requete.split(" ", 5);
    
        if (mots.length < 5 || !mots[0].equalsIgnoreCase("ajoute") || !mots[1].equalsIgnoreCase("dans") || !mots[3].equals(":")) {
            throw new Exception("Structure de requête invalide");
        }
    
        String nomTable = mots[2];
        String attributsValeursPart = mots[4];
    
        // Split en ignorant les virgules dans les valeurs
        String[] attributsValeurs = attributsValeursPart.split(",(?=(?:[^']*'[^']*')*[^']*$)");
    
        ArrayList<String> nomsAttributs = new ArrayList<>();
        ArrayList<String> valeursAttributs = new ArrayList<>();
    
        for (String attributValeur : attributsValeurs) {
            attributValeur = attributValeur.trim();
            String[] attributValeurSplit = attributValeur.split("=(?=(?:[^']*'[^']*')*[^']*$)");
    
            if (attributValeurSplit.length != 2) {
                throw new Exception("Erreur de syntaxe dans la définition d'attributs et de valeurs");
            }
    
            String nomAttribut = attributValeurSplit[0].trim().toLowerCase();
            String valeurAttribut = attributValeurSplit[1].trim();
    
            // Vérifie que les valeurs sont entourées de guillemets simples
            if (!valeurAttribut.startsWith("'") || !valeurAttribut.endsWith("'")) {
                throw new Exception("Les valeurs doivent être entourées de guillemets simples : " + valeurAttribut);
            }
    
            // Supprime les guillemets simples des valeurs
            valeurAttribut = valeurAttribut.substring(1, valeurAttribut.length() - 1);
    
            nomsAttributs.add(nomAttribut);
            valeursAttributs.add(valeurAttribut);
        }
    
        Vector<Object> resultat = new Vector<>();
        resultat.add(nomTable); // Nom de la table
        resultat.add(nomsAttributs); // Noms des attributs
        resultat.add(valeursAttributs); // Valeurs des attributs
        return resultat;

    }

    public static void insererDansTable(String requete) throws Exception 
    {
        Vector donneesRequete = requeteInsertion(requete);
        String nomTable = ((String) donneesRequete.get(0)).toLowerCase();
        if (!tableExiste(nomTable)) 
        {
            throw new Exception("La table '" + nomTable + "' n'existe pas.");
        }
        Relation relation = (Relation) AccesFichier.recupererObjetDepuisFichier(nomTable+".txt");
        
        ArrayList<String> nomsAttributsRequete = (ArrayList<String>) donneesRequete.get(1);
        ArrayList<String> valeursRequete = (ArrayList<String>) donneesRequete.get(2);

        ArrayList<String> domainesTable = relation.getDomaines();
        ArrayList<String> nomsAttributsTable = relation.getAttributs();
        ArrayList<String> valeursTable = new ArrayList<>();

        // Vérifie si les noms d'attributs de la requête correspondent à ceux de la table
        for (String nomAttributRequete : nomsAttributsRequete) {
            if (!nomsAttributsTable.contains(nomAttributRequete)) {
                throw new Exception("Le nom d'attribut '" + nomAttributRequete + "' n'existe pas dans la table.");
            }
        }

        // à jour les valeurs de la table en fonction de la requête
        for (int i = 0; i < nomsAttributsTable.size(); i++) {
            String nomAttributTable = nomsAttributsTable.get(i);

            if (nomsAttributsRequete.stream().anyMatch(attr -> attr.equalsIgnoreCase(nomAttributTable))) {
                // Le nom d'attribut est présent dans la requête, met à jour la valeur correspondante
                int indexRequete = nomsAttributsRequete.indexOf(nomAttributTable);
                String valeurRequete = valeursRequete.get(indexRequete);
                String domaineAttribut = domainesTable.get(i);

                // Vérifie si la valeur est compatible avec le domaine de l'attribut
                if (!estValeurCompatible(valeurRequete, domaineAttribut)) {
                    throw new Exception("La valeur '" + valeurRequete + "' n'est pas compatible avec le domaine de l'attribut '" + nomAttributTable + "'.");
                }
                valeursTable.add(i, valeurRequete);
            } 
            else 
            {
                // Le nom d'attribut n'est pas présent dans la requête, ajout d' une valeur null
                valeursTable.add(i, null);
            }
        }
        if (valeursTable.size()>0) 
        {
            relation.getValeurs().add(valeursTable);
        }
        
        AccesFichier.enregistrerObjetDansFichier(relation, nomTable+".txt");

        System.out.println("Donnees inserees !");
    }





                                    /* SELECTION DE DONNEES */






    public static Vector<String> requetePrendsTout(String requete) throws Exception {
        Vector<String> resultat = new Vector<>();
        String[] mots = requete.split(" ", 6);
        System.out.println(mots[2]);
    
        if (mots.length < 4 || !mots[0].equalsIgnoreCase("prends") || !mots[1].equalsIgnoreCase("tout") || !mots[2].equalsIgnoreCase("dans")) {
            throw new Exception("Structure de requête invalide");
        }
    
        String nomTable = mots[3].toLowerCase();
        resultat.add(nomTable); // Nom de la table
    
        if (mots.length == 6) {
            String conditions = mots[5];
    
            if (contientEspacesEnDehorsDesGuillemets(conditions)) {
                throw new Exception("Les conditions contiennent des espaces en dehors des guillemets simples.");
            }
    
            resultat.add(conditions); // Conditions de la requête
        } else {
            resultat.add(""); // Aucune condition spécifiée
        }
    
        return resultat;
    }

    public static Vector<String> requetePrendsSpecial(String requete) throws Exception {
        Vector<String> resultat = new Vector<>();
        String[] mots = requete.split(" ", 6);

        if (mots.length < 4 || !mots[0].equalsIgnoreCase("prends") || !mots[2].equalsIgnoreCase("dans")) {
            throw new Exception("Structure de requête invalide");
        }

        String nomTable = mots[3].toLowerCase();
        String specColonnes = mots[1];

        resultat.add(nomTable); // Nom de la table
        resultat.add(specColonnes); // Nom de la table

        if (mots.length >= 5) {
            
            String conditions = mots[5];

            if (contientEspacesEnDehorsDesGuillemets(conditions)) {
                throw new Exception("Les conditions contiennent des espaces en dehors des guillemets simples.");
            }

            resultat.add(conditions); // Conditions de la requête
        } else {
            resultat.add(""); // Aucune condition spécifiée
        }

        return resultat;
    }

    public static Relation verifierConditions(Relation table, String conditions) throws Exception {
        ArrayList<ArrayList<String>> valeursValides = new ArrayList<>();
    
        for (ArrayList<String> ligne : table.getValeurs()) {
            if (conditions.isEmpty()) {
                // Aucune condition spécifiée, toutes les lignes sont valides
                valeursValides.add(new ArrayList<>(ligne)); // Cloner la ligne
            } else {
                boolean ligneValide = true;
    
                if (conditions.contains("&") && conditions.contains("|")) {
                    // Les deux opérateurs sont présents, on les traite en conséquence
                    String[] conditionsOr = conditions.split("\\|");
                    boolean auMoinsUneConditionOrValide = false;
    
                    for (String conditionOr : conditionsOr) {
                        String[] conditionsAnd = conditionOr.split("&");
                        boolean toutesLesConditionsValides = true;
    
                        for (String condition : conditionsAnd) {
                            String operateur = detecterOperateur(condition);
                            String[] conditionParts = condition.split(operateur);
                            if (conditionParts.length != 2) {
                                throw new Exception("Condition invalide : " + condition);
                            }
                            String nomAttribut = conditionParts[0].toLowerCase();
                            String valeurCondition = conditionParts[1];
                            if (!valeurCondition.startsWith("'") || !valeurCondition.endsWith("'")) {
                                throw new Exception("La valeur dans la condition doit être entourée de guillemets simples (apostrophes) : " + valeurCondition);
                            }
                            valeurCondition = valeurCondition.substring(1, valeurCondition.length() - 1);
    
                            if (!table.getAttributs().contains(nomAttribut)) {
                                throw new Exception("Le nom d'attribut '" + nomAttribut + "' n'existe pas.");
                            }
    
                            String attribut = ligne.get(table.getAttributs().indexOf(nomAttribut));
                            
                            toutesLesConditionsValides = comparerValeurs(attribut, valeurCondition, operateur);
                            if (!toutesLesConditionsValides) 
                            {
                                break;
                            }
                        }
    
                        if (toutesLesConditionsValides) {
                            auMoinsUneConditionOrValide = true;
                            break; // Pas besoin de vérifier les autres conditions OR pour cette ligne
                        }
                    }
    
                    if (!auMoinsUneConditionOrValide) {
                        ligneValide = false;
                    }
                    
                } else if (conditions.contains("|") && !conditions.contains("&")) {
                    // L'opérateur | (ou) est présent
                    String[] conditionsOr = conditions.split("\\|");
                    boolean toutesLesConditionsValides = false;
    
                    for (String condition : conditionsOr) {
                        
    
                        String operateur = detecterOperateur(condition);
                        String[] conditionParts = condition.split(operateur);
                        if (conditionParts.length != 2) {
                            throw new Exception("Condition invalide : " + condition);
                        }
                        String nomAttribut = conditionParts[0].toLowerCase();
                        String valeurCondition = conditionParts[1];
                        if (!valeurCondition.startsWith("'") || !valeurCondition.endsWith("'")) {
                            throw new Exception("La valeur dans la condition doit être entourée de guillemets simples (apostrophes) : " + valeurCondition);
                        }
                        valeurCondition = valeurCondition.substring(1, valeurCondition.length() - 1);

                        if (!table.getAttributs().contains(nomAttribut)) {
                            throw new Exception("Le nom d'attribut '" + nomAttribut + "' n'existe pas.");
                        }

                        String attribut = ligne.get(table.getAttributs().indexOf(nomAttribut));
                        toutesLesConditionsValides = comparerValeurs(attribut, valeurCondition, operateur);
                        if (toutesLesConditionsValides) 
                        {
                            break;
                        }
                    }
    
                        if (toutesLesConditionsValides) {
                            ligneValide = true;
                        }
                    
                } else if (conditions.contains("&") && !conditions.contains("|")) {
                    // L'opérateur & (et) est présent
                    String[] conditionsAnd = conditions.split("&");
    
                    for (String condition : conditionsAnd) {
                        String operateur = detecterOperateur(condition);
                        String[] conditionParts = condition.split(operateur);
                        if (conditionParts.length != 2) {
                            throw new Exception("Condition invalide : " + condition);
                        }
                        String nomAttribut = conditionParts[0].toLowerCase();
                        String valeurCondition = conditionParts[1];
                        if (!valeurCondition.startsWith("'") || !valeurCondition.endsWith("'")) {
                            throw new Exception("La valeur dans la condition doit être entourée de guillemets simples (apostrophes) : " + valeurCondition);
                        }
                        valeurCondition = valeurCondition.substring(1, valeurCondition.length() - 1);
    
                        if (!table.getAttributs().contains(nomAttribut)) {
                            throw new Exception("Le nom d'attribut '" + nomAttribut + "' n'existe pas.");
                        }
    
                        String attribut = ligne.get(table.getAttributs().indexOf(nomAttribut));
                        ligneValide = comparerValeurs(attribut, valeurCondition, operateur);
                        if (!ligneValide) 
                        {
                            break;
                        }
                    }
                } else {
                    // Ni | ni & n'est présent, on traite les conditions sans distinction
                    boolean toutesLesConditionsValides = true;
    
                    String[] conditionsSplit = new String[1];
                    conditionsSplit[0] = conditions;
    
                    for (String condition : conditionsSplit) {
                        String operateur = detecterOperateur(condition);
                        String[] conditionParts = condition.split(operateur);
                        if (conditionParts.length != 2) {
                            throw new Exception("Condition invalide : " + condition);
                        }
                        String nomAttribut = conditionParts[0].toLowerCase();
                        String valeurCondition = conditionParts[1];
                        if (!valeurCondition.startsWith("'") || !valeurCondition.endsWith("'")) {
                            throw new Exception("La valeur dans la condition doit être entourée de guillemets simples (apostrophes) : " + valeurCondition);
                        }
                        valeurCondition = valeurCondition.substring(1, valeurCondition.length() - 1);
    
                        if (!table.getAttributs().contains(nomAttribut)) {
                            throw new Exception("Le nom d'attribut '" + nomAttribut + "' n'existe pas.");
                        }
    
                        String attribut = ligne.get(table.getAttributs().indexOf(nomAttribut));
                        toutesLesConditionsValides = comparerValeurs(attribut, valeurCondition, operateur);
                        if (!toutesLesConditionsValides) 
                        {
                            break;
                        }
                    }
    
                    if (!toutesLesConditionsValides) {
                        ligneValide = false;
                    }
                }
    
                if (ligneValide) {
                    valeursValides.add(new ArrayList<>(ligne)); // Cloner la ligne
                }
            }
        }
    
        Relation relationValide = new Relation();
        relationValide.setNom(table.getNom());
        relationValide.setDomaines(table.getDomaines());
        relationValide.setAttributs(table.getAttributs());
        relationValide.setValeurs(valeursValides);
    
        return relationValide;
    }
    
    public static Relation executePrendTout(String requete) throws Exception {
        Vector<String> requeteInfo = requetePrendsTout(requete);
        Relation table = new Relation();
        if (requeteInfo.size() < 2) {
            throw new Exception("Structure de requête invalide");
        }
    
        String nomTable = requeteInfo.get(0);
        String conditions = requeteInfo.get(1);

        if (nomTable.contains("✶")) 
        {
            table = effectuerJoin(nomTable, "✶");
        }
        else if (nomTable.contains("×")) {
            table = effectuerJoin(nomTable, "×");
        }
        else
        {
            String cheminFichier = nomTable + ".txt";
            File fichier = new File(cheminFichier);
        
            if (!fichier.exists()) {
                throw new Exception("La table '" + nomTable + "' n'existe pas.");
            }
            Object objet = AccesFichier.recupererObjetDepuisFichier(cheminFichier);
            table = (Relation) objet;
        }
    
        table = verifierConditions(table, conditions);
        return table;
        
    }
    
    public static Relation executePrendsSpecial(String requete) throws Exception {

        Relation table = new Relation();

        Vector<String> requeteInfo = requetePrendsSpecial(requete);
    
        if (requeteInfo.size() < 2) {
            throw new Exception("Structure de requête invalide");
        }
    
        String nomTable = requeteInfo.get(0);
        String specColonnes = requeteInfo.get(1);
        String conditions = requeteInfo.get(2);

        if (nomTable.contains("✶")) 
        {
            table = effectuerJoin(nomTable, "✶");
        }
        else if (nomTable.contains("×")) {
            table = effectuerJoin(nomTable, "×");
        }
        else{
            String cheminFichier = nomTable + ".txt";
            File fichier = new File(cheminFichier);
        
            if (!fichier.exists()) {
                throw new Exception("La table '" + nomTable + "' n'existe pas.");
            }
        
            Object objet = AccesFichier.recupererObjetDepuisFichier(cheminFichier);
        
            table = (Relation) objet;
        }
    
        
    
            table = verifierConditions(table, conditions);
    
            Relation tableSpeciale = new Relation();
            tableSpeciale.setNom(table.getNom());
    
            String[] colonnesSpecifiees;

            if(specColonnes.contains("-"))
            {
                colonnesSpecifiees = specColonnes.split("-");
            }
            else
            {
                colonnesSpecifiees = new String[]{specColonnes};
            }
    
            if (!specColonnes.isEmpty()) {
                ArrayList<String> attributsTable = table.getAttributs();
                ArrayList<String> attributsSpeciaux = new ArrayList<>();
    
                for (String colonneSpec : colonnesSpecifiees) {
                    if (!attributsTable.contains(colonneSpec.toLowerCase())) {
                        throw new Exception("La colonne spécifiée '" + colonneSpec + "' n'existe pas dans la table.");
                    }
                    attributsSpeciaux.add(colonneSpec.toLowerCase());
                }
    
                ArrayList<String> domainesSpeciaux = new ArrayList<>();
                ArrayList<ArrayList<String>> valeursSpeciales = new ArrayList<>();
    
                for (String attribut : attributsSpeciaux) {
                    int index = attributsTable.indexOf(attribut);
                    domainesSpeciaux.add(table.getDomaines().get(index));
                    for (ArrayList<String> ligne : table.getValeurs()) {
                        ArrayList<String> valeursLigne = new ArrayList<>();
                        for (String colonneSpec : colonnesSpecifiees) {
                            int id = attributsTable.indexOf(colonneSpec.toLowerCase());
                            valeursLigne.add(ligne.get(id));
                        }
                        if(!valeursSpeciales.contains(valeursLigne))
                        {
                            valeursSpeciales.add(valeursLigne);
                        }
                        
                    }
                }
    
                tableSpeciale.setAttributs(attributsSpeciaux);
                tableSpeciale.setDomaines(domainesSpeciaux);
                tableSpeciale.setValeurs(valeursSpeciales);
            } else {
                throw new Exception("Aucune colonne spécifiée.");
            }
            return tableSpeciale;
        } 
    
    public static Relation executeSelect(String executeQuery) throws Exception
    {
        String[] s = executeQuery.split(" ");
        Relation r = new Relation();
        if (s[1].equalsIgnoreCase("tout")) 
        {
            r = executePrendTout(executeQuery);
        } 
        else 
        {
            r = executePrendsSpecial(executeQuery);
        }
        r.supprimerDoublons();
        return r;
    }





                            /* EXECUTER TOUTES LES REQUETES */







    public static void executeTout(String executeQuery) throws Exception
    {
        String[] s = executeQuery.split(" ");
        if (s[0].equalsIgnoreCase("cree")) 
        {
            Relation r = new Relation(executeQuery);
        } 
        else if(s[0].equalsIgnoreCase("ajoute"))
        {
            insererDansTable(executeQuery);
        }
        else if (s[0].equalsIgnoreCase("prends")) 
        {
            Relation r = executeSelect(executeQuery);
            r.supprimerDoublons();
            r.afficherRelationConsole();
        }
        else
        {
            throw new Exception("Requete invalide :"+s[0]);
        }
    }





                                /* AFFICHAGE DE RELATION */






    public static String padRight(String input, int length, char fillChar) {
        if (input.length() >= length) {
            return input;
        }
        StringBuilder padded = new StringBuilder(input);
        while (padded.length() < length) {
            padded.append(fillChar);
        }
        return padded.toString();
    }

    public void afficherRelationConsole() {
        int totalWidth = 1; // Largeur totale de la table, initialisee à 1 pour la barre verticale
        int[] columnWidths = new int[attributs.size()];
    
        // Calculer la largeur de chaque colonne
        for (int i = 0; i < attributs.size(); i++) {
            columnWidths[i] = attributs.get(i).length();
    
            for (ArrayList<String> ligne : valeurs) {
                int cellWidth = ligne.get(i).length();
                if (cellWidth > columnWidths[i]) {
                    columnWidths[i] = cellWidth;
                }
            }
    
            totalWidth += columnWidths[i] + 1; // +1 pour la barre verticale
        }
    
        // Afficher les en-têtes (attributs)
        for (int i = 0; i < attributs.size(); i++) {
            System.out.print("+");
            System.out.print(padRight(attributs.get(i), columnWidths[i], ' '));
        }
        System.out.println("+");
    
        // Afficher la ligne de séparation
        for (int i = 0; i < attributs.size(); i++) {
            System.out.print("+");
            for (int j = 0; j < columnWidths[i]; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    
        // Afficher les valeurs
        for (ArrayList<String> ligne : valeurs) {
            for (int i = 0; i < attributs.size(); i++) {
                System.out.print("|");
                System.out.print(padRight(ligne.get(i), columnWidths[i], ' '));
            }
            System.out.println("|");
        }
    
        // Afficher la ligne de séparation
        for (int i = 0; i < attributs.size(); i++) {
            System.out.print("+");
            for (int j = 0; j < columnWidths[i]; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }
    





                                    /* FONCTIONS D'ACTIONS SUR DEUX RELATIONS */






    public static Relation intersection(Relation relation1, String attribut1, Relation relation2, String attribut2) throws Exception {
        attribut1 = attribut1.toLowerCase();
        attribut2 = attribut2.toLowerCase();
    
        ArrayList<String> domaines1 = relation1.getDomaines();
        ArrayList<String> domaines2 = relation2.getDomaines();
        ArrayList<String> attributs1 = relation1.getAttributs();
        ArrayList<String> attributs2 = relation2.getAttributs();
    
        if (!attributs1.contains(attribut1)) {
            throw new Exception("L'attribut '" + attribut1 + "' n'existe pas dans la première relation.");
        }
        if (!attributs2.contains(attribut2)) {
            throw new Exception("L'attribut '" + attribut2 + "' n'existe pas dans la deuxième relation.");
        }

        if (!domaines1.get(attributs1.indexOf(attribut1)).equalsIgnoreCase(domaines2.get(attributs2.indexOf(attribut2)))) {
            throw new Exception("Les attributs ne sont pas de même type.");
        }
            
    
        Relation resultat = new Relation();
    
        for (String attribut : attributs1) {
            attribut = attribut.toLowerCase();
            if ( !resultat.getAttributs().contains(attribut)) {
                resultat.ajouterAttribut(attribut);
            }
        }
    
        for (String attribut : attributs2) {
            attribut = attribut.toLowerCase();
            if (!resultat.getAttributs().contains(attribut)) {
                resultat.ajouterAttribut(attribut);
            }
        }
    
        int indexAttribut1 = attributs1.indexOf(attribut1);
        int indexAttribut2 = attributs2.indexOf(attribut2);
    
        for (ArrayList<String> ligne1 : relation1.getValeurs()) {
            String valeurAttribut1 = ligne1.get(indexAttribut1);
            for (ArrayList<String> ligne2 : relation2.getValeurs()) {
                String valeurAttribut2 = ligne2.get(indexAttribut2);
                if (valeurAttribut1.equals(valeurAttribut2)) {

                    ArrayList<String> ligneResultat = new ArrayList<>();
                    for (String attribut : resultat.getAttributs()) {
                        int indexAttribut = attributs1.contains(attribut) ?
                                attributs1.indexOf(attribut) : attributs2.indexOf(attribut);
                        if (indexAttribut != -1) {
                            if (attributs1.contains(attribut)) {
                                ligneResultat.add(ligne1.get(indexAttribut));
                            } else {
                                ligneResultat.add(ligne2.get(indexAttribut));
                            }
                        } else {
                            ligneResultat.add(null); // attribut manquant
                        }
                    }
                    resultat.ajouterValeur(ligneResultat);
                }
            }
        }
        System.out.println("Resultat d'intersection : ");
        resultat.afficherRelationConsole();
        resultat.supprimerDoublons();
        return resultat;
    }
    
    public static Relation union(Relation relation1, Relation relation2) throws Exception 
    { 
        if (!sontStructuresIdentiques(relation1, relation2)) 
        {
            throw new Exception("Les deux relations n'ont pas la même structure.");
        }
    
        Relation resultat = new Relation();
        
        for (String attribut : relation1.getAttributs()) {
            attribut = attribut.toLowerCase();
            resultat.ajouterAttribut(attribut);
        }
        
        for (ArrayList<String> ligne : relation1.getValeurs()) {
            resultat.ajouterValeur(new ArrayList<>(ligne));
        }
        
        for (ArrayList<String> ligne : relation2.getValeurs()) {
            if (!resultat.getValeurs().contains(ligne)) {
                resultat.ajouterValeur(new ArrayList<>(ligne));
            }
        }
        System.out.println("Resultat d'union : ");
        resultat.supprimerDoublons();
        resultat.afficherRelationConsole();
        return resultat;
    }

    public static Relation produitCartesien(Relation relation1, Relation relation2) {
        Relation resultat = new Relation();
        resultat.setNom(relation1.getNom() + "_" + relation2.getNom());

        // Combinaison des domaines
        ArrayList<String> domainesResultat = new ArrayList<>(relation1.getDomaines());
        domainesResultat.addAll(relation2.getDomaines());
        resultat.setDomaines(domainesResultat);

        // Combinaison des attributs
        ArrayList<String> attributsResultat = new ArrayList<>(relation1.getAttributs());
        attributsResultat.addAll(relation2.getAttributs());
        
        // Si des attributs en commun existent, les renommer avec le nom de la table
        ArrayList<String> attributsCommuns = new ArrayList<>(relation1.getAttributs());
        attributsCommuns.retainAll(relation2.getAttributs());

        for (String attributCommun : attributsCommuns) {
            int indexAttributCommun1 = relation1.getAttributs().indexOf(attributCommun);
            int indexAttributCommun2 = relation2.getAttributs().indexOf(attributCommun);

            attributsResultat.set(indexAttributCommun1, relation1.getNom() + "." + attributCommun);
            attributsResultat.set(indexAttributCommun2 + relation1.getAttributs().size(), relation2.getNom() + "." + attributCommun);
        }

        resultat.setAttributs(attributsResultat);

        // Combinaison des valeurs
        ArrayList<ArrayList<String>> valeursResultat = new ArrayList<>();
        for (ArrayList<String> ligne1 : relation1.getValeurs()) {
            for (ArrayList<String> ligne2 : relation2.getValeurs()) {
                ArrayList<String> ligneResultat = new ArrayList<>(ligne1);
                ligneResultat.addAll(ligne2);
                valeursResultat.add(ligneResultat);
            }
        }
        resultat.setValeurs(valeursResultat);
        resultat.supprimerDoublons();
        return resultat;
    }

    public static Relation jointureNaturelle(Relation relation1, Relation relation2) {
        // Utiliser la fonction cartesienne pour obtenir le produit cartésien des deux relations
        Relation produitCartesien = produitCartesien(relation1, relation2);
    
        // Trouver les attributs communs
        ArrayList<String> attributsCommuns = new ArrayList<>(relation1.getAttributs());
        attributsCommuns.retainAll(relation2.getAttributs());
    
        // Créer une nouvelle relation pour le résultat final
        Relation resultat = new Relation();
        resultat.setNom(relation1.getNom() + "_JOIN_" + relation2.getNom());
    
        // Ajouter les domaines et attributs à la nouvelle relation
        ArrayList<String> domainesResultat = new ArrayList<>(relation1.getDomaines());
        domainesResultat.addAll(relation2.getDomaines());
        resultat.setDomaines(domainesResultat);
    
        ArrayList<String> attributsResultat = new ArrayList<>(relation1.getAttributs());
        attributsResultat.addAll(relation2.getAttributs());
        resultat.setAttributs(attributsResultat);
    
        // Ajouter les valeurs correspondantes à la nouvelle relation
        ArrayList<ArrayList<String>> valeursResultat = new ArrayList<>();
        for (ArrayList<String> ligne : produitCartesien.getValeurs()) {
            // Vérifier si les valeurs des attributs communs sont égales
            boolean correspondance = true;
            for (String attributCommun : attributsCommuns) {
                int indexAttributCommun1 = produitCartesien.getAttributs().indexOf(relation1.getNom()+"."+attributCommun);
                int indexAttributCommun2 = produitCartesien.getAttributs().indexOf(relation2.getNom()+"."+attributCommun);
    
                if (!ligne.get(indexAttributCommun1).equals(ligne.get(indexAttributCommun2))) {
                    correspondance = false;
                    break;
                }
            }
    
            // Si correspondance, ajouter la ligne à la nouvelle relation
            if (correspondance) {
                valeursResultat.add(ligne);
            }
        }
        resultat.setValeurs(valeursResultat);
        //resultat.supprimerDoublonsAttributs();
        resultat.supprimerDoublons();
    
        return resultat;
    }

    public static Relation difference(Relation table1, Relation table2) {
        // Vérifier si les attributs des deux tables sont identiques
        if (!table1.attributs.equals(table2.attributs) || !table1.domaines.equals(table2.domaines)) {
            throw new IllegalArgumentException("Les attributs et domaines des deux tables doivent être identiques.");
        }

        // Créer la table résultante
        Relation resultat = new Relation();
        resultat.setNom("Difference_" + table1.nom + "_" + table2.nom);
        resultat.setDomaines(new ArrayList<>(table1.domaines));
        resultat.setAttributs(new ArrayList<>(table1.attributs));

        // Ajouter les valeurs présentes dans la première table mais absentes de la deuxième table
        for (ArrayList<String> ligne1 : table1.valeurs) {
            if (!table2.valeurs.contains(ligne1)) {
                resultat.valeurs.add(new ArrayList<>(ligne1));
            }
        }
        resultat.supprimerDoublons();
        return resultat;
    }

    public static Relation equiJointure(Relation table1, Relation table2, String attribut1, String attribut2){
        // Utiliser la fonction cartesienne pour obtenir le produit cartésien des deux relations
        Relation produitCartesien = produitCartesien(table1, table2);

        int indexAttributCommun1 = produitCartesien.getAttributs().indexOf(table1.getNom()+"."+attribut1);
        int indexAttributCommun2 = produitCartesien.getAttributs().indexOf(table2.getNom()+"."+attribut2);
    
        // Créer une nouvelle relation pour le résultat final
        Relation resultat = new Relation();
        resultat.setNom(table1.getNom() + "_JOIN_" + table2.getNom());
    
        // Ajouter les domaines et attributs à la nouvelle relation
        ArrayList<String> domainesResultat = new ArrayList<>(table1.getDomaines());
        domainesResultat.addAll(table2.getDomaines());
        resultat.setDomaines(domainesResultat);
    
        ArrayList<String> attributsResultat = new ArrayList<>(table1.getAttributs());
        attributsResultat.addAll(table2.getAttributs());
        resultat.setAttributs(attributsResultat);
    
        // Ajouter les valeurs correspondantes à la nouvelle relation
        ArrayList<ArrayList<String>> valeursResultat = new ArrayList<>();
        for (ArrayList<String> ligne : produitCartesien.getValeurs()) {
            // Vérifier si les valeurs des attributs communs sont égales
            if (!ligne.get(indexAttributCommun1).equals(ligne.get(indexAttributCommun2))) {
                valeursResultat.add(ligne);
            }
            
        }
        resultat.setValeurs(valeursResultat);
        //resultat.supprimerDoublonsAttributs();
        resultat.supprimerDoublons();
        return resultat;
    }

    public static Relation effectuerJoin(String nomTables, String regex) throws Exception{
            String[] noms = nomTables.split(regex);
            ArrayList<Relation> tablesavecspecifique= new ArrayList<>();
            ArrayList<String> nomavecspecifique= new ArrayList<>();

            Relation resultat = new Relation();

            for (int i = 0; i < noms.length; i++) {
                String nomTable = noms[i];
                if (nomTable.contains(".")) {
                    nomavecspecifique.add(nomTable.split(".")[1]);
                    nomTable = nomTable.split(".")[0];
                }
                
                String cheminFichier = nomTable + ".txt"; 
                File fichier = new File(cheminFichier);
            
                if (!fichier.exists()) {
                    throw new Exception("La table '" + nomTable + "' n'existe pas.");
                }
            
                Object objet = AccesFichier.recupererObjetDepuisFichier(cheminFichier);
                Relation ri = (Relation) objet;
                tablesavecspecifique.add(ri);
            }

            for (int i = 1; i < tablesavecspecifique.size(); i++) {
                if (i==1) {
                    resultat = tablesavecspecifique.get(i-1);
                }
                if (regex.equals("×") && nomavecspecifique.get(i)!=null && nomavecspecifique.get(i-1)!=null) {
                    resultat = equiJointure(resultat, tablesavecspecifique.get(i), nomavecspecifique.get(i-1),nomavecspecifique.get(i));
                }
                else if (regex.equals("×")&&( nomavecspecifique.get(i)!=null || nomavecspecifique.get(i-1)!=null)) {
                    resultat = produitCartesien(resultat, tablesavecspecifique.get(i));
                }
                else if (regex.equals("✶")) {
                    resultat = jointureNaturelle(resultat, tablesavecspecifique.get(i));
                }
                
            }

            return resultat;
    }

    public void supprimerDoublonsAttributs() {
        HashSet<String> attributsUniques = new HashSet<>();
        ArrayList<Integer> indicesDoublons = new ArrayList<>();

        // Trouver les indices des doublons
        for (int i = 0; i < attributs.size(); i++) {
            String attribut = attributs.get(i);
            if (!attributsUniques.add(attribut)) {
                indicesDoublons.add(i);
            }
        }

        // Supprimer les doublons des attributs
        attributs = new ArrayList<>(attributsUniques);

        // Supprimer les valeurs associées aux doublons
        Iterator<ArrayList<String>> iterator = valeurs.iterator();
        while (iterator.hasNext()) {
            ArrayList<String> ligne = iterator.next();
            for (int i = indicesDoublons.size() - 1; i >= 0; i--) {
                int indiceDoublon = indicesDoublons.get(i);
                ligne.remove(indiceDoublon);
            }
        }
    }

    


                                    /*FONCTIONS DE TEST */

    



    // Fonction pour vérifier si les deux relations ont la même structure
    public static boolean sontStructuresIdentiques(Relation relation1, Relation relation2) {
        ArrayList<String> attributs1 = relation1.getAttributs();
        ArrayList<String> attributs2 = relation2.getAttributs();
        
        // Vérifie que les attributs sont identiques (en ignorant la casse)
        return attributs1.size() == attributs2.size() && attributs1.containsAll(attributs2);
    }

    public static boolean estValeurCompatible(String valeur, String domaine) {
        try {
            switch (domaine.toLowerCase()) {
                case "int":
                    Integer.parseInt(valeur);
                    break;
                case "double":
                    Double.parseDouble(valeur);
                    break;
                case "float":
                    Float.parseFloat(valeur);
                    break;
                case "string":// Aucune vérification nécessaire pour les chaînes de caractères
                    break;
                case "date":
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(valeur);
                    break;
                default:
                    return false; // Domaine inconnu
            }
            return true;
        } catch (NumberFormatException | ParseException e) {
            return false; // La valeur n'est pas compatible avec le domaine
        }
    }
 
    public static boolean tableExiste(String nomTable) 
    {
        String nomTableLowerCase = nomTable.toLowerCase();

        File fichierTable = new File(nomTableLowerCase + ".txt");
        return fichierTable.exists();
    }

    public static boolean isDate(String valeur) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // N'accepte pas les dates invalides
        
        try {
            Date date = dateFormat.parse(valeur);
            return true; // La valeur est une date valide
        } catch (ParseException e) {
            return false; // La valeur n'est pas une date valide
        }
    }

    private static boolean comparerDates(String date1, String date2, String operateur) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
    
        try {
            Date dateObj1 = dateFormat.parse(date1);
            Date dateObj2 = dateFormat.parse(date2);
            
            if (operateur.equals("=")) {
                return dateObj1.equals(dateObj2);
            } else if (operateur.equals("<")) {
                return dateObj1.before(dateObj2);
            } else if (operateur.equals(">")) {
                return dateObj1.after(dateObj2);
            } else if (operateur.equals("≤")) {
                return dateObj1.equals(dateObj2) || dateObj1.before(dateObj2);
            } else if (operateur.equals("≥")) {
                return dateObj1.equals(dateObj2) || dateObj1.after(dateObj2);
            } else {
                throw new Exception("Opérateur de comparaison de dates invalide : " + operateur);
            }
        } catch (ParseException e) {
            throw new Exception("Fausse format de date " + date1+" " +date2); // La comparaison échoue si l'une des valeurs n'est pas une date valide
        }
    }

    private static boolean comparerNumeriqueOuTexte(String valeur1, String valeur2, String operateur) throws Exception {
        try {
            switch (operateur) {
                case "=":
                    return valeur1.equals(valeur2);
                case "<":
                    return Double.parseDouble(valeur1) < Double.parseDouble(valeur2);
                case ">":
                    return Double.parseDouble(valeur1) > Double.parseDouble(valeur2);
                case "≤":
                    return Double.parseDouble(valeur1) <= Double.parseDouble(valeur2);
                case "≥":
                    return Double.parseDouble(valeur1) >= Double.parseDouble(valeur2);
                default:
                    throw new Exception("Opérateur de comparaison invalide : " + operateur);
            }
        } catch (NumberFormatException e) {
            throw new Exception("Erreur de type de nombre : "+ valeur1+" "+valeur2);
        }
    }

    private static boolean comparerValeurs(String valeur1, String valeur2, String operateur) throws Exception {
        if (isDate(valeur1) && isDate(valeur2)) {
            // Comparaison de dates
            return comparerDates(valeur1, valeur2, operateur);
        } else {
            // Comparaison de nombres ou de chaînes de caractères
            return comparerNumeriqueOuTexte(valeur1, valeur2, operateur);
        }
    }
 
    public static boolean contientEspacesEnDehorsDesGuillemets(String chaine) {
        boolean entreGuillemets = false;
        for (char caractere : chaine.toCharArray()) {
            if (caractere == '\'') {
                entreGuillemets = !entreGuillemets;
            } else if (caractere == ' ' && !entreGuillemets) {
                return true;
            }
        }
        return false;
    }




                                    /* FONCTIONS TOOLS */






    public static String[] extraireSignesComparaison(String condition) {
        // Utilisez une expression régulière pour trouver tous les signes de comparaison
        Pattern pattern = Pattern.compile("(?<=')[^']*'|(=|<|>|<=|>=)");

        Matcher matcher = pattern.matcher(condition);
        
        // Créez un tableau pour stocker les signes de comparaison
        List<String> signesComparaison = new ArrayList<>();
        
        // Parcourez la chaîne et ajoutez les signes de comparaison à la liste
        while (matcher.find()) {
            signesComparaison.add(matcher.group());
        }
        
        // Convertissez la liste en tableau
        String[] signesArray = new String[signesComparaison.size()];
        signesArray = signesComparaison.toArray(signesArray);
        
        return signesArray;
    }


    public static String[] extraireOperateurs(String conditions) {
        // Utilisez une expression régulière pour trouver tous les opérateurs "&" et "|"
        Pattern pattern = Pattern.compile("'[^']*'|[&|]");

        Matcher matcher = pattern.matcher(conditions);
        ArrayList<String> operateurs = new ArrayList<>();
        while (matcher.find()) {
            operateurs.add(matcher.group());
        }
        return operateurs.toArray(new String[0]);
    }

 
    private static String detecterOperateur(String valeur) throws Exception {
        if (valeur.contains("=")) {
            return "=";
        } else if (valeur.contains("<")) {
            return "<";
        } else if (valeur.contains(">")) {
            return ">";
        } else if (valeur.contains("≤")) {
            return "≤";
        } else if (valeur.contains("≥")) {
            return "≥";
        }
        else
        {
            throw new Exception("erreur: il n'y a pas de signe de comparaison . ");
        }
    }

    public void supprimerDoublons() {
        ArrayList<ArrayList<String>> nouvellesValeurs = new ArrayList<>();

        for (ArrayList<String> ligne : valeurs) {
            if (!nouvellesValeurs.contains(ligne)) {
                nouvellesValeurs.add(ligne);
            }
        }
        valeurs = nouvellesValeurs;
    }
    

    

}
    






    



