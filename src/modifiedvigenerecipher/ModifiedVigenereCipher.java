/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modifiedvigenerecipher;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author hsaldarriaga
 */
public class ModifiedVigenereCipher {
    static boolean IsValidASCII(String key_hex) {
        for (int i = 0; i < key_hex.length()/2; i++) {
            String bb = key_hex.substring(i*2, i*2+2);
            int value = Integer.parseInt(bb, 16);
            if (value > 127)
                return false;
        }
        return true;
    }
    static String ValidateHex(String key_hex) {
        if (key_hex.length() % 2 !=0) {
            StringBuilder build = new StringBuilder(key_hex);
            build.insert(key_hex.length()-1, "0");
            return build.toString();
        }  
        return key_hex;
    }
    
    static byte[] getCipherText(String text, String key_hex) {
        byte[] kbytes = new byte[key_hex.length()/2];
        for (int i = 0; i < kbytes.length; i++) {
            String hex_value = key_hex.substring(i*2, i*2 + 2);
            kbytes[i] = (byte)Integer.parseInt(hex_value, 16);
        }
        int trealsize = text.length();
        int tsize = text.length(), ksize = key_hex.length()/2;
        int padding = ((tsize/ksize) + 1)*ksize - tsize;
        if (padding != ksize) {
            for (int i = 0; i < padding; i++) {
                text +=" ";
            }
        }
        tsize = text.length();
        byte[] tbytes, cbytes = new byte[trealsize];
        try {
            tbytes = text.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
        boolean breaking = false;
        for (int i = 0; i < tsize/ksize && !breaking; i++) {
            for (int j = 0; j < ksize && !breaking; j++) {
                if (i*ksize + j < trealsize)
                    cbytes[i*ksize + j] = (byte) (tbytes[i*ksize+j] ^ kbytes[j]);
                else 
                   breaking = true;
            }
        }
        return cbytes;
    }
}
