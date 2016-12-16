package br.com.codecode.downloadmanager.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Class CheckUrl.
 */
public class CheckUrl {    
    
    /**
     * Instantiates a new check url.
     */
    private CheckUrl(){}

    /**
     * Verify URL.
     *
     * @param url the url
     * @return the url
     * @throws MalformedURLException the malformed URL exception
     */
    public static URL verifyURL(String url) throws MalformedURLException {

        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("ftp://") && !url.toLowerCase().startsWith("https://") && !url.toLowerCase().startsWith("ftps://")) {
            return null;
        }

        if (url.lastIndexOf('.') == -1) {
            return null;
        }

        URL verifiedUrl = new URL(url);

        if (verifiedUrl.getFile().isEmpty()) {
            return null;
        }
        if (verifiedUrl.getFile().length() < 2) {
            return null;
        }

        System.out.println("URL Autority: " + verifiedUrl.getAuthority());
        System.out.println("URL Host: " + verifiedUrl.getHost());
        System.out.println("URL Protocolo: " + verifiedUrl.getProtocol());
        System.out.println("URL Path: " + verifiedUrl.getPath());
        System.out.println("URL File: " + verifiedUrl.getFile().substring(verifiedUrl.getFile().lastIndexOf('/') + 1));
        System.out.println("");

        return verifiedUrl;
    }

}
