package sample;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PairEntry {
    private SimpleStringProperty  name;
    private SimpleIntegerProperty count;

    public PairEntry(String name, int count){
        this.name = new SimpleStringProperty(name);
        this.count = new SimpleIntegerProperty(count);
    }

    public String getName(){
        return name.get();
    }

    public Integer getCount(){
        return count.get();
    }
}
