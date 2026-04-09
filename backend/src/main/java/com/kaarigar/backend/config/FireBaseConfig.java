package com.kaarigar.backend.config;

import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseApp;
import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class FireBaseConfig{

    @PostConstruct
    public void init(){
        try{
            FileInputStream serviceAccount=new FileInputStream("src/main/resources/serviceAccountKey.json");

            FirebaseOptions options= FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully for Kaarigar Gatekeeper");
            }
        } catch (IOException e) {
            System.err.println("❌ ERROR: Could not find serviceAccountKey.json. Make sure it's in src/main/resources/");
            e.printStackTrace();
        }
    }
}