/**
 *
 * @author Felipe Rodrigues Michetti
 * @see http://portfolio-frmichetti.rhcloud.com
 * @see mailto:frmichetti@gmail.com
 * */
package br.com.codecode.downloadmanager.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public abstract class Serialize {

    public static boolean writeobject(ArrayList<Download> downloadList, File path) {

        try (ObjectOutputStream ooS = new ObjectOutputStream(new FileOutputStream(path))) {
            ooS.writeObject((ArrayList<Download>) downloadList);
            ooS.flush();

        } catch (IOException ex) {
            System.err.println("Erro de IO : " + ex);
            JOptionPane.showMessageDialog(null, ex, "Erro de IO", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public static ArrayList<Download> readObject(File path) {
        if (path.exists()) {
            
        	ArrayList<Download> downloadList = null;
           
            try (ObjectInputStream oiS = new ObjectInputStream(new FileInputStream(path))) {

                downloadList = (ArrayList<Download>) oiS.readObject();

                System.out.println(oiS.toString());

            } catch (IOException | ClassNotFoundException ex) {
                System.err.println("Excessão : " + ex);
                JOptionPane.showMessageDialog(null, ex, "Erro de IO", JOptionPane.ERROR_MESSAGE);
            }
            return downloadList;
            
        } else {
            System.err.println("Arquivo " + path.getName() + " não foi encontrado ");
            JOptionPane.showMessageDialog(null, "Arquivo " + path.getName() + " não foi encontrado ");
            return null;

        }

    }

}
