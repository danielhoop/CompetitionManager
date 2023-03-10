package ch.ffhs.pa.competitionmanager.webserver.j2html.attributes;

import ch.ffhs.pa.competitionmanager.webserver.j2html.tags.Renderable;
import ch.ffhs.pa.competitionmanager.webserver.j2html.Config;

import java.io.IOException;

public class Attribute implements Renderable {
    private String name;
    private String value;

    public Attribute(String name, String value) {
        this.name = name;
        this.value = Config.textEscaper.escape(value);
    }

    public Attribute(String name) {
        this.name = name;
        this.value = null;
    }

    @Override
    public void renderModel(Appendable writer, Object model) throws IOException {
        if (name == null) {
            return;
        }
        writer.append(" ");
        writer.append(name);
        if (value != null) {
            writer.append("=\"");
            writer.append(value);
            writer.append("\"");
        }
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
