import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AccesFichier {
    public static void enregistrerObjetDansFichier(Object objet, String cheminFichier) throws IOException 
    {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try 
        {
            File fichier = new File(cheminFichier);
            if (fichier.exists()) {
                fichier.delete();
            }
            fichier.createNewFile();

            fos = new FileOutputStream(cheminFichier);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(objet);
        } 
        finally 
        {
            if (oos != null) {
                oos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }


    public static Object recupererObjetDepuisFichier(String cheminFichier) throws Exception 
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(cheminFichier);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
    
}
