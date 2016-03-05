/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.entity;

import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.RelationshipStore;
import br.com.mvbos.mymer.xml.XMLUtil;
import static br.com.mvbos.mymer.xml.XMLUtil.FORMATTED_OUTPUT;
import static br.com.mvbos.mymer.xml.XMLUtil.getFileInputStream;
import static br.com.mvbos.mymer.xml.XMLUtil.getFileOutputStream;

import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Relationship;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
public class RelationEntity implements IElementEntity<RelationshipElement> {

    private static final File FILE_DIR_REL = new File(XMLUtil.CURRENT_PATH, "relations");
    private static final File FILE_RELATIONSHIP_STORE = new File(FILE_DIR_REL, "relationship_config.xml");

    private List<RelationshipElement> relations = null;

    @Override
    public boolean add(RelationshipElement e) {
        relations.add(e);

        return true;
    }

    public void addNewRelationship(RelationshipElement.Type type, TableElement elLeft, TableElement elRight) {
        relations.add(new RelationshipElement(type, elLeft, elRight));
    }

    @Override
    public boolean remove(RelationshipElement e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean save(IElementEntity parent) {
        RelationshipStore rStore = new RelationshipStore();
        List<Relationship> rel = new ArrayList<>(relations.size());

        for (RelationshipElement e : relations) {
            Relationship r = new Relationship(e);

            r.setChildFields(new LinkedHashSet<>(e.getChildFields()));
            r.setParentFields(new LinkedHashSet<>(e.getParentFields()));

            rel.add(r);
        }

        rStore.setRelations(rel);

        try {
            if (!FILE_DIR_REL.exists()) {
                FILE_DIR_REL.mkdir();
            }

            JAXBContext context = JAXBContext.newInstance(RelationshipStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);

            m.marshal(rStore, getFileOutputStream(FILE_RELATIONSHIP_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    @Override
    public boolean load(IElementEntity parent) {
        DataBaseEntity ent = (DataBaseEntity) parent;

        List<DataBaseElement> dataBases = ent.getList();

        RelationshipStore rStore = null;

        try {

            if (!FILE_DIR_REL.exists()) {
                FILE_DIR_REL.mkdir();
            }

            if (FILE_RELATIONSHIP_STORE.exists()) {

                JAXBContext context = JAXBContext.newInstance(RelationshipStore.class);
                Unmarshaller um = context.createUnmarshaller();

                rStore = (RelationshipStore) um.unmarshal(getFileInputStream(FILE_RELATIONSHIP_STORE));
            }

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (rStore != null && rStore.getRelations() != null && !rStore.getRelations().isEmpty()) {

            relations = new ArrayList<>(rStore.getRelations().size());

            for (Relationship r : rStore.getRelations()) {

                if (r.getType() < 0 || r.getType() > RelationshipElement.Type.values().length) {
                    continue;
                }

                TableElement rParent = EntityUtil.findTableByName(dataBases, r.getDbParente(), r.getParent());
                if (rParent == null) {
                    continue;
                }

                TableElement rChild = EntityUtil.findTableByName(dataBases, r.getDbChild(), r.getChild());
                if (rChild == null) {
                    continue;
                }

                RelationshipElement.Type type = RelationshipElement.Type.values()[r.getType()];
                RelationshipElement re = new RelationshipElement(type, rParent, rChild);

                for (Field f : r.getParentFields()) {
                    Field ff = EntityUtil.findFieldByName(rParent.getFields(), f.getName());
                    if (ff != null) {
                        re.getParentFields().add(ff);
                    }
                }

                for (Field f : r.getChildFields()) {
                    Field ff = EntityUtil.findFieldByName(rChild.getFields(), f.getName());
                    if (ff != null) {
                        re.getChildFields().add(ff);
                    }
                }

                relations.add(re);
            }
        }

        if (relations == null) {
            relations = new ArrayList<>(10);
        }

        return true;
    }

    @Override
    public List<RelationshipElement> getList() {
        return relations;
    }

    @Override
    public RelationshipElement findByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RelationshipElement> findBy(IEntityFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addActionListern(ActionListener actionListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Set<RelationshipElement> findRelationship(TableElement e) {
        Set<RelationshipElement> set = new HashSet<>(10);

        for (RelationshipElement re : relations) {
            if (re.getParent().equals(e) || re.getChild().equals(e)) {
                set.add(re);
            }
        }

        return set;
    }

    public Set<RelationshipElement> findRelationship(List<TableElement> lst) {
        Set<RelationshipElement> set = new HashSet<>(10);

        for (RelationshipElement re : relations) {
            if (lst.contains(re.getParent()) && lst.contains(re.getChild())) {
                set.add(re);
            }
        }

        return set;
    }

}
