package ru.javarush.cryptography.project1.crack;

import ru.javarush.cryptography.project1.data.Alphabet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Crack {

    public void process(HashMap<String, String> params){
        if("bruteforce".equals(params.get("crackMode"))){
            bruteforce(params);
        }

        if("statistics".equals(params.get("crackMode"))){
            statistics(params);
        }
    }

    private void statistics(HashMap<String, String> params) {
        try {
            Path path = Path.of(params.get("path"));

            long fileSizeKB = Files.size(path) / 1024;

            if(fileSizeKB < 10){
                System.out.println("Sample file less than 10 KB");
                return;
            }

            long fileSizeMB = Files.size(path) / 1024 / 1024;

            if(fileSizeMB > 10){
                System.out.println("File size exceeds 10 MB");
                return;
            }

            Path samplePath = Path.of(params.get("samplePath"));

            long sampleSize = Files.size(samplePath) / 1024 / 1024;

            if(sampleSize > 10){
                System.out.println("Sample file size exceeds 10 MB");
                return;
            }

            char[] data = Files.readString(path).toCharArray();

            char[] sampleData = Files.readString(samplePath).toCharArray();

            LinkedHashMap<Character, BigDecimal> dataStatistics = countStatistics(data);

            LinkedHashMap<Character, BigDecimal> sampleStatistics = countStatistics(sampleData);

            HashMap<Character, Character> actualToMostProbable = makeDictionaryBasedOnStatistics(dataStatistics, sampleStatistics);

            for (int i = 0; i < data.length; i++) {
                char currentChar = data[i];

                if(actualToMostProbable.containsKey(currentChar)){
                    data[i] = actualToMostProbable.get(currentChar);
                }
            }

            Files.writeString(path, String.valueOf(data));

            System.out.println("Cracked with statistics file saved.");
        } catch (IOException e){
            System.out.printf("Error occurred during file decoding, message: %s, stacktrace: %s",
                    e.getMessage(), e);
        }
    }

    private HashMap<Character, Character> makeDictionaryBasedOnStatistics(LinkedHashMap<Character, BigDecimal> dataStatistics,
                                                                          LinkedHashMap<Character, BigDecimal> sampleStatistics) {
        HashMap<Character, Character> out = new HashMap<>();
        for (Map.Entry<Character, BigDecimal> entry : dataStatistics.entrySet()){
            char mostProbable = findClosest(sampleStatistics, entry.getValue());
            out.put(entry.getKey(), mostProbable);
        }

        return out;
    }


    private char findClosest(LinkedHashMap<Character, BigDecimal> sampleStatistics, BigDecimal value) {
        char out = sampleStatistics.entrySet().stream().findFirst().get().getKey();
        BigDecimal minDelta = sampleStatistics.entrySet().stream().findFirst().get().getValue()
                .subtract(value).abs();

        for(Map.Entry<Character, BigDecimal> entry : sampleStatistics.entrySet()){
            BigDecimal delta = entry.getValue().subtract(value).abs();
            if(delta.compareTo(minDelta) < 0){
                minDelta = delta;
                out = entry.getKey();
            }
        }

        return out;
    }

    private LinkedHashMap<Character, BigDecimal> countStatistics(char[] sampleData) {
        Map<Character, Long> charOccurrence = new HashMap<>();
        for (int i = 0; i < sampleData.length; i++) {
            char sampleChar = sampleData[i];

            if(charOccurrence.containsKey(sampleChar)){
                charOccurrence.put(sampleChar, charOccurrence.get(sampleChar) + 1L);
            } else {
                charOccurrence.put(sampleChar, 1L);
            }
        }

        LinkedHashMap<Character, BigDecimal> out = new LinkedHashMap<>();

        for (Map.Entry<Character, Long> entry : charOccurrence.entrySet()){
            out.put(entry.getKey(), new BigDecimal(entry.getValue()).setScale(1000, RoundingMode.HALF_EVEN)
                    .divide(new BigDecimal(sampleData.length), RoundingMode.HALF_EVEN));
        }

        out = out.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (entry1, entry2) -> entry1, LinkedHashMap::new));

        return out;
    }

    private void bruteforce(HashMap<String, String> params) {

        try {
            Path path = Path.of(params.get("path"));

            long fileSize = Files.size(path) / 1024 / 1024;

            if(fileSize > 10){
                System.out.println("File size exceeds 10 MB");
                return;
            }

            char[] data = Files.readString(path).toCharArray();

            HashMap<Integer, Long> offsetAlignments = new HashMap<>();

            Alphabet alphabet = new Alphabet();

            alphabet.initDecodeKeySet(0);

            for (int i = 0; i < alphabet.getMapping().size() - 1; i++) {
                alphabet.initDecodeKeySet(i);
                HashMap<Character, Character> mapping = alphabet.getMapping();

                getOffsetAlignment(offsetAlignments, data, mapping, i);
            }

            int bestOffset = 0;
            long maxAlignments = 0;

            for (int i = 0; i < offsetAlignments.size(); i++) {
                if(offsetAlignments.get(i) > maxAlignments){
                    maxAlignments = offsetAlignments.get(i);
                    bestOffset = i;
                }
            }

            if(bestOffset == 0){
                System.out.println("Can't bruteforce decode file provided. File kept intact.");
                return;
            }

            alphabet.initDecodeKeySet(bestOffset);
            HashMap<Character, Character> mapping = alphabet.getMapping();

            for (int i = 0; i < data.length; i++) {
                char currentChar = data[i];

                if(mapping.containsKey(currentChar)) {
                    data[i] = mapping.get(currentChar);
                }
            }

            Files.writeString(path, String.valueOf(data));

            System.out.println("Cracked with bruteforce file saved.");
        } catch (IOException e){
            System.out.printf("Error occurred during file decoding, message: %s, stacktrace: %s",
                    e.getMessage(), e);
        }
    }

    private void getOffsetAlignment(HashMap<Integer, Long> offsetAlignments, char[] data, HashMap<Character, Character> mapping, int offset) {
        long alignments = 0L;

        for (int i = 0; i < data.length; i++) {
            int index = i + offset < data.length ? i + offset : i + offset - data.length;

            char probableChar = data[index];

            if(!mapping.containsKey(probableChar)){
                continue;
            }

            char currentChar = mapping.get(probableChar);

            char nextChar;
            if(index + 1 < data.length){
                if(!mapping.containsKey(data[index + 1])){
                    continue;
                }
                nextChar = mapping.get(data[index + 1]);
            } else {
                nextChar = mapping.get(data[0]);
            }

            if(Alphabet.PUNCTUATION_MARKS.contains(currentChar) && " ".equals(String.valueOf(nextChar))){
                alignments++;
            }
        }

        offsetAlignments.put(offset, alignments);
    }


}
