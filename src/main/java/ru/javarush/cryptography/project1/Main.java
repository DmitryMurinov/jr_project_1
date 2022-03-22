package ru.javarush.cryptography.project1;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        HashMap<String, String> params = ParametersHandler.handle(args);
        if(params.isEmpty()){
            return;
        }

        if("encode".equals(params.get("mode"))){
            Encode encode = new Encode();
            encode.process(params);
        }

        if("decode".equals(params.get("mode"))){
            Decode decode = new Decode();
            decode.process(params);
        }

        if("crack".equals(params.get("mode"))){
            Crack crack = new Crack();
            crack.process(params);
        }

    }

}