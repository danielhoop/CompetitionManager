package ch.ffhs.pa.competitionmanager.webserver.j2html.utils;

@FunctionalInterface
public interface Minifier {
    String minify(String s);
}
