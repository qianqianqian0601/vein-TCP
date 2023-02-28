package com.huantek.vein.Config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer ScannerMapperConfigure(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");

        mapperScannerConfigurer.setBasePackage("com.huantek.vein.Mapper");

        return mapperScannerConfigurer;
    }
}
