/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.sync;

import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.field.Field;
import java.util.Collection;

/**
 *
 * @author Marcus Becker
 */
public class Progress4GLEntityToScript extends EntityToScriptAbstract {

    @Override
    public void addTable(TableElement tb, StringBuilder sb) {
        if (IEntityToScript.Mode.PLAIN == mode) {
            sb.append(String.format("ADD TABLE \"%s\"\n", tb.getName()));
            sb.append("  AREA \"Dados\"\n");
            sb.append(String.format("  DESCRIPTION \"%s\"\n", tb.getDescription()));
            sb.append(String.format("  DUMP-NAME \"%s\"\n", tb.getName()));
            sb.append("\n");
        } else {

        }
    }

    @Override
    public void addField(TableElement tb, Field f, int index, StringBuilder sb) {
        if (IEntityToScript.Mode.PLAIN == mode) {
            sb.append("ADD FIELD \"").append(f.getName()).append("\" OF \"").append(tb.getName()).append("\" AS ").append(f.getType()).append("\n");
            sb.append("  DESCRIPTION \"").append(f.getDescription()).append("\"\n");
            sb.append("  FORMAT \"").append(f.getFormat()).append("\"\n");
            sb.append("  INITIAL \"\"\n");
            sb.append("  LABEL \"").append(f.getName()).append("\"\n");
            //sb.append("  POSITION ").append(ct).append("\n");
            //sb.append("  MAX-WIDTH 4").append(te.getName()).append("\"\n");
            sb.append("  COLUMN-LABEL \"").append(f.getLabel()).append("\"\n");
            sb.append("  HELP \"").append(f.getHelp()).append("\"\n");
            sb.append("  ORDER ").append(index * 10).append("\n");
            sb.append("\n");

        } else {
            //log.append("ADD <b>").append(f.getName()).append("</i><br />");
            sb.append("ADD FIELD <b>\"").append(f.getName()).append("\"</b> OF <b>\"").append(tb.getName());
            sb.append("\"</b> AS ").append(f.getType()).append("<br>\n");
            sb.append("  DESCRIPTION \"").append(f.getDescription()).append("\"<br>\n");
            sb.append("  FORMAT \"").append(f.getFormat()).append("\"<br>\n");
            sb.append("  INITIAL \"\"<br>\n");
            sb.append("  LABEL \"").append(f.getName()).append("\"<br>\n");
            //log.append("  POSITION ").append(ct).append("<br>");
            //log.append("  MAX-WIDTH 4").append(te.getName()).append("\"<br>");
            sb.append("  COLUMN-LABEL \"").append(f.getLabel()).append("\"<br>\n");
            sb.append("  HELP \"").append(f.getHelp()).append("\"<br>\n");
            sb.append("  ORDER ").append(index * 10).append("<br>\n");

            sb.append("<br>");

        }

    }

    @Override
    public void addIndex(TableElement tb, IndexElement ie, StringBuilder sb) {
        if (IEntityToScript.Mode.PLAIN == mode) {
            sb.append("ADD INDEX \"").append(ie.getName()).append("\" ON \"").append(tb.getName()).append("\"\n");
            sb.append("  AREA \"Indices\"\n");
            if (ie.getPrimary()) {
                sb.append("  UNIQUE\n");
            }
            if (ie.getUnique()) {
                sb.append("  PRIMARY\n");
            }

            for (Field f : ie.getFields()) {
                sb.append("  INDEX-FIELD \"").append(f.getName()).append("\" ASCENDING\n");
            }

            sb.append("\n");
        } else {

        }
    }

    @Override
    public void renameField(TableElement tb, String oldName, String newName, StringBuilder sb) {
        sb.append("RENAME FIELD <b>\"").append(oldName);
        sb.append("\"</b> OF <b>\"").append(tb.getName());
        sb.append("\"</b> TO <b>\"").append(newName).append("\"</b><br><br>\n");
    }

    @Override
    public void updateField(TableElement tb, Field f, Collection<String> changes, StringBuilder sb) {
        sb.append("UPDATE FIELD <b>\"");
        sb.append(f.getName()).append("\"</b> OF <b>\"");
        sb.append(tb.getName()).append("\"</b><br>\n");

        for (String c : changes) {
            sb.append(c).append("<br>\n");
        }

        sb.append("<br>\n");
    }

    @Override
    public void dropField(TableElement tb, Field f, StringBuilder sb) {
        sb.append("DROP FIELD <b>\"").append(f.getName()).append("\"</b> OF <b>\"");
        sb.append(tb.getName()).append("\"</b> <br><br>");
    }

    @Override
    public void dropTable(TableElement tb, StringBuilder sb) {
        sb.append("DROP TABLE \"").append(tb.getName()).append("\"\n");
    }

}
