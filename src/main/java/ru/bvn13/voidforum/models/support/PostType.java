package ru.bvn13.voidforum.models.support;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
public enum PostType {
    PAGE("Page"),
    POST("Post");

    private String name;

    PostType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId(){
        return name();
    }

    @Override
    public String toString() {
        return getName();
    }
}
