/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

/**
 *
 * @author Marcus Becker
 */
public interface IEntityFilter<T> {

    public boolean accept(T e);

}
