/**
 *
 * @author Felipe Rodrigues Michetti
 * @see http://portfolio-frmichetti.rhcloud.com
 * @see mailto:frmichetti@gmail.com
 * */
package br.com.frmichetti.downloadmanager.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;


public abstract class MyPath {

    public static final String LIB_OPEN_SHIFT = "http://portfolio-frmichetti.rhcloud.com/proj/java/download_manager/lib/icons/";
    public static final String LIB_LOCAL_HOST = "http://127.0.0.1/portfolio/proj/java/download_manager/lib/icons/";
    public static final String LIB_RELATIVE = "./lib/icons/";
    public static final String FILE_SEPARATOR = FileSystems.getDefault().getSeparator();

    public static String localePath(String caminho) {
        Path path;
        URI uri = null;

        try {
            uri = new URI(caminho);
            System.out.println("Host " + uri.getHost());
            System.out.println("Path " + uri.getPath());
            System.out.println("Raw Path " + uri.getRawPath());
            uri = uri.normalize();
            System.out.println("Normalize " + uri);
            System.out.println("");
        } catch (URISyntaxException ex) {
            System.err.println("Erro na Sintaxe da URI : " + ex.getMessage());
        }
        System.out.println("PATH");
        path = FileSystems.getDefault().getPath(uri.getPath());
        System.out.println("Path is absolute ? " + path.isAbsolute());
        System.out.println("Caminho Resolvido " + path.resolve(uri.getPath()));;
        return uri.toString();

    }

}
