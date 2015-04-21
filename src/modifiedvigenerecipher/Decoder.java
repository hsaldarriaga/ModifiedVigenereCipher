/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modifiedvigenerecipher;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final HashMap<Character, Float> LetterFrequency;
    private float error;
    private boolean found;
    private double Count;
    private int Total;
    private byte[] TempSol = null;
    public Decoder(JProgressBar bar, JLabel label, CallBack call) {
        this.bar = bar;
        this.label = label;
        this.call = call;
        error = Float.MAX_VALUE;
        LetterFrequency = new HashMap<>();
        LetterFrequency.put('a', 8.167f);	
        LetterFrequency.put('b', 1.492f);	
        LetterFrequency.put('c', 2.782f);	
        LetterFrequency.put('d', 4.253f);	
        LetterFrequency.put('e', 12.702f);	
        LetterFrequency.put('f', 2.228f);
        LetterFrequency.put('g', 2.015f);	
        LetterFrequency.put('h', 6.094f);	
        LetterFrequency.put('i', 6.966f);	
        LetterFrequency.put('j', 0.153f);	
        LetterFrequency.put('k', 0.772f);	
        LetterFrequency.put('l', 4.025f);	
        LetterFrequency.put('m', 2.406f);	
        LetterFrequency.put('n', 6.749f);	
        LetterFrequency.put('o', 7.507f);	
        LetterFrequency.put('p', 1.929f);	
        LetterFrequency.put('q', 0.095f);	
        LetterFrequency.put('r', 5.987f);	
        LetterFrequency.put('s', 6.327f);	
        LetterFrequency.put('t', 9.056f);	
        LetterFrequency.put('u', 2.758f);	
        LetterFrequency.put('v', 0.978f);	
        LetterFrequency.put('w', 2.360f);	
        LetterFrequency.put('x', 0.150f);	
        LetterFrequency.put('y', 1.974f);	
        LetterFrequency.put('z', 0.074f);	
        //http://en.wikipedia.org/wiki/Letter_frequency
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
        SwingUtilities.invokeLater(() -> {
            if (text!= null)
                label.setText(text);
            if (value > 0)
                bar.setValue(value);
        });
    }

    @Override
    public void run() {
        //Aqui se escribe el metodo para decifrarlo, la palabra cifrada es la variable 'data' es un vector
        // de bytes
        final Answer answer = new Answer();
        //----------------------------------
        int patronSize = 2;
        int MaxPatronSize = 6; //http://www.wolframalpha.com/input/?i=average+english+word+length
        boolean Coincidence = false;
        found = false;
        Count =0;
        Total = 0;
        ArrayList<Integer> PossibleKey = new ArrayList<>();
        ArrayList<Integer> Repetition = new ArrayList<>();
        SetText("Buscando Patrones..", -1);
        //Buscan todos los patrones de tamaño minimo patronSize y maximo MaxPatronSize
        while (!Coincidence)
        {
            patronSize ++;
            for (int i = 0; i < data.length - patronSize; i++) {
                Byte[] patron = new Byte[patronSize];
                for (int j = 0; j < patron.length; j++) {
                    patron[j] = data[i+j];
                }
                ArrayList<Integer> Pos_Patron = new ArrayList<>();
                for (int j = 0; j < data.length - patronSize; j++) {
                    Byte[] temp = new Byte[patronSize];
                    for (int l = 0; l < patronSize; l++) {
                        temp[l] = data[j+l];
                    }
                    if (VEquals(patron, temp))
                    {
                        Pos_Patron.add(j);
                        j+=patronSize-1;
                    }
                }
                if (Pos_Patron.size() > 1) {
                    int num = MCD(Pos_Patron); //MCD entre las posiciones que repitió cierto patron
                    int R = Pos_Patron.size();
                    if (num > 2) { //Restriccion de longtidu de la llave mayor que dos
                        Repetition.add(R); // Posiciones donde se repitió
                        PossibleKey.add(num); // Possible llaves
                    }
                }
                if (Mayor(Repetition) > data.length*0.012){ // Se repitio tantas veces que es una alta probabilidad de que sea la llave
                    Coincidence = true;
                    break;
                }
            }
            if (patronSize > MaxPatronSize)
                break;
        }
        SetText("Buscando Longitud de la llave", 5);
        ArrayList<Integer> escaneados = new ArrayList<>();
        int media = 0;
        int k= 0;
        ArrayList<PossibleKeySize> sizes = new ArrayList<>();
        /**
         * llaves posibles
        1 2 3 6 7 10 13

        1 +2 +3 +6+ 7+ 10 +13 = 42

        42 / 7 = 6

        promedio = 6 

        las nuevas posibles llaves son
        7 10 13
         */
        for (int i = 0; i < PossibleKey.size(); i++) {
            if (!escaneados.contains(PossibleKey.get(i)))
            {
                escaneados.add(PossibleKey.get(i));
                int sum= 0;
                for (int j = i; j < PossibleKey.size(); j++) {
                    if (Objects.equals(PossibleKey.get(j), PossibleKey.get(i))) {
                        sum++;
                    }
                }
                PossibleKeySize s = new PossibleKeySize();
                s.Longitud_Llave = PossibleKey.get(i);
                s.Repet_Llave = sum;
                sizes.add(s);
                media += sum;
                k++;
            }
        }
        media /=k;
        SetText("Buscando llave..", 10);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).Repet_Llave < media) {
                sizes.remove(i); i--;
            }
        }
        float Error = Float.MAX_VALUE;
        ArrayList<Byte> values = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            values.add((byte)i);
        }
        byte[] Solution = null;
        Total = 10;
        for (int l = 0; l < sizes.size() && !found; l++) {
            PossibleKeySize size = sizes.get(l);
            ArrayList<Byte> key = new ArrayList<>();
            try {
                Llave(size.Longitud_Llave, size.Longitud_Llave, values, key, sizes.size()); // Busca todas las posibles llaves
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (error < Error) {
                Solution = TempSol;
                Error = error;
            }
            Total = 10 + (70 / (sizes.size() + 1))*(l+1);
            Count = 0;
            SetText(null, Total);
        }
        SetText("Decifrando Texto..", 90);
        String text = "";
        for (int i = 0; i < data.length; i++) { // Descifro el codigo
            for (int j = 0; j < Solution.length && i + j < data.length; j++) {
                text += ((char)(data[i+j] ^ Solution[j]))+"";
            }
            i += Solution.length - 1; 
            SetText(null, 90 + (int)(((double)i/data.length)*10d));
        }
        answer.Text = text;
        answer.key ="";
        for (int i = 0; i < Solution.length; i++) {
            answer.key += String.format("%02x", Solution[i]);
        }
        //----------------------------------
        SwingUtilities.invokeLater(() -> {
            call.onFinish(answer); // Muestra el resultado
        });
    }
    public void Llave(int LongKey, int templongkey, ArrayList<Byte> Values, ArrayList<Byte> key, int numkeys) throws UnsupportedEncodingException
    {
        if (templongkey > 0 && !found)
        {
            for (int i = 0; i < Values.size() && !found; i++) {
                Byte r = Values.get(i);
                if (ValidChar(r, LongKey - templongkey, LongKey)){ // Mira si la llave da caracteres no imprimibles
                    ArrayList<Byte> pkey = new ArrayList<>(key);
                    pkey.add(r); // Prueba todas las posibilidades
                    Llave(LongKey, templongkey - 1, Values, pkey, numkeys);
                }
            }
        } else {
           if (!found) { // Hay una llave posible
                Count += 1;
                double doublevalue = (Count/(Math.pow(LongKey, Values.size()*0.055)))*(70.0d/numkeys);
                int value = (int) (Total + (doublevalue < (70.0d/numkeys) ? (int)(doublevalue) : (70.0d/numkeys)));
                SetText(null, value);
                float errorA = getError(key); // Obtengo la suma de porcentaje de repeticion de cada letra del ingles
                if (errorA < error) // Se reemplaza por una mejor llave
                {
                    TempSol = new byte[LongKey];
                    for (int i = 0; i < LongKey; i++) {
                        TempSol[i] = key.get(i);
                    }
                    error = errorA;
                    if (error < 25) // Si el error es menor que 25 entonces, posiblemente ya no se pueda encontrar una llave mejor
                        found = true;
                }
            }
        }
    }
    
    public float getError(ArrayList<Byte> key)
    {
        int j = 0;
        ArrayList<Character> chars = new ArrayList<>();
        while (j < data.length)
        {
            for (int i = 0; i < key.size() && i+j < data.length; i++) {
               chars.add((char)(data[j+i] ^ key.get(i)));
            }
            j += key.size();
        }
        Float errorA = 0f;
        HashMap<Character, Integer> repeticiones = new HashMap<>();
        for (Character char1 : chars) {
            if (repeticiones.containsKey(char1)) {
                repeticiones.put(char1, repeticiones.get(char1) + 1);
            } else {
                repeticiones.put(char1, 1);
            }
        }
        errorA = repeticiones.entrySet().stream().map((m) -> {
            float value = m.getValue() / (float)chars.size();
            float rest = Math.abs((value*100f) - LetterFrequency.getOrDefault(m.getKey(), (value*100f)));
            return rest;
        }).map((rest) -> rest).reduce(errorA, (accumulator, _item) -> accumulator + _item);
        return errorA;
    }
    public boolean ValidChar(Byte r, int offset, int logKey)
    {
        for (int i = offset; i < data.length; i++) {
            byte resu = (byte) (data[i] ^ r);
            if (resu < 23 || resu > 126)
                return false;
            i+=logKey -1;
        }
        return true;
    }
    public boolean VEquals(Byte[] data, Byte[] data1)
    {
        if (data.length != data1.length)
            return false;
        for (int i = 0; i < data.length; i++) {
            if (!Objects.equals(data[i], data1[i])){
                return false;
            }
        }
        return true;
    }
    
    public int MCD(ArrayList<Integer> data)
    {
        int resu = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            resu = gcd(resu, data.get(i));
        }
        return resu;
    }
    public static int gcd(int p, int q) {
        while (q != 0) {
            int temp = q;
            q = p % q;
            p = temp;
        }
        return p;
    }
    public static int Mayor(ArrayList<Integer> data)
    {
        if (data.size()> 0) {
        int Mayor = data.get(0);
        for (Integer d : data)
        {
            if (d > Mayor)
            {
                Mayor = d;
            }
        }
        return Mayor;
        } else {
            return 0;
        }
    }
    public static class Answer{
        public String Text;
        public String key;
    }
    
    public static class PossibleKeySize {
        public int Longitud_Llave;
        public int Repet_Llave;
    }
}
