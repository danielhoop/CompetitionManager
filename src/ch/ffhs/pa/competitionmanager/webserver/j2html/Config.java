package ch.ffhs.pa.competitionmanager.webserver.j2html;

import ch.ffhs.pa.competitionmanager.webserver.j2html.utils.CSSMin;
import ch.ffhs.pa.competitionmanager.webserver.j2html.utils.EscapeUtil;
import ch.ffhs.pa.competitionmanager.webserver.j2html.utils.Indenter;
import ch.ffhs.pa.competitionmanager.webserver.j2html.utils.JSMin;
import ch.ffhs.pa.competitionmanager.webserver.j2html.utils.Minifier;
import ch.ffhs.pa.competitionmanager.webserver.j2html.utils.TextEscaper;
import java.util.Collections;

public class Config {

    /**
     * Change this to configure text-escaping
     * For example, to disable escaping, do <code>{@code Config.textEscaper = text -> text;}</code>
     */
    public static TextEscaper textEscaper = EscapeUtil::escape;
    /**
     * Change this to configure css-minification.
     * The default minifier is https://github.com/barryvan/CSSMin
     */
    public static Minifier cssMinifier = CSSMin::compressCss;
    /**
     * Change this to configure js-minification.
     * The default minifier is a simple whitespace/newline stripper
     */
    public static Minifier jsMinifier = JSMin::compressJs;
    /**
     * Change this to configure enable/disable closing empty tags
     * The default is to NOT close them
     */
    public static boolean closeEmptyTags = false;
    private static String FOUR_SPACES = "    ";
    /**
     * Change this to configure indentation when rendering formatted html
     * The default is four spaces
     */
    public static Indenter indenter = (level, text) -> String.join("", Collections.nCopies(level, FOUR_SPACES)) + text;


    private Config() {
    }

}
