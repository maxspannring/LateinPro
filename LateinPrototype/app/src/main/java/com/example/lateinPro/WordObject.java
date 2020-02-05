package com.example.lateinPro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class WordObject implements Serializable {
    ArrayList<HashMap> EntryList = new ArrayList<>();


    public ArrayList<HashMap> getEntryList() { return EntryList; }
    public void setEntryList(ArrayList<HashMap> entryList) { EntryList = entryList; }
    //public void addEntryList(ArrayList<HashMap> al){EntryList.add();}
}
