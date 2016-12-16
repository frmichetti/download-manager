package br.com.codecode.downloadmanager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.com.codecode.downloadmanager.console.DownloadManager;


/**
 * The Class Start.
 */
public class Start {

    /**
     * Instantiates a new start.
     */
    public Start() {
        doChangeTheme();
        doCreateFrame();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String ... args) {
        new Start();
    }

    /**
     * Do change theme.
     */
    private void doChangeTheme() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * Do create frame.
     */
    private void doCreateFrame() {
        SwingUtilities.invokeLater((() -> {
	    new DownloadManager();
	}));
    }

}
