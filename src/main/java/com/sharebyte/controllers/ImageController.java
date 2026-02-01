package com.sharebyte.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController  
@RequestMapping("/images")
public class ImageController {
	@GetMapping("/profile/{filename}")
	public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) throws IOException {
//			System.out.println(filename);
		    Path path = Paths.get("uploads/profile/" + filename);
		    Resource resource = new UrlResource(path.toUri());

		    if (!resource.exists()) {
		        return ResponseEntity.notFound().build();
		    } 

		    return ResponseEntity.ok()
		            .contentType(MediaType.IMAGE_JPEG)
		            .body(resource);
	}
	
}
