package com.book.ensureu.api;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class StaticContentApi {

	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${spring.static.image}")
	private String staticImageFolder;

	@RequestMapping(value = "/file/{imageName}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<InputStreamResource> getImage(@PathVariable(value = "imageName") String imageName)
			throws IOException {

		String path = staticImageFolder + imageName;
		System.out.println("path " + path);
		Resource resource = resourceLoader.getResource("file:" + path + ".png");
		InputStream inputStream = resource.getInputStream();
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(inputStream));
	}
}
