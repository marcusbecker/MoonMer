/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.entity;

import java.util.ArrayList;

/**
 *
 * @author Marcus Becker
 */
public class EntityManager {

    private static EntityManager em;

    public static EntityManager e() {
        if (em == null) {
            em = new EntityManager();
        }

        return em;
    }

    private final IElementEntity[] entities = new IElementEntity[5];

    private EntityManager() {
        entities[0] = new DataBaseEntity();
        entities[1] = new ConfigEntity();
        entities[2] = new RelationEntity();
        entities[3] = new IndexEntity();
        entities[4] = new ViewEntity();
    }

    public void start() {
        boolean load = entities[0].load(null);

        if (load) {
            for (int i = 1; i < entities.length; i++) {
                entities[i].load(entities[0]);
            }
        }
    }

    public boolean save() {
        boolean save = entities[0].save(null);

        if (save) {
            for (int i = 1; i < entities.length; i++) {
                entities[i].save(entities[0]);
            }

            return true;
        }

        return false;
    }

    public <T extends IElementEntity> T getEntity(Class<T> cl) {

        for (IElementEntity e : entities) {

            if (cl.isInstance(e)) {
                return cl.cast(e);
            }

        }

        return null;
    }

    public static void main(String[] a) {
        EntityManager em = EntityManager.e();
        em.start();
        DataBaseEntity dbe = em.getEntity(DataBaseEntity.class);
    }

}
