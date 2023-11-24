package ua.kpi.realitu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestRealituApplication {

	public static void main(String[] args) {
		SpringApplication.from(RealituApplication::main).with(TestRealituApplication.class).run(args);
	}

}
