package ch.ffhs.pa.competitionmanager.utils;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Opens an URL with the default browser of the computer.
 * Source: https://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
 */
public class UrlOpener {
    /**
     * Open a web page from given URI.
     * @param uri The URI.
     * @return True if no exception has occurred.
     */
    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Open a web page from given URL.
     * @param url The URL.
     * @return True if no exception has occurred.
     */
    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
