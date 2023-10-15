package com.clarku.workshop.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@PropertySource("classpath:sql.properties")
@ConfigurationProperties(prefix = "sql")
@Data
public class SqlProperties {

	public static Map<String, String> user;
	public static Map<String, String> login;
	public static Map<String, String> session;
	public static Map<String, String> skills;
	public static Map<String, String> workshop;
	public static Map<String, String> admin;

	public void setUser(Map<String, String> user) {
		SqlProperties.user = user;
	}

	public void setLogin(Map<String, String> login) {
		SqlProperties.login = login;
	}
	
	public void setSession(Map<String, String> session) {
		SqlProperties.session = session;
	}
	
	public void setSkills(Map<String, String> skills) {
		SqlProperties.skills = skills;
	}
	
	public void setWorkshop(Map<String, String> workshop) {
		SqlProperties.workshop = workshop;
	}

	public void setAdmin(Map<String, String> admin) {
		SqlProperties.admin = admin;
	}

}