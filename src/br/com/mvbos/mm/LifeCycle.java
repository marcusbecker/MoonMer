/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcuss
 */
class LifeCycle {

    private static final List<ICycle> cycles = new ArrayList<>(4);

    protected static void preStart() {
        for (ICycle c : cycles) {
            c.onPreStart();
        }
    }

    protected static void afterLoadBases() {
        for (ICycle c : cycles) {
            c.onAfterLoadBases();
        }
    }

    protected static void afterLoadMainWindow() {
        for (ICycle c : cycles) {
            c.onAfterLoadMainWindow();
            Logger.getLogger(LifeCycle.class.getName()).log(Level.INFO, "{0} finished.", c.getCycleName());
        }
    }

    public static void addCycle(ICycle cycle) {
        cycles.add(cycle);
    }

}
