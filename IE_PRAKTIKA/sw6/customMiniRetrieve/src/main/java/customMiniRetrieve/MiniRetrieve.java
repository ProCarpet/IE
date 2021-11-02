/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package customMiniRetrieve;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiniRetrieve {
    private static final String DOCUMENT_PATH = "documents/";
    private static final String QUERRY_PATH = "queries/";

    private static Map<Integer, String> querries;
    private static Map<Integer, String> documents;

    private static Map<String, Map<String, Integer>> invIndex;
    private static Map<File, String> nonInvIndex;


    public static void main(String[] args) {
        querries = readFiles(readDirectory(QUERRY_PATH));
        documents = readFiles(readDirectory(DOCUMENT_PATH));
        invIndex = new HashMap<>();
        System.out.println(tokenizeString(querries.get(1)));
        indexing(querries,documents);

    }
    //todo dont save documents as a string in hashmap
    private static void indexing(Map<Integer, String> querries, Map<Integer, String> documents) {
        for (String doc : documents.values()) {
            List<String> terms = tokenizeString(doc);
            for (String term : terms) {
                //if invIndex already contains word and a doc already containing that term once increment
                if (invIndex.containsKey(term) && invIndex.get(term).containsKey(doc)) {
                    Map<String, Integer> mapOfTerm = invIndex.get(term);
                    mapOfTerm.put(doc, (mapOfTerm.get(doc) + 1));
                // if invIndex contains word already but not with this doc put doc with count 1
                } else if (invIndex.containsKey(term)) {
                    invIndex.get(term).put(doc, 1);
                // if term is new in invIndex put it there with the doc it was found
                } else {
                    Map<String, Integer> newMap = new HashMap<>();
                    newMap.put(doc, 1);
                    invIndex.put(term, newMap);
                }
            }
        }
        System.out.println(invIndex);
    }

    private static List<String> tokenizeString(String fullString) {
        List<String> rawTokens = Arrays.asList(fullString.split(" "));
        List<String> trimmedTokens = rawTokens.stream().map(String::trim).collect(Collectors.toList());
        List<String> cleanedTokens = trimmedTokens.stream().map(s -> s.replace("\\.\\,\\;\\<\\>\\/\\?\\!\\(\\)\\[\\]\\{\\}", "")).collect(Collectors.toList());
        List<String> lowercasedToken = cleanedTokens.stream().map(String::toLowerCase).collect(Collectors.toList());
        return lowercasedToken;
    }

    private static Map<Integer, String> readFiles(Map<Integer, File> files) {
        Map<Integer, String> output = new HashMap<>();
        for (Map.Entry<Integer, File> integerFileEntry : files.entrySet()) {
            try (BufferedReader bf = new BufferedReader(new FileReader(integerFileEntry.getValue()))) {
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                output.put(integerFileEntry.getKey(), sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return output;
    }

    private static Map<Integer, File> readDirectory(String path) {
        HashMap<Integer, File> map = new HashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(e -> map.put(Integer.valueOf(e.toFile().getName()), e.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}