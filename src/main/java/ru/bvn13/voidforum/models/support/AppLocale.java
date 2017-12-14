package ru.bvn13.voidforum.models.support;

/**
 * Created by bvn13 on 12.12.2017.
 */
public enum AppLocale {

    EN("en"),
    RU("ru");

    private String name;

    AppLocale(String name){
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

    public static AppLocale valueOfOrDefault(String name) {
        try {
            return AppLocale.valueOf(name);
        } catch (IllegalArgumentException iae) {
            return AppLocale.EN;
        }
    }
}
