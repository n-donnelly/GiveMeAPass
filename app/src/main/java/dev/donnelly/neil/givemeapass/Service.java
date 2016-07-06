package dev.donnelly.neil.givemeapass;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Neil on 24/06/2016.
 */
public class Service {
    String name;
    int numCharacters;
    boolean wantSpecialChars;
    boolean wantNumbers;

    public Service(String name, int numCharacters, boolean wantSpecialChars, boolean wantNumbers){
        this.name = name;
        this.numCharacters = numCharacters;
        this.wantSpecialChars =wantSpecialChars;
        this.wantNumbers = wantNumbers;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getNumCharacters(){
        return numCharacters;
    }

    public void setNumCharacters(int numCharacters){
        this.numCharacters = numCharacters;
    }

    public boolean doesWantSpecialChars(){
        return wantSpecialChars;
    }

    public void setWantSpecialChars(){
        this.wantSpecialChars = wantSpecialChars;
    }

    public boolean doesWantNumbers() { return wantNumbers; }

    public void setWantNumbers(boolean wantNumbers) { this.wantNumbers = wantNumbers; }
}
