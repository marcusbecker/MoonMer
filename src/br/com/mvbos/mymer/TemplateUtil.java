/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.field.Field;

/**
 *
 * @author MarcusS
 */
public class TemplateUtil {

    private final StringBuilder clip = new StringBuilder(100);

    public void clear() {
        clip.delete(0, clip.length());
    }

    public StringBuilder create(int type, TableElement t) {
        clear();
        switch (type) {
            case 1:
                clip.append("FIND FIRST ").append(t.getName()).append(" NO-LOCK NO-ERROR.");
                clip.append("\r\nDISP ").append(t.getName()).append(".");
                break;
            case 2:
                clip.append("FOR EACH ").append(t.getName()).append(" NO-LOCK:");
                clip.append("\r\nDISP ").append(t.getName()).append(".");
                clip.append("\r\nEND.");
                break;
            case 3:
                clip.append("CREATE ").append(t.getName()).append(".");
                clip.append("\r\nASSIGN\r\n");
                for (Field f : t.getFields()) {
                    clip.append(t.getName()).append(".").append(f.getName()).append(" = ").append(f.getType()).append("\r\n");
                }
                clip.append(".");
                break;
            case 4:
                for (Field f : t.getFields()) {
                    clip.append(f.getName()).append("\r\n");
                }
                break;
            case 5:
                clip.append("DEF TEMP-TABLE ").append(t.getName()).append(" NO-UNDO");
                for (Field f : t.getFields()) {
                    clip.append("\r\nFIELD ").append(f.getName()).append(" AS ").append(f.getType());
                }
                clip.append("\r\n.");
                break;
        }

        return clip;
    }
}
