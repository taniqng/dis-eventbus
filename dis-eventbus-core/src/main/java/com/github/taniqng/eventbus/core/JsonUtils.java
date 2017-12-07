package com.github.taniqng.eventbus.core;

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

/**
 * 
 */
public final class JsonUtils {

    public static <T> T readObject(String json, TypeReference<T> type) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T readObject(String json, Class<T> type) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);

        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Serialize
     * @param obj obj 
     * @return String
     * @throws RuntimeException on error
     */
    public static String writeObject(Object obj) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Object readObject(String json){
    	 ObjectMapper mapper = new ObjectMapper();
         mapper.setSerializationInclusion(Include.NON_NULL);
         mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
         mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);

         try {
             return mapper.readValue(json, Object.class);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }
}
