package com.zomasystems;

import com.zomasystems.processors.EmailProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@ComponentScan({"com.zomasystems.config", "com.zomasystems"})
@PropertySource("classpath:application.yml")
@Profile("!test")
@EnableScheduling
@EnableAutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication
public class Application {

    @Autowired
    private EmailProcessor emailChecker;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
        };
    }
    @Scheduled(fixedRate = 1200)
    public void checkEmail(){
        try {
            emailChecker.processEmail();
        }catch(Exception err){
            err.printStackTrace();
        }
    }

}
