package ch.webserver.j2html.utils;

@FunctionalInterface
public interface TextEscaper {
    String escape(String text);
}
