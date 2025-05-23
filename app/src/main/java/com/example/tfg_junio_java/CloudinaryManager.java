package com.example.tfg_junio_java;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {
    public static void init(Context context) {
        try {
            MediaManager.get();
        } catch (IllegalStateException e) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "da8f7v46t");
            config.put("api_key", "767354815669725");
            config.put("api_secret", "Rn90y4t5CRo0vH1BfWxC7kM3GQs");
            MediaManager.init(context, config);
        }
    }
}
