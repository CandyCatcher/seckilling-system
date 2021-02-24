package top.candyboy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
//@MapperScan(basePackages = "top.candysky.mapper")
// 默认扫描top.candysky下的包
// 现在加上其他组件包
//@ComponentScan(basePackages = {"top.candyboy"})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
