/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.sync;

import br.com.mvbos.mymer.el.DataBaseElement;
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

    private static final Progress4GLEntityToScript entityToScript;

    static {
        log = new StringBuilder();
        entityToScript = new Progress4GLEntityToScript();
    }

    private static <T> void compare(Map<String, String> map, T fa, T fb) {

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

    public static StringBuilder compareField(TableElement tb, List<Field> left, List<Field> right) {

        //log.delete(0, log.length());

        if (tb == null || left.isEmpty() || right.isEmpty()) {
            return log;
        }

        int ct = 1;
        entityToScript.setMode(IEntityToScript.Mode.DECORED);

        for (int i = 0; i < left.size(); i++) {
            Field fl = left.get(i);
            int id;

            if (fl.getName().equals(fl.getOrgId())) {
                //rightField.indexOf(fl);
                id = EntityUtil.indexOfFieldByName(right, fl.getName());

                if (id == -1) {
                    id = EntityUtil.query(right, "orgId", fl.getOrgId());
                    //id = EntityUtil.indexOfFieldByName(rightField, fl.getOrgId());

                    if (id != -1) {
                        Field fr = right.get(id);
                        entityToScript.renameField(tb, fr, fr.getName(), fr.getOrgId(), log);
                    }
                }

            } else {
                id = EntityUtil.indexOfFieldByName(right, fl.getOrgId());

                if (id != -1) {
                    entityToScript.renameField(tb, fl, fl.getOrgId(), fl.getName(), log);
                }
            }

            if (id == -1) {
                entityToScript.addField(tb, fl, ct++, log);

            } else {
                Field fr = right.get(id);

                if (id != i) {
                    //change order
                }

                final LinkedHashMap<String, String> map = new LinkedHashMap<>(15);
                compare(map, fl, fr);
                map.remove("name");
                map.remove("orgId");

                if (!map.isEmpty()) {
                    entityToScript.updateField(tb, fl, map.values(), log);
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
                entityToScript.dropField(tb, f, log);
            }
        }

        return log;
    }

    public static StringBuilder compareIndex(TableElement tb, List<IndexElement> left, List<IndexElement> right) {

        //log.delete(0, log.length());

        if (tb == null || left.isEmpty() || right.isEmpty()) {
            return log;
        }

        int ct = 1;
        entityToScript.setMode(IEntityToScript.Mode.DECORED);

        for (int i = 0; i < left.size(); i++) {
            IndexElement fl = left.get(i);
            int id;

            if (fl.getName().equals(fl.getOrgId())) {
                id = EntityUtil.indexOfIndexByName(right, fl.getName());

                if (id == -1) {
                    id = EntityUtil.query(right, "orgId", fl.getOrgId());

                    if (id != -1) {
                        IndexElement fr = right.get(id);
                        entityToScript.renameIndex(tb, fr, fr.getName(), fr.getOrgId(), log);
                    }
                }

            } else {
                id = EntityUtil.indexOfIndexByName(right, fl.getOrgId());

                if (id != -1) {
                    entityToScript.renameIndex(tb, fl, fl.getOrgId(), fl.getName(), log);
                }
            }

            if (id == -1) {
                entityToScript.addIndex(tb, fl, log);

            } else {
                IndexElement fr = right.get(id);

                if (id != i) {
                    //change order
                }

                final LinkedHashMap<String, String> map = new LinkedHashMap<>(15);
                compare(map, fl, fr);
                map.remove("name");
                map.remove("orgId");

                if (!map.isEmpty()) {
                    entityToScript.updateIndex(tb, fl, map.values(), log);
                }
            }
        }

        for (IndexElement f : right) {
            boolean drop = true;

            for (IndexElement ff : left) {
                if (EntityUtil.compareName(f.getName(), ff.getName())
                        || EntityUtil.compareName(f.getName(), ff.getOrgId())
                        || EntityUtil.compareName(f.getOrgId(), ff.getName())
                        || EntityUtil.compareName(f.getOrgId(), ff.getOrgId())) {

                    drop = false;
                    break;
                }
            }

            if (drop) {
                entityToScript.dropIndex(tb, f, log);
            }
        }

        return log;
    }

    public static StringBuilder compare(TableElement t, Table temp) {
        StringBuilder sb = new StringBuilder(compareField(t, t.getFields(), temp.getFields()));
        //sb.append(compareIndex(t, null, null));

        return sb;
    }

    public static void removeTable(TableElement te, StringBuilder sb) {
        entityToScript.dropTable(te, sb);
    }

    public static void addTable(TableElement te, StringBuilder sb) {
        entityToScript.setMode(IEntityToScript.Mode.PLAIN);
        entityToScript.addTable(te, sb);

        int ct = 1;

        for (Field f : te.getFields()) {
            entityToScript.addField(te, f, ct++, sb);
        }
    }

    public static void addTableIndex(TableElement te, StringBuilder sb) {
        entityToScript.setMode(IEntityToScript.Mode.PLAIN);

        for (IndexElement ie : EntityManager.e().getEntity(IndexEntity.class).getList()) {
            if (!te.equals(ie.getTable())) {
                continue;
            }

            entityToScript.addIndex(te, ie, sb);
        }
    }

    public static void addBase(DataBaseElement localBase, StringBuilder sb) {
        //sb.append("CREATE DATABASE ").append(localBase.getName());
    }

    public static void clear() {
        log.delete(0, log.length());
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
