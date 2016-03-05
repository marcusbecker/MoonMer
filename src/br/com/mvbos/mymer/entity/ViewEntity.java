/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.entity;

import br.com.mvbos.mymer.xml.ViewStore;
import br.com.mvbos.mymer.xml.XMLUtil;
import br.com.mvbos.mymer.xml.XMLUtil1;
import static br.com.mvbos.mymer.xml.XMLUtil1.FORMATTED_OUTPUT;
import static br.com.mvbos.mymer.xml.XMLUtil1.getFileInputStream;
import static br.com.mvbos.mymer.xml.XMLUtil1.getFileOutputStream;
import br.com.mvbos.mymer.xml.field.View;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
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
public class ViewEntity implements IElementEntity<View> {

    private static final File FILE_DIR_VIEWS = new File(XMLUtil.CURRENT_PATH, "views");
    private static final File FILE_VIEW_STORE = new File(FILE_DIR_VIEWS, "view.xml");

    private List<View> views;

    @Override
    public boolean add(View e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(View e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean save(IElementEntity parent) {
        try {
            if (!FILE_DIR_VIEWS.exists()) {
                FILE_DIR_VIEWS.mkdir();
            }

            ViewStore vs = new ViewStore();
            vs.setViews(views);

            JAXBContext context = JAXBContext.newInstance(ViewStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);

            m.marshal(vs, getFileOutputStream(FILE_VIEW_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil1.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    @Override
    public boolean load(IElementEntity parent) {
        views = new ArrayList<>(20);

        if (!FILE_DIR_VIEWS.exists()) {
            return false;
        }

        File[] files = FILE_DIR_VIEWS.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml");
            }
        });

        for (File f : files) {
            try {
                JAXBContext context = JAXBContext.newInstance(ViewStore.class);
                Unmarshaller um = context.createUnmarshaller();

                ViewStore vs = (ViewStore) um.unmarshal(getFileInputStream(f));

                if (vs.getViews() != null) {
                    views.addAll(vs.getViews());
                }

            } catch (JAXBException | FileNotFoundException ex) {
                Logger.getLogger(XMLUtil1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return true;
    }

    @Override
    public List<View> getList() {
        return views;
    }

    @Override
    public View findByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<View> findBy(IEntityFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addActionListern(ActionListener actionListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
