package com.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTest {
    ApplicationModules modules = ApplicationModules.of(ECommerceApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        //Here you can check draw UML diagram http://www.plantuml.com/plantuml/uml/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000
        //You can find files in the /target/spring-modulith-docs/*.puml
        new Documenter(modules).writeDocumentation().writeIndividualModulesAsPlantUml();
    }
}
