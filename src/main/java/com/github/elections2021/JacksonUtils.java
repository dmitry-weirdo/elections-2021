package com.github.elections2021;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class JacksonUtils {
    public static <T> T parse(File file, Class<T> clazz) {
        try {
            ObjectMapper mapper = createObjectMapper();
            return mapper.readValue(file, clazz);
        }
        catch (IOException e) {
            String errorMessage = String.format("Error on parsing file %s to class %s", file.getPath(), clazz.getName());
            throw handleError(e, errorMessage);
        }
    }

    public static <T> T parse(String string, Class<T> clazz) {
        try {
            ObjectMapper mapper = createObjectMapper();
            return mapper.readValue(string, clazz);
        }
        catch (IOException e) {
            String errorMessage = String.format("Error on parsing string %s to class %s", string, clazz.getName());
            throw handleError(e, errorMessage);
        }
    }

    public static <T> List<T> parseList(String string, Class<T> clazz) {
        try {
            Class<?> arrayClass = Class.forName(
                "[L" + clazz.getCanonicalName() + ";"
            );

            ObjectMapper mapper = createObjectMapper();
            final T[] array = (T[]) mapper.readValue(string, arrayClass);
            return Arrays.asList(array);
        }
        catch (Exception e) {
            String errorMessage = String.format("Error on parsing string %s to class %s", string, clazz.getName());
            throw handleError(e, errorMessage);
        }
    }

    public static void serialize(File file, Object object) {
        try {
            ObjectMapper mapper = createObjectMapper();
            mapper.writeValue(file, object);
        }
        catch (IOException e) {
            String errorMessage = String.format("Error on writing object of class %s to file %s", object.getClass().getName(), file.getPath());
            throw handleError(e, errorMessage);
        }
    }

    public static String serializeToString(Object object) {
        try {
            ObjectMapper mapper = createObjectMapper();
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            String errorMessage = String.format("Error on writing object of class %s to String", object.getClass().getName());
            throw handleError(e, errorMessage);
        }
    }

    private static RuntimeException handleError(final Exception e, final String errorMessage) {
        log.error(errorMessage, e);
        return new RuntimeException(errorMessage, e);
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);

        // prevent failing on Optional de-serialization, see https://stackoverflow.com/a/40408273/8534088
        // prevent failing on ZonedDateTime deserialization
        mapper
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false); // do not change ZonedDateTime timezone, see https://stackoverflow.com/a/59098543/8534088

        return mapper;
    }
}
