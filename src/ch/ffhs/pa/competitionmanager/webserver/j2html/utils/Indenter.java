package ch.ffhs.pa.competitionmanager.webserver.j2html.utils;

@FunctionalInterface
public interface Indenter {
    String indent(int level, String text);
}
