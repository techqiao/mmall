package com.mmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(value = "com.mmall.dao")
@EnableAspectJAutoProxy
@ServletComponentScan
public class MmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(MmallApplication.class, args);
	}
}
