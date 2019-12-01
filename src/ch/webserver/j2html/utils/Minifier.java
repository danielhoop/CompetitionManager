package ch.webserver.j2html.utils;

@FunctionalInterface
public interface Minifier {
    String minify(String s);
}
