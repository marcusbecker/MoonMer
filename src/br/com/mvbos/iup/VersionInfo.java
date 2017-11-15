/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.iup;

import java.util.Objects;

/**
 *
 * @author MarcusS
 */
public class VersionInfo {

    private final String appVersion;
    private String version;
    private String description;
    private String[] files;
    private boolean hasNewVersion;

    public VersionInfo(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getFiles() {
        return files == null ? new String[0] : files;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    public boolean isHasNewVersion() {
        return hasNewVersion;
    }

    public void setHasNewVersion(boolean hasNewVersion) {
        this.hasNewVersion = hasNewVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VersionInfo other = (VersionInfo) obj;
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "VersionInfo{" + "appVersion=" + appVersion + ", version=" + version + ", description=" + description + ", files=" + files + ", hasNewVersion=" + hasNewVersion + '}';
    }

}
