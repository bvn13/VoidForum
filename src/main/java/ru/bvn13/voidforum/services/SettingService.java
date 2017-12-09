package ru.bvn13.voidforum.services;

import java.io.Serializable;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
public interface SettingService {
    Serializable get(String key);
    Serializable get(String key, Serializable defaultValue);
    void put(String key, Serializable value);
}
