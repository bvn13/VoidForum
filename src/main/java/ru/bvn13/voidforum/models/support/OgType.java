package ru.bvn13.voidforum.models.support;

public enum OgType {

    WEBSITE("website"),
    ARTICLE("article");

    private String name;

    OgType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getId() {
        return name();
    }

    @Override
    public String toString() {
        return getName();
    }

}
