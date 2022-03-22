package ru.javarush.cryptography.project1.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Alphabet {

    /**
     * Don't change this string, or further decode will not be possible.
     */
    private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
            ".,\":-!? ";

    public static final List<Character> PUNCTUATION_MARKS = Arrays.asList(',', '.', '!', '?');

    private HashMap<Character, Character> mapping = new HashMap<>();

    public HashMap<Character, Character> getMapping(){
        return mapping;
    }

    public void initEncodeKeySet(int offset){
        if(offset < 0){
            offset = -offset;
        }

        if(offset > CODES.length()){
            offset = offset % (CODES.length() - 1);
        }

        char[] keys = CODES.toCharArray();
        for (int i = 0; i < CODES.length(); i++) {
            if(i + offset < CODES.length()){
                mapping.put(keys[i], keys[i + offset]);
            } else {
                mapping.put(keys[i], keys[i + offset - CODES.length()]);
            }
        }
    }

    public void initDecodeKeySet(int offset){
        if(offset < 0){
            offset = -offset;
        }

        if(offset > CODES.length()){
            offset = offset % (CODES.length() - 1);
        }

        char[] keys = CODES.toCharArray();
        for (int i = 0; i < CODES.length(); i++) {
            if(i + offset < CODES.length()){
                mapping.put(keys[i + offset], keys[i]);
            } else {
                mapping.put(keys[i + offset - CODES.length()], keys[i]);
            }
        }
    }


}
