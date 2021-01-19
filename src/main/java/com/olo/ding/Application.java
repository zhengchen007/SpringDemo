package com.olo.ding;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.olo.ding.utils.SpringContextHolder;

/**
 * 
 * @ClassName Application
 * @author Zhang.Tao
 * @Date 2017年4月27日 下午5:30:3
 * @version V2.0.0
 */

@SpringBootApplication(exclude = MybatisAutoConfiguration.class)
@ServletComponentScan
@MapperScan("com.olo.ding.mapper")
public class Application  extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {

	@Value("${server.port}")   
	private int port;//应用的端口
	/**
	 * 启动入口
	 * @param args  
	 */
    public static void main(String ... args){
    	ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    	new SpringContextHolder().setApplicationContext(context);
    }

    /**
     * 自定义端口
     */
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		container.setPort(port);
	}

	
} 
