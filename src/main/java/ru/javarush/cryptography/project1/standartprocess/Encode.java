package ru.javarush.cryptography.project1.standartprocess;

import ru.javarush.cryptography.project1.data.Alphabet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Encode {

    public void process(HashMap<String, String> params){

        Alphabet alphabet = new Alphabet();
        alphabet.initEncodeKeySet(Integer.parseInt(params.get("offset")));

        HashMap<Character, Character> mapping = alphabet.getMapping();

        try {
            Path path = Path.of(params.get("path"));

            long fileSize = Files.size(path) / 1024 / 1024;

            if(fileSize > 10){
                System.out.println("File size exceeds 10 MB");
                return;
            }

            char[] data = Files.readString(path).toCharArray();
            for (int i = 0; i < data.length; i++) {
               if(mapping.containsKey(data[i])) {
                   data[i] = mapping.get(data[i]);
               }
            }

            Files.writeString(path, String.valueOf(data));

            System.out.println("Encoded file saved.");
        } catch (IOException e){
            System.out.printf("Error occurred during file encoding, message: %s, stacktrace: %s",
                    e.getMessage(), e);
        }
    }

}
