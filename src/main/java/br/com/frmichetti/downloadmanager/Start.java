/**
 *
 * @author Felipe Rodrigues Michetti
 * @see http://portfolio-frmichetti.rhcloud.com
 * @see mailto:frmichetti@gmail.com
 * */
package br.com.frmichetti.downloadmanager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.com.frmichetti.downloadmanager.console.DownloadManager;

public class Start {

    public Start() {
        doChangeTheme();
        doCreateFrame();
    }

    private static void doChangeTheme() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Não foi Possivel Definir o Tema Nimbus : " + ex);
        }

    }

    public static void main(String ... args) {
        doChangeTheme();
        doCreateFrame();
    }

    private static void doCreateFrame() {
        SwingUtilities.invokeLater((() -> {
	    new DownloadManager();
	}));
    }

}
