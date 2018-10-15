/**
 * @author rmalhotra
 * @created 02/09/2018
 */
package com.zomasystems.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@ConfigurationProperties
@EnableConfigurationProperties({GmailServerProperties.class,AwsClientProperties.class, AwsS3Properties.class, AppProperties.class, GoogleAutoMLProperties.class})
public class ApplicationConfiguration {

    @Autowired
    private GmailServerProperties gmailServerProperties;

    @Autowired
    private AwsClientProperties awsClientConfig;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private GoogleAutoMLProperties googleAutoMLProperties;

}
