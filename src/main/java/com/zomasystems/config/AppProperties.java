/**
 * @author rmalhotra
 * @created 02/09/2018
 */
package com.zomasystems.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application")
public class AppProperties {
}
