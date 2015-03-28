/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modifiedvigenerecipher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Laboratorio
 */
public class Decoder implements Runnable{
    private final JProgressBar bar;
    private final JLabel label;
    private final CallBack call;
    private Byte[] data;
    
    public Decoder(JProgressBar bar, JLabel label, CallBack call) {
        this.bar = bar;
        this.label = label;
        this.call = call;
    }
    
    public void getTextAsyncronous(byte[] data){
        this.data = new Byte[data.length];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
        }
        label.setText("Preparing Data...");
        bar.setValue(0);
        Thread hilo = new Thread(this);
        hilo.start();
    }
    
    private void SetText(final String text, final int value){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (text!= null)
                    label.setText(text);
                if (value > 0)
                    bar.setValue(value);
            }
        });
    }

    @Override
    public void run() {
        //Aqui se escribe el metodo para decifrarlo, la palabra cifrada es la variable 'data' es un vector
        // de bytes
        
    }
    
    
    public static class Answer{
        public String Text;
        public String key;
    }
}
