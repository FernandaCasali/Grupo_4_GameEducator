package org.example.grupo_4_gameeducator.arquitetura;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.List;
import java.util.Map;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Evidencias do B1 (D1, D2, D4): as fronteiras da arquitetura sao verificadas por teste.
 * Se alguem violar uma regra, o build quebra.
 */
@AnalyzeClasses(packages = "org.example.grupo_4_gameeducator", importOptions = ImportOption.DoNotIncludeTests.class)
class FronteirasModulosTest {

    private static final String BASE = "org.example.grupo_4_gameeducator";

    private static final List<String> MODULOS =
            List.of("aluno", "cursos", "moedas", "ranking", "recomendacao", "notificacoes");

    /** Quem cada modulo PODE conhecer (alem de si mesmo). */
    private static final Map<String, List<String>> PERMITIDO = Map.of(
            "aluno", List.of(),
            "cursos", List.of("aluno"),
            "moedas", List.of("aluno"),
            "ranking", List.of("aluno"),
            "recomendacao", List.of("aluno"),
            "notificacoes", List.of("cursos"));

    // ---- D1: modulos
    @ArchTest
    static void internalDeCadaModuloEhPrivado(JavaClasses classes) {
        for (String m : MODULOS) {
            noClasses()
                    .that().resideOutsideOfPackage(BASE + "." + m + "..")
                    .should().dependOnClassesThat().resideInAPackage(BASE + "." + m + ".internal..")
                    .because("o pacote internal do modulo " + m + " e privado")
                    .check(classes);
        }
    }

    @ArchTest
    static final ArchRule modulosSemCiclos = slices()
            .matching(BASE + ".(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static void cadaModuloSoConheceOsModulosPermitidos(JavaClasses classes) {
        for (String m : MODULOS) {
            String[] proibidos = MODULOS.stream()
                    .filter(o -> !o.equals(m) && !PERMITIDO.get(m).contains(o))
                    .map(o -> BASE + "." + o + "..")
                    .toArray(String[]::new);
            noClasses()
                    .that().resideInAPackage(BASE + "." + m + "..")
                    .should().dependOnClassesThat().resideInAnyPackage(proibidos)
                    .because("o modulo " + m + " so pode depender de " + PERMITIDO.get(m))
                    .check(classes);
        }
    }

    @ArchTest
    static final ArchRule notificacoesSoUsaOContratoPublicoDeCursos = noClasses()
            .that().resideInAPackage(BASE + ".notificacoes..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    BASE + ".cursos.application..", BASE + ".cursos.domain..");

    // ---- D2: Ports & Adapters
    @ArchTest
    static final ArchRule dominioNaoDependeDeFrameworkNemDeCamadasExternas = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "..application..", "..api..", "..internal..");

    @ArchTest
    static void aplicacaoNaoConheceAdapters(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..internal..")
                .because("a aplicacao depende de PORTAS, nunca dos adapters")
                .check(classes);
        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "org.springframework.data..")
                .because("a aplicacao nao pode depender de JPA/Spring Data")
                .check(classes);
    }
}
