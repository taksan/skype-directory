package org.taksan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String render(Object model) {
        if (model == null) return "{}";
        return gson.toJson(model);
    }
}