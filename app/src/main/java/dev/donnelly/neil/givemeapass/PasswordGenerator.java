package dev.donnelly.neil.givemeapass;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Neil on 24/06/2016.
 *
 * Creates the password based on the Pass Phrase, Password and Service
 */
public class PasswordGenerator {

    ArrayList<Integer> symList;

    public PasswordGenerator(){
        //List is used to allow specific special characters be added to the password
        symList = new ArrayList<Integer>();
        symList.add(33);//!
        symList.add(35);//#
        symList.add(36);//$
        symList.add(42);//*
        symList.add(45);//-
        symList.add(95);//_
        symList.add(63);//?
    }

    //Builds password from user's own password, the service and the pass phrase
    //Also includes how long the password should be and whether there should be special characters
    //and/or numbers
    public String buildPassword(String password, String service, String pass_phrase,
                                int num_chars, boolean inc_spec, boolean inc_nums){
        try {
            //Hash the three input strings to ensure they can't be discovered
            //by reverse engineering my algorithm
            password = hashString(password);
            service = hashString(service);

            //Mix up the password and pass phrase
            String[] strings = {password,pass_phrase};
            String mixUp = createMixedUpString(strings);

            //Encode the mixed up string with the name of the service
            String encodedStr = encodeString(service,mixUp);

            //Now use the encoded string to create the password
            return createPassword(encodedStr, inc_nums, inc_spec, num_chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    //Hashes the given string input with the MD5 algorithm and outputs the Hex string
    private String hashString(String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < byteData.length; i++){
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    //Use the service string to encode the given string
    private String encodeString(String service, String mix) throws UnsupportedEncodingException{
        byte[] serviceBytes = service.getBytes();
        byte[] mixBytes = mix.getBytes();

        //Loops over the mix bytes and multiplies it by the int values for the service bytes
        //The chances are the mix bytes array should be longer than the service bytes so we loop
        //back around for the service bytes values
        for(int i = 0; i<mixBytes.length;i++){
            mixBytes[i] =
                    (byte) (((int)mixBytes[i]*(int)serviceBytes[i%(serviceBytes.length-1)])%122);
        }

        //Return the resulting array as a string
        return new String(mixBytes,"UTF8");
    }

    //Accept an array of strings and mix the byte arrays of the strings up to make
    //reverse engineering the algorithm
    private String createMixedUpString(String[] strings) throws UnsupportedEncodingException{
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

    //Using the given string, and flags, create a valid password
    private String createPassword(String msg, boolean wantNums, boolean wantSpecs, int desLength)
            throws UnsupportedEncodingException{
        String new_password = "";

        //The startPos is used to help ensure we get a valid ASCII value from the integers
        int startPos = 65;
        //The modVal acts as the limit for what we accept as a valid ASCII value
        int modVal = 122;
        //If we want numbers included, we move the startPos back to include numbers
        startPos = wantNums ? 48 : startPos;
        //If we want special characters included, we move the startPos back again to include some
        //special characters
        startPos = wantSpecs ? 32 : startPos;
        modVal = modVal - startPos;

        //Get the byte values for the msg and create a byte array of the same length
        //so we can store the resulting password
        byte[] msgBytes = msg.getBytes(Charset.forName("UTF8"));
        msgBytes = trimByteArray(msgBytes);
        byte[] passBytes = new byte[msgBytes.length];

        //Loop through the msgBytes array
        for(int i = 0; i < msgBytes.length-1;i++){
            //As the unsigned byte values when cast to signed integers can be negative,
            // we use their absolute values
            int val = Math.abs((int)msgBytes[i]);
            int val2 = Math.abs((int)msgBytes[(msgBytes.length-1)-i]);
            //We add together values from the msg bytes array to increase the randomness
            //of the generated password. We then mod the value by modVal so it matches the
            // desired range then add the startPos to ensure it begins with the valid ASCII values
            val+= val2;
            val = (val%modVal)+startPos;

            //We then validate the generated value to ensure it matches a valid ASCII value
            val = validVal(val, startPos, wantNums, wantSpecs);

            //Add the value to the passBytes array
            passBytes[i] = (byte)val;
        }

        //Create the password string
        new_password = new String(passBytes, "UTF8");

        //If somehow beyond reason, the password length is below the required length, just say so
        if(desLength > new_password.length())
            System.err.println("Uh oh, this shouldn't happen");
        else
            //Limit the length of the new password
            new_password = new_password.substring(0,desLength);

        return new_password;
    }

    private byte[] trimByteArray(byte[] bytes){
        int i = bytes.length-1;
        while(i >= 0 && bytes[i] == 0){
            i--;
        }
        return Arrays.copyOf(bytes, i);
    }

    private int validVal(int v, int startPos, boolean wantNums, boolean wantSpecs){
        //If we allow special characters and the value is part of the symbol list then allow
        if(wantSpecs && symList.contains(v)){
            return v;
        } else if(v > 90 && v < 97){
            //If the val is in the range of 90 to 97 and not in allowable symbols, then redo method
            //with new value
            if(symList.contains(v) && wantSpecs)
                return v;
            v = (v/2)+(122-startPos);
        } else if(v > 57 && v < 65) {
            //If the val is in the range of 90 to 97 and not in allowable symbols, then redo method
            //with new value
            if(symList.contains(v) && wantSpecs)
                return v;
            v = (v/2)+(122-startPos);
        } else if(v < 48){
            //If less than the allowed value, then add the startPos and redo
            v += startPos;
        } else if(v < 58 && !wantNums){
            //If less than 58 and does not want nums, then add startPos and redo
            v += startPos;
        } else if(v < startPos){
            v += startPos;
        } else if(v > 122){
            v = (v%122);
        } else {
            //Return the value as it is valid
            return v;
        }
        //Recursively validate value
        return validVal(v,startPos,wantNums,wantSpecs);
    }
}
