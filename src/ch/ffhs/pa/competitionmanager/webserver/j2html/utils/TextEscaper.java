package ch.ffhs.pa.competitionmanager.webserver.j2html.utils;

@FunctionalInterface
public interface TextEscaper {
    String escape(String text);
}
