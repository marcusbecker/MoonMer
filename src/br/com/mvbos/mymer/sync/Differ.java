/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.sync;

import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.entity.EntityManager;
import br.com.mvbos.mymer.entity.EntityUtil;
import br.com.mvbos.mymer.entity.IndexEntity;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Table;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcuss
 */
public class Differ {

    private static final StringBuilder log;

    static {
        log = new StringBuilder();
    }

    private static void compare(Map<String, String> map, Field fa, Field fb) {

        java.lang.reflect.Field[] fields = fa.getClass().getDeclaredFields();

        for (java.lang.reflect.Field fl : fields) {

            try {
                java.lang.reflect.Field fr = fb.getClass().getDeclaredField(fl.getName());
                fl.setAccessible(true);
                fr.setAccessible(true);

                if (fl.get(fa) == null || fr.get(fb) == null) {
                    continue;
                }

                if (!fl.get(fa).equals(fr.get(fb))) {
                    map.put(fl.getName(), fl.getName().toUpperCase() + " " + fl.get(fa));
                }

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(Differ.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String compare(String name, List<Field> left, List<Field> right) {

        if (left.isEmpty() || right.isEmpty()) {
            return "";
        }

        log.delete(0, log.length());

        int ct = 1;

        for (int i = 0; i < left.size(); i++) {
            Field fl = left.get(i);
            int id;

            if (fl.getName().equals(fl.getOrgId())) {
                //rightField.indexOf(fl);
                id = EntityUtil.findIndexFieldByName(right, fl.getName());

                if (id == -1) {
                    id = EntityUtil.query(right, "orgId", fl.getOrgId());
                    //id = EntityUtil.findIndexFieldByName(rightField, fl.getOrgId());

                    if (id != -1) {
                        Field fr = right.get(id);

                        log.append("RENAME FIELD <b>\"").append(fr.getName());
                        log.append("\"</b> OF <b>\"").append(name);
                        log.append("\"</b> TO <b>\"").append(fr.getOrgId()).append("\"</b><br><br>\n");
                    }
                }

            } else {
                id = EntityUtil.findIndexFieldByName(right, fl.getOrgId());

                if (id != -1) {
                    log.append("RENAME FIELD <b>\"").append(fl.getOrgId());
                    log.append("\"</b> OF <b>\"").append(name);
                    log.append("\"</b> TO <b>\"").append(fl.getName()).append("\"</b><br><br>\n");
                }
            }

            if (id == -1) {
                //log.append("ADD <b>").append(f.getName()).append("</i><br />");
                log.append("ADD FIELD <b>\"").append(fl.getName()).append("\"</b> OF <b>\"").append(name);
                log.append("\"</b> AS ").append(fl.getType()).append("<br>\n");
                log.append("  DESCRIPTION \"").append(fl.getDescription()).append("\"<br>\n");
                log.append("  FORMAT \"").append(fl.getFormat()).append("\"<br>\n");
                log.append("  INITIAL \"\"<br>\n");
                log.append("  LABEL \"").append(fl.getName()).append("\"<br>\n");
                //log.append("  POSITION ").append(ct).append("<br>");
                //log.append("  MAX-WIDTH 4").append(te.getName()).append("\"<br>");
                log.append("  COLUMN-LABEL \"").append(fl.getLabel()).append("\"<br>\n");
                log.append("  HELP \"").append(fl.getHelp()).append("\"<br>\n");
                log.append("  ORDER ").append(ct * 10).append("<br>\n");

                log.append("<br>");
                ct++;

            } else {
                Field fr = right.get(id);

                if (id != i) {
                    //change order
                }

                LinkedHashMap<String, String> map = new LinkedHashMap<>(15);
                compare(map, fl, fr);
                map.remove("name");
                map.remove("orgId");

                if (!map.isEmpty()) {
                    log.append("UPDATE FIELD <b>\"");
                    log.append(fl.getName()).append("\"</b> OF <b>\"");
                    log.append(name).append("\"</b><br>\n");

                    for (String k : map.keySet()) {
                        log.append(map.get(k)).append("<br>\n");
                    }

                    log.append("<br>\n");
                }
            }
        }

        for (Field f : right) {
            boolean drop = true;

            for (Field ff : left) {
                if (EntityUtil.compareName(f.getName(), ff.getName())
                        || EntityUtil.compareName(f.getName(), ff.getOrgId())
                        || EntityUtil.compareName(f.getOrgId(), ff.getName())
                        || EntityUtil.compareName(f.getOrgId(), ff.getOrgId())) {

                    drop = false;
                    break;
                }
            }

            if (drop) {
                log.append("DROP FIELD <b>\"").append(f.getName()).append("\"</b> OF <b>\"");
                log.append(name).append("\"</b> <br><br>");
            }
        }

        return log.toString();
    }

    public static StringBuilder compare(TableElement t, Table temp) {
        return new StringBuilder(compare(t.getName(), t.getFields(), temp.getFields()));
    }

    public static void removeTable(TableElement te, StringBuilder sb) {
        sb.append("DROP TABLE \"").append(te.getName()).append("\"\n");
    }

    public static void addTable(TableElement te, StringBuilder sb) {
        sb.append("ADD TABLE \"").append(te.getName()).append("\"\n");
        sb.append("  AREA \"Dados\"\n");
        sb.append("  DESCRIPTION \"").append(te.getDescription()).append("\"\n");
        sb.append("  DUMP-NAME \"").append(te.getName()).append("\"\n");
        sb.append("\n");

        int ct = 1;
        for (Field f : te.getFields()) {
            sb.append("ADD FIELD \"").append(f.getName()).append("\" OF \"").append(te.getName()).append("\" AS ").append(f.getType()).append("\n");
            sb.append("  DESCRIPTION \"").append(f.getDescription()).append("\"\n");
            sb.append("  FORMAT \"").append(f.getFormat()).append("\"\n");
            sb.append("  INITIAL \"\"\n");
            sb.append("  LABEL \"").append(f.getName()).append("\"\n");
            //sb.append("  POSITION ").append(ct).append("\n");
            //sb.append("  MAX-WIDTH 4").append(te.getName()).append("\"\n");
            sb.append("  COLUMN-LABEL \"").append(f.getLabel()).append("\"\n");
            sb.append("  HELP \"").append(f.getHelp()).append("\"\n");
            sb.append("  ORDER ").append(ct * 10).append("\n");

            sb.append("\n");
            ct++;
        }
    }

    public static void addTableIndex(TableElement te, StringBuilder sb) {
        for (IndexElement ie : EntityManager.e().getEntity(IndexEntity.class).getList()) {
            if (!te.equals(ie.getTable())) {
                continue;
            }

            sb.append("ADD INDEX \"").append(ie.getName()).append("\" ON \"").append(te.getName()).append("\"\n");
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
        }
    }

    private void compare(Field fa, Field fb) {
        if (!fa.equals(fb)) {
            return;
        }

        java.lang.reflect.Field[] fields = fa.getClass().getDeclaredFields();

        for (java.lang.reflect.Field fl : fields) {
            try {
                java.lang.reflect.Field fr = fb.getClass().getDeclaredField(fl.getName());
                fl.setAccessible(true);
                fr.setAccessible(true);

                if (fl.get(fa) == null || fr.get(fb) == null) {
                    continue;
                }

                if (fl.getName().equals("name") || fl.getName().equals("orgId")) {
                    continue;
                }

                if (!fl.get(fa).equals(fr.get(fb))) {
                    log.append("<b>");
                    log.append(fa.getName()).append("</b> change <b>").append(fl.getName());
                    log.append("</b> from <i>").append(fl.get(fa)).append("</i> to <i>").append(fr.get(fb));
                    log.append("</i><br />");
                }

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(DiffWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
