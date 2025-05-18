package com.example.final_project.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final String upload_preset;


    public CloudinaryService(@Value("${cloudinary.cloud_name}") String cloud_name,
                             @Value("${cloudinary.api_key}") String api_key,
                             @Value("${cloudinary.api_secret}") String api_secret,
                             @Value("${cloudinary.upload_preset}") String upload_preset
    )
    {
        this.upload_preset = upload_preset;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloud_name,
                "api_key", api_key,
                "api_secret", api_secret
        )
        );
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "upload_preset", upload_preset
        ));

        return uploadResult.get("secure_url").toString();
    }
}

