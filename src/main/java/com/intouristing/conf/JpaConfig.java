package com.intouristing.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.intouristing")
public class JpaConfig {
}
