package com.awbp;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.awbp", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainIsFrameworkFree = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "lombok..");

    @ArchTest
    static final ArchRule noAccessToOtherAreasInternals = slices()
            .matching("com.awbp.(*).[application|domain]..")
            .should().notDependOnEachOther()
            .ignoreDependency(alwaysTrue(), resideInAPackage("com.awbp.shared.."));

    @ArchTest
    static final ArchRule sharedDependsOnNoArea = noClasses()
            .that().resideInAPackage("com.awbp.shared..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.awbp.booking..", "com.awbp.scheduling..", "com.awbp.identity..");
}
