/**
 *
 * @author frmichetti
 * <Felipe Rodrigues Michetti at http://portfolio-frmichetti.rhcloud.com/>
 */
package downloadmanager.logic;

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
            System.out.println("Erro de IO : " + ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro de IO", JOptionPane.ERROR_MESSAGE);
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
                System.out.println("Excessão : " + ex.getMessage());
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro de IO", JOptionPane.ERROR_MESSAGE);
            }
            return downloadList;
        } else {
            System.out.println("Arquivo " + path.getName() + " não foi encontrado ");
            JOptionPane.showMessageDialog(null, "Arquivo " + path.getName() + " não foi encontrado ");
            return null;

        }

    }

}
