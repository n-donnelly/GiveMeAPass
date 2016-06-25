package dev.donnelly.neil.givemeapass;

/**
 * Created by Neil on 24/06/2016.
 */
public class Service {
    String name;
    int numCharacters;
    boolean wantSpecialChars;

    public Service(String name, int numCharacters, boolean wantSpecialChars){
        this.name = name;
        this.numCharacters = numCharacters;
        this.wantSpecialChars =wantSpecialChars;
    }

    public String getName(){
        return name;
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
}
