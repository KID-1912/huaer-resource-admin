package com.huaer.resource.admin;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init(){
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS t_user ("
        + "id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY, "
        + "username VARCHAR(100) NOT NULL, "
        + "password VARCHAR(100) NOT NULL, "
        + "createdAt BIGINT NOT NULL, "
        + "UNIQUE(username)"
        + ");"
        );
    }
}
