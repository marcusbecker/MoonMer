/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.el.DataBaseElement;
import static br.com.mvbos.mymer.xml.XMLUtil.getFileInputStream;
import br.com.mvbos.mymer.xml.field.DataConfig;
import java.awt.Color;
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
public class ConfigEntity implements IElementEntity<DataConfig> {

    private static final File DIR_CONFIG = new File(XMLUtil.CURRENT_PATH, "config");
    private static final File FILE_CONFIG = new File(DIR_CONFIG, "config.xml");

    private ConfigStore config;

    @Override
    public boolean add(DataConfig e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(DataConfig e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean save(Object... parent) {
        List<DataBaseElement> dataBases = (List<DataBaseElement>) parent[0];

        ConfigStore cs = new ConfigStore();
        List<DataConfig> bases = new ArrayList<>(dataBases.size());

        for (DataBaseElement e : dataBases) {

            DataConfig dc = new DataConfig(e.getName());
            if (e.getColor() != null) {
                dc.setColor(e.getColor().getRGB());
            }

            dc.setTableCount(e.getTables().size());

            bases.add(dc);
        }

        cs.setBases(bases);

        try {
            if (!DIR_CONFIG.exists()) {
                DIR_CONFIG.mkdir();
            }

            JAXBContext context = JAXBContext.newInstance(ConfigStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, XMLUtil.FORMATTED_OUTPUT);

            m.marshal(cs, XMLUtil.getFileOutputStream(FILE_CONFIG));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    @Override
    public boolean load(Object... parent) {

        List<DataBaseElement> dataBases = (List<DataBaseElement>) parent[0];

        config = null;

        if (!FILE_CONFIG.exists()) {
            return false;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(ConfigStore.class);
            Unmarshaller um = context.createUnmarshaller();

            config = (ConfigStore) um.unmarshal(getFileInputStream(FILE_CONFIG));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (config != null && config.getBases() != null) {

            for (DataBaseElement db : dataBases) {
                for (DataConfig c : config.getBases()) {
                    if (db.getName().equals(c.getName())) {
                        db.setColor(new Color(c.getColor()));
                    }
                }
            }

            return true;
        }

        return false;
    }

    public ConfigStore getConfig() {
        return config;
    }

    @Override
    public List<DataConfig> getList() {
        return config.getBases();
    }

    @Override
    public DataConfig findByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DataConfig> findBy(IEntityFilter filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addActionListern(ActionListener actionListener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
