/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.entity;

import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.IndexStore;
import br.com.mvbos.mymer.xml.XMLUtil;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Index;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Marcus Becker
 */
public class IndexEntity implements IElementEntity<IndexElement> {

    private List<IndexElement> indices = null;

    private static final File FILE_DIR_INDEX = new File(XMLUtil.CURRENT_PATH, "index");
    private static final File FILE_INDEX_STORE = new File(FILE_DIR_INDEX, "index.xml");

    @Override
    public boolean add(IndexElement e) {
        if (indices.contains(e)) {
            return false;
        }

        indices.add(e);
        return true;
    }

    @Override
    public boolean remove(IndexElement e) {
        if (!indices.contains(e)) {
            return false;
        }

        indices.remove(e);
        return true;
    }

    @Override
    public boolean save(IElementEntity parent) {
        IndexStore rStore = new IndexStore();
        List<Index> ind = new ArrayList<>(indices.size());

        for (IndexElement ie : indices) {
            Index i = new Index(ie.getTable().getDataBase().getName(), ie.getTable().getName(), ie.getName(), ie.getPrimary(), ie.getUnique(), ie.getActive(), ie.getFields());
            ind.add(i);
        }

        rStore.setIndices(ind);

        try {
            if (!FILE_DIR_INDEX.exists()) {
                FILE_DIR_INDEX.mkdir();
            }

            JAXBContext context = JAXBContext.newInstance(IndexStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, XMLUtil.FORMATTED_OUTPUT);

            m.marshal(rStore, XMLUtil.getFileOutputStream(FILE_INDEX_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    @Override
    public boolean load(IElementEntity parent) {

        DataBaseEntity ent = (DataBaseEntity) parent;

        List<DataBaseElement> dataBases = ent.getList();

        IndexStore iStore = null;

        try {

            if (!FILE_DIR_INDEX.exists()) {
                FILE_DIR_INDEX.mkdir();
            }

            if (FILE_INDEX_STORE.exists()) {

                JAXBContext context = JAXBContext.newInstance(IndexStore.class);
                Unmarshaller um = context.createUnmarshaller();

                iStore = (IndexStore) um.unmarshal(XMLUtil.getFileInputStream(FILE_INDEX_STORE));
            }

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        if (iStore != null && iStore.getIndices() != null && !iStore.getIndices().isEmpty()) {

            indices = new ArrayList<>(iStore.getIndices().size());

            for (Index i : iStore.getIndices()) {

                TableElement tb = EntityUtil.findTableByName(dataBases, i.getDataBaseName(), i.getTableName());
                if (tb == null) {
                    continue;
                }

                IndexElement ie = new IndexElement(i, tb);

                if (i.getFields() != null) {
                    List<Field> lstField = new ArrayList<>(i.getFields().size());
                    for (Field f : i.getFields()) {
                        int index = tb.getFields().indexOf(f);

                        if (index != -1) {
                            Field ff = tb.getFields().get(index);

                            if (i.getActive()) {
                                if (i.getPrimary()) {
                                    ff.addAttribute(Field.Attribute.PRIMARY);
                                } else {
                                    ff.addAttribute(Field.Attribute.INDEX);
                                }

                                if (i.getUnique()) {
                                    ff.addAttribute(Field.Attribute.UNIQUE);
                                }
                            }

                            lstField.add(ff);
                        }
                    }

                    ie.setFields(lstField);
                }

                indices.add(ie);
            }
        }

        if (indices == null) {
            indices = new ArrayList<>(10);
        }

        return true;
    }

    @Override
    public List<IndexElement> getList() {
        return indices;
    }

    @Override
    public IndexElement findByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IndexElement> findBy(IEntityFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addActionListern(ActionListener actionListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public IndexElement findByName(String indexName, TableElement e) {

        for (IndexElement ie : indices) {
            if (ie.getTable().equals(e)) {
                if (ie.getName().equals(indexName)) {
                    return ie;
                }
            }
        }

        return null;
    }

    public void replace(int idx, IndexElement index) {
        indices.set(idx, index);
    }

    public List<IndexElement> findIndexByTable(TableElement e) {
        List<IndexElement> lst = new ArrayList<>(15);

        for (IndexElement ie : indices) {
            if (ie.getTable().equals(e)) {
                lst.add(ie);
            }
        }

        return lst;
    }
}
