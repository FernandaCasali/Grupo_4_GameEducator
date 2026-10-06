package org.example.grupo_4_gameeducator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Grupo4GameEducatorApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainDeveIniciarAplicacaoSemLevantarServidorWeb() {
        Grupo4GameEducatorApplication.main(new String[]{
                "--spring.main.web-application-type=none",
                "--spring.main.banner-mode=off",
                "--spring.jpa.hibernate.ddl-auto=create-drop",
                "--spring.datasource.url=jdbc:h2:mem:testdb-main",
                "--spring.datasource.driver-class-name=org.h2.Driver",
                "--spring.datasource.username=sa",
                "--spring.datasource.password="
        });
    }

}