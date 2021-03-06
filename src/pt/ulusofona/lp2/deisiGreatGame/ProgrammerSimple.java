package pt.ulusofona.lp2.deisiGreatGame;

import java.io.Serializable;

public class ProgrammerSimple implements Comparable, Serializable {

    private String name;
    private int pos;

    public ProgrammerSimple(String name, int pos){
        this.name = name;
        this.pos = pos;
    }

    public String getName() {
        return this.name;
    }

    public int getPos(){
        return this.pos;
    }

    @Override
    public int compareTo(Object o) {
        return  this.name.compareTo(((ProgrammerSimple) o).getName()) ;
    }
}
