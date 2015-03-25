/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modifiedvigenerecipher;

import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Laboratorio
 */
public class Decoder {
    JProgressBar bar;
    JLabel label;
    CallBack call;
    
    public Decoder(JProgressBar bar, JLabel label, CallBack call) {
        this.bar = bar;
        this.label = label;
        this.call = call;
    }
    
    public void getTextAsyncronous(final byte[] data){
        Thread hilo = new Thread(new Runnable() {

            @Override
            public void run() {
                final Answer answer = new Answer();
                //Code
                SetText("Preparando Buffer de Valores...");
                UpgradeValue(5);
                ArrayList<Byte> key_pos_let = new ArrayList<Byte>();
                for (int i = 0; i < 128; i++) {
                    key_pos_let.add((byte)i);
                }
                boolean found = false;
                int size = 1;
                while (!found) {
                    for (int j = 0; j < key_pos_let.size(); j++) {
                        int i=0;
                        while (i*size < data.length) {
                            byte resu = (byte) (data[i*size] ^ key_pos_let.get(j));
                            i++;
                        }
                    }
                    
                    size++;
                }
                //Code
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        call.onFinish(answer);
                    }
                });
            }
        });
        hilo.start();
    }
    
    private void SetText(final String text){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                label.setText(text);
            }
        });
    }
    
    private void UpgradeValue(final int value) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                bar.setValue(value);
            }
        });
    }
    
    public static class Answer{
        public String Text;
        public String key;
    }
}
