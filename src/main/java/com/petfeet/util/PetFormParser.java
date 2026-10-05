package com.petfeet.util;

import com.petfeet.exception.ValidationException;
import com.petfeet.model.Pet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Reads and validates the shared "add / edit pet" form (including the optional image upload). */
public final class PetFormParser {
    private static final Set<String> IMAGE_TYPES = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "webp"));

    private PetFormParser() { }

    public static Pet parse(HttpServletRequest req, Pet target, String uploadDir) throws ValidationException, IOException, ServletException {
        target.setName(ValidationUtil.required(req.getParameter("name"), "Pet name", 80));
        target.setSpecies(ValidationUtil.required(req.getParameter("species"), "Species", 40));
        target.setBreed(ValidationUtil.required(req.getParameter("breed"), "Breed", 80));
        target.setAge(ValidationUtil.intInRange(req.getParameter("age"), "Age", 0, 30));
        target.setGender(ValidationUtil.oneOf(req.getParameter("gender"), "gender", "Male", "Female"));
        target.setLocation(ValidationUtil.required(req.getParameter("location"), "Location", 100));
        target.setDescription(ValidationUtil.optional(req.getParameter("description"), "Description", 2000));
        String status = ValidationUtil.clean(req.getParameter("status"));
        if (!status.isEmpty()) target.setStatus(status);

        String uploaded = saveUpload(req.getPart("imageFile"), uploadDir);
        String url = ValidationUtil.optional(req.getParameter("imageUrl"), "Image URL", 255);
        if (uploaded != null) {
            target.setImageUrl(uploaded);
        } else if (!url.isEmpty()) {
            if (!(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("images/"))) {
                throw new ValidationException("Image URL must start with http:// or https://");
            }
            target.setImageUrl(url);
        } else if (target.getImageUrl() == null || target.getImageUrl().isEmpty()) {
            target.setImageUrl(defaultImageFor(target.getSpecies()));
        }
        return target;
    }

    public static String defaultImageFor(String species) {
        String s = species == null ? "" : species.toLowerCase(Locale.ROOT);
        if (s.contains("cat")) return "images/pets/cat-indie.svg";
        if (s.contains("dog")) return "images/pets/dog-indie.svg";
        return "images/pets/rabbit.svg";
    }

    /**
     * Local MySQL runs keep uploads in webapp/uploads. Hosted Render runs store the image
     * as a data URL in PostgreSQL so it survives container restarts and redeploys.
     */
    private static String saveUpload(Part part, String uploadDir) throws ValidationException, IOException {
        if (part == null || part.getSize() == 0) return null;
        String original = part.getSubmittedFileName() == null ? "" : part.getSubmittedFileName();
        int dot = original.lastIndexOf('.');
        String ext = dot < 0 ? "" : original.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!IMAGE_TYPES.contains(ext)) throw new ValidationException("Please upload a JPG, PNG, GIF or WEBP image.");

        if (System.getenv("DATABASE_URL") != null && !System.getenv("DATABASE_URL").isBlank()) {
            if (part.getSize() > 2 * 1024 * 1024) {
                throw new ValidationException("For the online version, please keep pet images under 2 MB.");
            }
            byte[] bytes;
            try (InputStream in = part.getInputStream()) {
                bytes = in.readAllBytes();
            }
            String media = "jpg".equals(ext) ? "jpeg" : ext;
            return "data:image/" + media + ";base64," + Base64.getEncoder().encodeToString(bytes);
        }

        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) throw new ValidationException("The server could not store the image.");
        String fileName = UUID.randomUUID() + "." + ext;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, new File(dir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return "uploads/" + fileName;
    }
}
