package com.huantek.vein;

import com.huantek.vein.util.LostFrame;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.huantek.vein.Mapper")
@EnableTransactionManagement//开启事务管理
@EnableAsync
public class VeinApplication {

    public static void main(String[] args) throws IOException {
        //LostFrame.logOut();
        SpringApplication.run(VeinApplication.class, args);
    }

}
