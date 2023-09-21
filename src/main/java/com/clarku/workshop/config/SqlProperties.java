package com.clarku.workshop.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:sql.properties")
@ConfigurationProperties(prefix="sql")
public class SqlProperties {

	public static Map<String, String> user;
	public static Map<String, String> login;

	public void setUser(Map<String, String> user) {
		SqlProperties.user = user;
	}

	public void setLogin(Map<String, String> login) {
		SqlProperties.login = login;
	}
	
}