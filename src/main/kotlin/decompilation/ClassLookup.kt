package decompilation

import java.io.File
import java.net.URLClassLoader

/**
 * Represents a folder which contain class files.
 */
class ClassLookup(val rootFolder: String) {
    /**
     * Returns all classes in a package.
     */
    fun getClassesInPackage(packageName: String): List<String> {
        if (packageName.isEmpty()) {
            throw IllegalArgumentException("Package name cannot be empty")
        }
        // Check if the package is a valid package name
        if (!packageName.matches(Regex("^[a-zA-Z0-9.]+$"))) {
            throw IllegalArgumentException("Package name is not valid")
        }
        val folder = File(rootFolder + "/" + packageName.replace(".", "/"))
        if (!folder.exists()) {
            throw IllegalArgumentException("Package does not exist")
        }
        return folder.listFiles()?.map { it.name } ?: emptyList()
    }

    /**
     * Returns the class file for a given class name with the full package name.
     */
    fun getClassFile(className: String): File {
        if (className.isEmpty()) {
            throw IllegalArgumentException("Class name cannot be empty")
        }
        // Check if the class name is a valid class name
        if (!className.matches(Regex("^[a-zA-Z0-9.]+$"))) {
            throw IllegalArgumentException("Class name is not valid")
        }
        val classFile = File(rootFolder + "/" + className.replace(".", "/") + ".class")
        if (!classFile.exists()) {
            throw IllegalArgumentException("Class file does not exist")
        }
        return classFile
    }

    /**
     * Load the class from the .class file, looking in the root folder.
     * @param className The full package name of the class.
     * @return The class.
     */
    fun loadClass(className: String): Class<*> {
        val classLoader = URLClassLoader(arrayOf(File(rootFolder).toURI().toURL()))
        return classLoader.loadClass(className)
    }

    /**
     * Find all the classes in the root folder which have the given annotation.
     */
    fun findClassesWithAnnotation(annotation: Class<out Annotation>, searchPackage: String): List<Class<*>> {
        // Replace the . with / to get the folder path
        val searchFolder = rootFolder + "/" + searchPackage.replace(".", "/")
        val folder = File(searchFolder)
        if (!folder.exists()) {
            throw IllegalArgumentException("Package does not exist")
        }
        // Recursively search the folder for files which end with .class
        val classes = folder.walkTopDown().filter { it.isFile && it.name.endsWith(".class") }.map {
            // Remove the root folder from the path
            val path = it.path.replace(rootFolder, "")
            // Remove the .class from the end
            val className = path.substring(1, path.length - 6)
            // Replace the / with . to get the full package name
            loadClass(className.replace("/", "."))
        }.toList()

        // Filter the classes to only include the ones with the annotation
        val classesWithAnnotation = classes.filter { clazz ->
            val annotationContainerName = annotation.name + ".Container"
            val classAnnotationNames = clazz.annotations.map { it.annotationClass.qualifiedName }
            classAnnotationNames.contains(annotation.name) || classAnnotationNames.contains(annotationContainerName)
        }
        return classesWithAnnotation
    }

    /**
     * Returns the instances of the given annotation on the given class.
     */
    fun getInstancesOfAnnotationsOnClass(clazz: Class<*>, annotation: Class<out Annotation>): List<Any> {
        val annotations = clazz.annotations
        val annotationInstances = mutableListOf<Any>()
        annotations.forEach { annotationInstance ->
            if (annotation.name == annotationInstance.annotationClass.qualifiedName) {
                annotationInstances.add(annotationInstance)
            }
        }
        val annotationAsContainer = annotation.name + ".Container"
        annotations.forEach { annotationInstance ->
            if (annotationAsContainer == annotationInstance.annotationClass.qualifiedName) {
                val valueMethod = annotationInstance.javaClass.getMethod("value")
                val value = valueMethod.invoke(annotationInstance)
                // Cast the value to an array of Any
                val annotationInstancesInContainer = value as Array<*>
                annotationInstancesInContainer.forEach {
                    if (it != null) {
                        annotationInstances.add(it)
                    }
                }
            }
        }
        return annotationInstances
    }
}