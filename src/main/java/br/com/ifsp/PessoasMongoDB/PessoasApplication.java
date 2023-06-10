package br.com.ifsp.PessoasMongoDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class PessoasApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PessoasApplication.class, args);
	}

}
