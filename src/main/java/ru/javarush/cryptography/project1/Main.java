package ru.javarush.cryptography.project1;

import ru.javarush.cryptography.project1.crack.Crack;
import ru.javarush.cryptography.project1.standartprocess.Decode;
import ru.javarush.cryptography.project1.standartprocess.Encode;
import ru.javarush.cryptography.project1.utils.ParametersHandler;

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
