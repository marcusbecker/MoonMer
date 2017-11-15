/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mm;

import br.com.mvbos.mymer.Window;

/**
 *
 * @author marcuss
 */
public interface ICycle {

    public void onPreStart();

    public void onAfterLoadBases();

    public void onAfterLoadMainWindow(Window w);

    public String getCycleName();

    public void recieveResult(boolean sucess, Exception e);
}
