package ru.bvn13.voidforum.models.support;

public enum CommentFormat {

    HTML("Html"),
    MARKDOWN("Markdown");

    private String name;

    CommentFormat(String name){
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
