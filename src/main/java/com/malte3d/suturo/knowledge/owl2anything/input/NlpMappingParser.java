package com.malte3d.suturo.knowledge.owl2anything.input;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NlpMappingParser {

    private static final Gson GSON = new Gson().newBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

	/**
	 * Parse the nlp mapping file and return a mapping from IRI suffix to nlp names
	 */
	public static Map<String, List<String>> parseNlpMappings(File nlpMappingFile) {
		try(JsonReader reader = new JsonReader(new FileReader(nlpMappingFile))) {

			Map<String,List<String>> mapping = new HashMap<>();

			TypeToken<List<String>> listType = new TypeToken<List<String>>(){};

			reader.beginObject();
			while(reader.hasNext()) {

				String name = reader.nextName();
				if (name.startsWith("_")) {
                    reader.skipValue();
                    continue;
                }

				List<String> nlpNames = GSON.fromJson(reader, listType);

				mapping.put(name, nlpNames);
			}
			reader.endObject();
			return mapping;
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
