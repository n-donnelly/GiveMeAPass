package dev.donnelly.neil.givemeapass;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Neil on 24/06/2016.
 */
public class PasswordGenerator {

    ArrayList<Integer> symList;

    public PasswordGenerator(){
        symList = new ArrayList<Integer>();
        symList.add(33);
        symList.add(35);
        symList.add(36);
        symList.add(42);
        symList.add(45);
        symList.add(95);
        symList.add(63);
    }

    public String buildPassword(String password, String service, String pass_phrase, int num_chars, boolean inc_spec, boolean inc_nums){
        try {
            password = hashString(password);
            service = hashString(service);
            pass_phrase = hashString(pass_phrase);
            String[] strings = {password,pass_phrase};
            String mixUp = createMixedUpString(strings);
            String encodedStr = encodeString(service,mixUp);
            return createPassword(encodedStr, inc_nums, inc_spec, num_chars);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Something went wrong with the hashing - apparently MD5 doesn't exist");
            return "";
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public String hashString(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < byteData.length; i++){
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public String encodeString(String service, String mix) throws UnsupportedEncodingException{
        byte[] serviceBytes = service.getBytes();
        byte[] mixBytes = mix.getBytes();

        for(int i = 0; i<serviceBytes.length;i++){
            serviceBytes[i] = (byte) (((int)serviceBytes[i]*(int)mixBytes[i%(mixBytes.length-1)])%122);
        }
        return new String(serviceBytes,"UTF8");
    }

    public String createMixedUpString(String[] strings) throws UnsupportedEncodingException{
        int length = strings.length;
        int longestStrLength = strings[0].length();

        byte[][] bytes = new byte[length][];

        for(int i = 0; i < length; i++){

            String str = strings[i];
            if (str.length() > longestStrLength)
                longestStrLength = str.length();

            if(i%2!=0){
                String resultStr = "";
                for(int k = str.length()-1; k >= 0; k--){
                    resultStr += str.charAt(k);
                }
                bytes[i] = resultStr.getBytes();
            } else {
                bytes[i] = str.getBytes();
            }
        }

        byte[] completeBytes = new byte[length*longestStrLength];
        int j = 0;
        for(int i = 0; i < longestStrLength; i++){
            for(int k = 0; k < length;k++){
                if(i < bytes[k].length){
                    completeBytes[j] = bytes[k][i];
                    j++;
                }
            }
        }

        return new String(completeBytes, "UTF8");
    }

    public String createPassword(String msg, boolean wantNums, boolean wantSpecs, int desLength) throws UnsupportedEncodingException{
        String new_password = "";
        int startPos = 65;
        int modVal = 122;
        startPos = wantNums ? 48 : startPos;
        startPos = wantSpecs ? 32 : startPos;
        modVal = modVal - startPos;
        byte[] msgBytes = msg.getBytes(Charset.forName("UTF8"));
        byte[] passBytes = new byte[msgBytes.length];

        int curIndex = 0;
        for(int i = 0; i < msgBytes.length-1;i++){
            int val = Math.abs((int)msgBytes[i]);
            int val2 = Math.abs((int)msgBytes[(msgBytes.length-1)-i]);
            val+= val2;
            val = (val%modVal)+startPos;

            val = validVal(val, startPos, wantNums, wantSpecs);

            passBytes[curIndex] = (byte)val;
            curIndex++;
        }

        new_password = new String(passBytes, "UTF8");
        if(desLength > new_password.length())
            System.err.println("Uh oh, this shouldn't happen");
        else
            new_password = new_password.substring(0,desLength);

        return new_password;
    }

    private int validVal(int v, int startPos, boolean wantNums, boolean wantSpecs){
        if(wantSpecs && symList.contains(v)){
            return v;
        } else if(v > 90 && v < 97){
            if(v==95 && wantSpecs)
                return v;
            v = (v/2)+(122-startPos);
        } else if(v > 57 && v < 65) {
            if(v==63 && wantSpecs)
                return v;
            v = (v/2)+(122-startPos);
        } else if(v < 48){
            v += startPos;
        } else if(v < 58 && !wantNums){
            v += startPos;
        } else {
            return v;
        }
        return validVal(v,startPos,wantNums,wantSpecs);
    }
}
