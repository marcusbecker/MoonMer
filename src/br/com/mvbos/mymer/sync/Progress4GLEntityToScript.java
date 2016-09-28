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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author Marcus Becker
 */
public class Progress4GLEntityToScript extends EntityToScriptAbstract {

    private final String line = System.lineSeparator();

    /*
     First improve: use text string in Java code
     */
    @Override
    public void addTable(TableElement tb, StringBuilder sb) {
        //IEntityToScript.Mode.PLAIN == mode
        sb.append(String.format("ADD TABLE \"%s\"", tb.getName())).append(line);
        sb.append("  AREA \"Dados\"").append(line);
        sb.append(String.format("  DESCRIPTION \"%s\"", tb.getDescription())).append(line);
        sb.append(String.format("  DUMP-NAME \"%s\"", tb.getName())).append(line);
        sb.append(line);

    }

    @Override
    public void addField(TableElement tb, Field f, int index, StringBuilder sb) {
        sb.append("ADD FIELD \"").append(f.getName()).append("\" OF \"");
        sb.append(tb.getName()).append("\" AS ").append(f.getType()).append(line);
        sb.append("  DESCRIPTION \"").append(f.getDescription()).append("\"").append(line);
        sb.append("  FORMAT \"").append(f.getFormat()).append("\"").append(line);

        if (f.getInitial() == null || f.getInitial().isEmpty()) {
            sb.append("  INITIAL \"\"").append(line);
        } else {
            if (f.getFormat() == null || f.getFormat().startsWith("x")) {
                sb.append("  INITIAL \"").append(f.getInitial()).append("\"").append(line);
            } else {
                sb.append("  INITIAL ").append(f.getInitial()).append(line);
            }
        }

        sb.append("  LABEL \"").append(f.getName()).append("\"").append(line);
        //sb.append("  POSITION ").append(ct).append(line);
        //sb.append("  MAX-WIDTH 4").append(te.getName()).append("\"").append(line);

        if (f.getDecimals() > 0) {
            sb.append("  DECIMALS ").append(f.getDecimals()).append(line);
        }

        sb.append("  COLUMN-LABEL \"").append(f.getLabel()).append("\"").append(line);
        sb.append("  HELP \"").append(f.getHelp()).append("\"").append(line);
        sb.append("  ORDER ").append(index * 10).append(line);

        sb.append(line);

    }

    @Override
    public void addIndex(TableElement tb, IndexElement ie, StringBuilder sb) {
        //String line = IEntityToScript.Mode.PLAIN == mode ? System.lineSeparator() : System.lineSeparator() + "";

        sb.append("ADD INDEX \"").append(ie.getName()).append("\" ON \"").append(tb.getName());
        sb.append("\"").append(line);
        sb.append("  AREA \"Indices\"");
        sb.append(line);

        if (ie.getActive()) {
            sb.append("  ACTIVE");
            sb.append(line);
        }

        if (ie.getPrimary()) {
            sb.append("  PRIMARY");
            sb.append(line);
        }

        if (ie.getUnique()) {
            sb.append("  UNIQUE ");
            sb.append(line);
        }

        for (Field f : ie.getFields()) {
            sb.append("  INDEX-FIELD \"").append(f.getName()).append("\" ASCENDING");
            sb.append(line);
        }

        sb.append(line);

    }

    @Override
    public void renameField(TableElement tb, Field field, String oldName, String newName, StringBuilder sb) {
        sb.append("RENAME FIELD \"").append(oldName);
        sb.append("\" OF \"").append(tb.getName());
        sb.append("\" TO \"").append(newName).append("\"").append(line);
    }

    @Override
    public void renameIndex(TableElement tb, IndexElement ie, String oldName, String newName, StringBuilder sb) {
        sb.append("RENAME INDEX \"").append(oldName);
        sb.append("\" TO \"").append(newName);
        sb.append("\" ON \"").append(tb.getName());
        sb.append("\"").append(line);
    }

    @Override
    public void updateField(TableElement tb, Field f, Collection<String> changes, StringBuilder sb) {
        sb.append("UPDATE FIELD \"");
        sb.append(f.getName()).append("\" OF \"");
        sb.append(tb.getName()).append("\"").append(line);

        for (String c : changes) {
            sb.append(c).append(line);
        }

        sb.append(line);
    }

    @Override
    public void dropField(TableElement tb, Field f, StringBuilder sb) {
        sb.append("DROP FIELD \"").append(f.getName()).append("\" OF \"");
        sb.append(tb.getName()).append("\" ").append(line);
    }

    @Override
    public void dropTable(TableElement tb, StringBuilder sb) {
        sb.append("DROP TABLE \"").append(tb.getName()).append("\"").append(line);
    }

    @Override
    public void updateIndex(TableElement tb, IndexElement newIndex, IndexElement oldIndex, StringBuilder sb) {

        final LinkedHashMap<String, String> map = new LinkedHashMap<>(15);
        Differ.compare(map, newIndex, oldIndex);
        map.remove("name");
        map.remove("orgId");

        String tempName = newIndex.getName();

        //List<Field> fieldsToAdd = new ArrayList<>(oldIndex.getFields().size());
        //List<Field> fieldsToRem = new ArrayList<>(newIndex.getFields().size());
        boolean changeUnique = !newIndex.getUnique().equals(oldIndex.getUnique());
        boolean addedOrRemovedFields = newIndex.getFields().size() != oldIndex.getFields().size();

        if (!addedOrRemovedFields) {

            Set<String> newIndexNames = new HashSet<>(newIndex.getFields().size());
            Set<String> oldIndexNames = new HashSet<>(oldIndex.getFields().size());

            for (Field f : newIndex.getFields()) {
                newIndexNames.add(f.getOrgId());
            }

            for (Field f : oldIndex.getFields()) {
                oldIndexNames.add(f.getOrgId());
            }

            if (!newIndexNames.containsAll(oldIndexNames)) {
                addedOrRemovedFields = true;
            }
        }
        /*
         Para tornar um índice único deve-se utilizar o temporário, segue a sintaxe abaixo.
         RENAME INDEX " INDICE02" TO "temp-30828" ON "TABELA ABC"

         ADD INDEX " INDICE02" ON " TABELA ABC "
         AREA "Indices"
         UNIQUE
         INDEX-FIELD "CAMPO" ASCENDING

         DROP INDEX "temp-30828" ON " TABELA ABC"
       
         */
        if (changeUnique || addedOrRemovedFields) {
            tempName = "temp-" + tempName;
            sb.append(String.format("RENAME INDEX \"%s\" TO \"%s\" ON \"%s\"", newIndex.getName(), tempName, tb.getName()));
            sb.append(line);

            addIndex(tb, newIndex, sb);

            sb.append(String.format("DROP INDEX \"%s\" ON \"%s\"", tempName, tb.getName()));
            sb.append(line);

        } else if (!map.isEmpty()) {
            sb.append("UPDATE INDEX \"");
            sb.append(tempName).append("\" OF \"");
            sb.append(tb.getName()).append("\"").append(line);

            for (String c : map.values()) {
                sb.append(c).append(line);
            }

            sb.append(line);
        }
    }

}
