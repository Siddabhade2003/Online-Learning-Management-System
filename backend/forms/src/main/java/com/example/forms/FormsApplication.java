package com.example.forms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FormsApplication {

	public static void main(String[] args) {
		// Set TLS protocol and cipher suites
		System.setProperty("https.protocols", "TLSv1.2");
		System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
		System.setProperty("https.cipherSuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");

		// Start Spring Boot application
		SpringApplication.run(FormsApplication.class, args);
	}
}
