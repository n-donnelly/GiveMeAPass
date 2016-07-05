package dev.donnelly.neil.givemeapass;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Neil on 27/06/2016.
 */
public class PassPhraseController {

    public String hashPassPhrase(String passPhrase) throws NoSuchAlgorithmException{

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] data = passPhrase.getBytes();
        md.update(data,0,0);
        byte[] shaBytes = md.digest();

        StringBuffer strBuff = new StringBuffer();
        for (int i = 0; i < shaBytes.length; i++) {
            strBuff.append(Integer.toString((shaBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return strBuff.toString();
    }
}
