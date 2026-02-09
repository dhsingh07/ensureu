package com.ensureu.commons.conf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

public class PropertiesFileConfig {
	static Properties configProperties=null;
	
	public enum PropertiesFile {
		Application_Properties("application.properties");
		private String propetiesFileName;

		PropertiesFile(String propetiesFileName) {
			this.propetiesFileName = propetiesFileName;
		}
	}
	
	static {
		loadPropertiesFile(PropertiesFile.Application_Properties);
	}
	public PropertiesFileConfig() {
		
	}
	
	public static void loadPropertiesFile(PropertiesFile propertiesFile) {
		ClassPathResource classPathResource=new ClassPathResource(propertiesFile.propetiesFileName);
		try(InputStream inputStream=classPathResource.getInputStream()){
		    configProperties = new Properties();
		    configProperties.load(inputStream);
		}catch (FileNotFoundException e) {
            throw new IllegalStateException("properties file does not exist: " + propertiesFile.propetiesFileName, e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load properties file. ", e);
        }
	}
	
	
	public static String getPropertyValue(String key) {
		return configProperties.getProperty(key);
	}
	
}
