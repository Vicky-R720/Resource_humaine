package com.itu.gest_emp.modules.personnel.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadHelper {

    public static String save(String UPLOAD_DIR, MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty())
            return null;

        // Créer le dossier si nécessaire
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists())
            dir.mkdirs();

        // Générer un nom unique
        String filename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        Path filepath = Paths.get(UPLOAD_DIR + filename);

        // Écrire le fichier
        Files.write(filepath, photo.getBytes());

        return UPLOAD_DIR + filename;
    }
}
