/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.entity;

import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author Marcus Becker
 */
public interface IElementEntity<T> {

    public static final int EVT_ADD = 0;
    public static final int EVT_REMOVE = 1;

    public boolean add(T e);

    public boolean remove(T e);

    public boolean save(IElementEntity parent);

    public boolean load(IElementEntity parent);

    public List<T> getList();

    public T findByName(String name);

    public List<T> findBy(IEntityFilter filter);

    public void addActionListern(ActionListener actionListener);
}
