package br.com.codecode.downloadmanager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.com.codecode.downloadmanager.console.DownloadManager;

public class Start {

    public Start() {
        doChangeTheme();
        doCreateFrame();
    }

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

    public static void main(String ... args) {
        new Start();
    }

    private void doCreateFrame() {
        SwingUtilities.invokeLater((() -> {
	    new DownloadManager();
	}));
    }

}
