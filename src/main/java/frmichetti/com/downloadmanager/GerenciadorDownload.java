/**
 *
 * @author Felipe Rodrigues Michetti
 * @see http://portfolio-frmichetti.rhcloud.com
 * @see mailto:frmichetti@gmail.com
 * */
package frmichetti.com.downloadmanager;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import frmichetti.com.downloadmanager.console.DownloadManager;

public class GerenciadorDownload {

    public GerenciadorDownload() {
        aplicarAparencia();
        criarJanelas();
    }

    private static void aplicarAparencia() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("NÃ£o foi Possivel Definir o Tema Nimbus : " + ex.getMessage());
        }

    }

    public static void main(String args[]) {
        aplicarAparencia();
        criarJanelas();

    }

    private static void criarJanelas() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                new DownloadManager();
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            System.err.println("ExcessÃ£o : " + ex.getMessage());
        }
    }

}