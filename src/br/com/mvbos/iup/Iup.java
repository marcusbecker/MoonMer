/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.iup;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MarcusS
 */
public class Iup {

    private final String path;
    private static final String DOT = ".";
    private static final String PREFIX = "iup_";
    private static final String PROP_FILE_DELIMITERS = ",";
    private static final String PROP_FILES = "files";
    private static final String PROP_VERSION = "version";
    private static final String PROP_APP_VERSION = "app_version";
    private static final String PROP_UPDATE_PATH = "updatePath";
    private static final String PROP_DESCRIPTION = "description";
    private static final String INFO_FILE_NAME = "info.txt";

    private int updateErrors;
    private final VersionInfo info;

    public Iup(String version) {
        info = new VersionInfo(version);

        IupProperties.set(PROP_APP_VERSION, info.getAppVersion());
        IupProperties.save();

        path = IupProperties.get(PROP_UPDATE_PATH, "");

        if (path.isEmpty()) {
            return;
        }

        checkVersion();
    }

    private void checkVersion() {
        try {
            Properties p = new Properties();
            File f = new File(path, INFO_FILE_NAME);
            if (!f.exists()) {
                return;
            }

            p.load(new FileInputStream(f));
            String lastVersion = p.getProperty(PROP_VERSION, "0");
            String description = p.getProperty(PROP_DESCRIPTION, "0");
            String[] names = p.getProperty(PROP_FILES, "").split(PROP_FILE_DELIMITERS);

            info.setFiles(names);
            info.setVersion(lastVersion);
            info.setDescription(description);

        } catch (Exception e) {
            Logger.getLogger(Iup.class.getName()).log(Level.WARNING, null, e);
        }
    }

    public boolean hasUncompletedWork() {
        final String version = IupProperties.get(PROP_VERSION, "0");

        File dir = new File(DOT);
        File[] pendentFiles = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return /*f.isFile() &&*/ f.getName().startsWith(PREFIX);
            }
        });

        int copyCount = pendentFiles.length;
        int lenPrefix = (PREFIX + version).length();

        for (File f : pendentFiles) {
            try {
                String newName = f.getName().substring(lenPrefix);
                File newFile = new File(DOT, newName);

                if (newFile.exists()) {
                    Files.delete(newFile.toPath());
                }

                f.renameTo(newFile);
                copyCount--;
            } catch (Exception e) {
                Logger.getLogger(Iup.class.getName()).log(Level.WARNING, null, e);
            }
        }

        return copyCount != 0;
    }

    public boolean hasNewVersion() {
        return info.getAppVersion() != null && !info.getAppVersion().equals(info.getVersion());
    }

    public void updateNewVersion(String currentVersion) {
        updateErrors = 0;

        if (currentVersion == null) {
            return;
        }

        try {
            String[] names = info.getFiles();

            for (String s : names) {
                File source = new File(path, s);
                File target = new File(DOT, s);

                if (!source.exists()) {
                    continue;
                }

                try {
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException ioe) {
                    updateErrors++;
                    target = new File(DOT, PREFIX + currentVersion + s);
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    Logger.getLogger(Iup.class.getName()).log(Level.WARNING, null, ioe);
                }
            }

            IupProperties.set(PROP_VERSION, info.getVersion());
            IupProperties.save();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Iup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Iup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        final String version = IupProperties.get(PROP_APP_VERSION, "0");
        final Iup iup = new Iup(version);

        try {
            final FileWriter fw = new FileWriter(new File(DOT, "iup.log"));
            echo(fw, "Start " + Calendar.getInstance().getTime());

            for (int i = 0; i < 5; i++) {
                if (!iup.hasUncompletedWork()) {
                    iup.updateNewVersion(version);
                    if (iup.getUpdateErrors() == 0) {
                        break;
                    }

                } else {
                    try {
                        Thread.sleep(1000 * 5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Iup.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            echo(fw, "End   " + Calendar.getInstance().getTime());
            fw.close();

            Runtime.getRuntime().exec("java -jar MoonMer.jar", null, new File("."));

        } catch (IOException ex) {
            Logger.getLogger(Iup.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.exit(0);
        }
    }

    private static void echo(FileWriter fw, String string) throws IOException {
        fw.write(string);
        fw.write(System.lineSeparator());
    }

    public int getUpdateErrors() {
        return updateErrors;
    }

    public VersionInfo getNewVerionInfo() {
        return info;
    }

    public void setVersion() {

    }

}
