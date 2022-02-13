package io.firestore4k.typed

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec

class CollectionsAnnotationProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // list of all classes with @IdOf annotation
        val idClassList = resolver
            // Getting all symbols that are annotated with @Entity.
            .getSymbolsWithAnnotation("io.firestore4k.typed.IdOf")
            // Making sure we take only class declarations.
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .map {
                val packageName = it.packageName.asString()
                val className = it.simpleName.asString()
                val collection = it.getAnnotationsByType(IdOf::class).single().collection
                collection to "$packageName.$className"
            }
            .toMap()
        // list of all classes with @Collection annotation
        val collectionClassList = resolver
            // Getting all symbols that are annotated with @Entity.
            .getSymbolsWithAnnotation("io.firestore4k.typed.Collection")
            // Making sure we take only class declarations.
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .map {
                val packageName = it.packageName.asString()
                val className = it.simpleName.asString()
                val collection = it.getAnnotationsByType(Collection::class).single()
                ClassInfo(
                    className = "$packageName.$className",
                    packageName = packageName,
                    collection = collection,
                    idClassName = idClassList[collection.name] ?: "kotlin.String",
                    childOf = it.getAnnotationsByType(ChildOf::class).singleOrNull(),
                )
            }
            .toList()
        generateCollectionPaths(collectionClassList)
        return emptyList()
    }

    private fun generateCollectionPaths(
        collectionClassList: List<ClassInfo>,
    ) {

        val collectionMap = collectionClassList.associateBy { it.collection.name }

        collectionClassList
            .groupBy { it.packageName }
            .forEach { (packageName: String, classInfoList: List<ClassInfo>) ->

                // file details
                val fileName = "CollectionPaths"
                val fileSpec = FileSpec.builder(packageName, fileName)

                // generic imports
                fileSpec.addImport(DSL_MODEL_PACKAGE_NAME, "rootCollection")

                val fileContent = buildString {

                    classInfoList.forEach { classInfo ->

                        val collectionName = classInfo.collection.name
                        if (classInfo.childOf == null) {
                            appendLine(
                                """
                                val $collectionName = rootCollection<${classInfo.className}, ${classInfo.idClassName}>("$collectionName")
    
                                """.trimIndent()
                            )
                        } else {
                            val parentCollection = classInfo.childOf.parent
                            val parentClass = collectionMap[parentCollection]?.className
                                ?: throw Exception("Collection $parentCollection not found.")
                            val parentCollectionName = classInfoList.find { it.className == parentClass }
                                ?: throw Exception("Class not found: $parentClass")
                            appendLine(
                                """
                                val $collectionName = ${parentCollectionName.collection.name}.subCollection<${classInfo.className}, ${classInfo.idClassName}>("$collectionName")
    
                                """.trimIndent()
                            )
                        }
                    }
                }

                // write to file
                val fileWriter = environment.codeGenerator.createNewFile(
                    dependencies = Dependencies.ALL_FILES,
                    packageName = packageName,
                    fileName = fileName
                ).writer()
                fileSpec.build().writeTo(fileWriter)
                fileWriter.append(fileContent)
                fileWriter.close()
            }
    }

    companion object {
        const val DSL_MODEL_PACKAGE_NAME = "io.firestore4k.typed"
    }
}

class CollectionsAnnotationProcessorProvider : SymbolProcessorProvider {

    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor = CollectionsAnnotationProcessor(
        environment
    )
}