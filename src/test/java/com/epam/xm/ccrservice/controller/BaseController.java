package com.epam.xm.ccrservice.controller;


import com.epam.xm.ccrservice.exception.JsonFileReadingException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseController {

    private Map<String, String> jsons;

    /**
     * The method is for processing source folder, i.e. getting
     * all "*.json" files and making a map Map<fileName,jsonString>
     *<br/> <b>!Note:</b> It doesn't make any deserialization whatsoever,
     * just linearize json string, removing all blanks (trick with ObjectMapper), so the
     * raw string can be used as expected result in proper test
     * @param jsonFolder source folder to parse json files
     * @param objectMapper Spring configured ObjectMapper like that used in App
     */
    protected void init(String jsonFolder, ObjectMapper objectMapper) {

        try {

            jsons = Files.walk(Paths.get("src/test/resources" +
                            System.getProperty("file.separator") + jsonFolder))
                    .map(p -> getJson(p, objectMapper))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        } catch (IOException e) {
            throw new JsonFileReadingException("Failed to json files", e); //todo more clear text
        }
    }

    private Optional<Pair<String, String>> getJson(Path p, ObjectMapper objectMapper) {

        try {

            String path = p.toString();

            if(Files.isRegularFile(new File(path).toPath()) && path.endsWith(".json")) {

                String raw = Strings.trimToNull(IOUtils.toString(p.toUri(), Charset.defaultCharset()));

                if(raw != null) {

                    JsonParser parser = objectMapper.createParser(raw);
                    TreeNode treeNode = objectMapper.readTree(parser);

                    return Optional.of(Pair.of(p.getFileName().toString(), treeNode.toString()));
                }

                return Optional.empty();
            }
        } catch (Exception e) {
            throw new JsonFileReadingException("Can not read json", e);
        }

        return Optional.empty();
    }

    /**
     * Method is for getting json by filename.json
     * @param fileName name of the file located in target folder
     *                 supplied in method - {@link BaseController#init(String fileName, ObjectMapper om)}
     * @return jsonString - expected result json string
     */
    protected String getJson(String fileName) {
        return jsons.get(fileName);
    }

}
