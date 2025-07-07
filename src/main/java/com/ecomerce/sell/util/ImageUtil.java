package com.ecomerce.sell.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public class ImageUtil {

    public static String saveBase64Image(String base64Str) {
        try {
            if (base64Str.contains(",")) {
                base64Str = base64Str.split(",")[1];
            }
            byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
            String fileName = UUID.randomUUID() + ".jpg";
            String folderPath = "D:/My Data/MyProjects/ecommerce-images/";

            File directory = new File(folderPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = folderPath + fileName;
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedBytes);
            }

            return "http://localhost:8080/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
}

