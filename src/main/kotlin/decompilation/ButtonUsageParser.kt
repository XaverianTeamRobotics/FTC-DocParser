package decompilation

class ButtonUsageParser(rootPath: String) {
    private val classLookup = ClassLookup(rootPath)

    private fun getDeclaredButtonUsages(): HashMap<String, List<ButtonUsage>> {
        val toReturn = HashMap<String, List<ButtonUsage>>()
        val annotation = classLookup.loadClass("org.firstinspires.ftc.teamcode.internals.documentation.ButtonUsage")
        val annotationClass: Class<out Annotation>
        if (!Annotation::class.java.isAssignableFrom(annotation)) {
            throw IllegalArgumentException("Class is not an annotation")
        } else {
            annotationClass = annotation as Class<out Annotation>
        }
        val buttonUsageClasses = classLookup.findClassesWithAnnotation(annotationClass, "org.firstinspires.ftc.teamcode")
        buttonUsageClasses.forEach { clazz ->
            val buttonUsages = mutableListOf<ButtonUsage>()
            classLookup.getInstancesOfAnnotationsOnClass(clazz, annotationClass).forEach { instance ->
                val description = instance.javaClass.getMethod("description").invoke(instance) as String
                val button = instance.javaClass.getMethod("button").invoke(instance).toString()
                val controller = instance.javaClass.getMethod("controller").invoke(instance).toString()
                buttonUsages.add(ButtonUsage(description, button, controller))
            }
            toReturn[clazz.simpleName] = buttonUsages
        }
        return toReturn
    }

    private fun getReferableButtonUsages(): HashMap<String, List<ButtonUsage>> {
        val toReturn = HashMap<String, List<ButtonUsage>>()
        val annotation = classLookup.loadClass("org.firstinspires.ftc.teamcode.internals.documentation.ReferableButtonUsage")
        val annotationClass: Class<out Annotation>
        if (!Annotation::class.java.isAssignableFrom(annotation)) {
            throw IllegalArgumentException("Class is not an annotation")
        } else {
            annotationClass = annotation as Class<out Annotation>
        }
        val buttonUsages = getDeclaredButtonUsages()
        val referableButtonUsageClasses = classLookup.findClassesWithAnnotation(annotationClass, "org.firstinspires.ftc.teamcode")
        referableButtonUsageClasses.forEach { clazz ->
            var referableAs: String? = null
            classLookup.getInstancesOfAnnotationsOnClass(clazz, annotationClass).forEach { instance ->
                referableAs = instance.javaClass.getMethod("referableAs").invoke(instance).toString()
                // the annotation is not repeatable, so there is only one instance
            }
            if (referableAs != null && buttonUsages.containsKey(clazz.simpleName)) {
                toReturn[referableAs!!] = buttonUsages[clazz.simpleName]!!
            }
        }

        return toReturn
    }

    fun getAllButtonUsages(): HashMap<String, List<ButtonUsage>> {
        val toReturn = getDeclaredButtonUsages()
        val annotation = classLookup.loadClass("org.firstinspires.ftc.teamcode.internals.documentation.ReferToButtonUsage")
        val annotationClass: Class<out Annotation>
        if (!Annotation::class.java.isAssignableFrom(annotation)) {
            throw IllegalArgumentException("Class is not an annotation")
        } else {
            annotationClass = annotation as Class<out Annotation>
        }
        val referableButtonUsages = getReferableButtonUsages()
        val referToUsageClasses = classLookup.findClassesWithAnnotation(annotationClass, "org.firstinspires.ftc.teamcode")
        referToUsageClasses.forEach { clazz ->
            classLookup.getInstancesOfAnnotationsOnClass(clazz, annotationClass).forEach { instance ->
                // Get the referTo value
                val referTo = instance.javaClass.getMethod("referTo").invoke(instance).toString()
                // Check if the referTo value is a key in the referableButtonUsages map
                if (referableButtonUsages.containsKey(referTo)) {
                    // Add the buttonUsages to the toReturn map
                    referableButtonUsages[referTo]!!.forEach {
                        // Inject the obtainedFrom value
                        val newButtonUsage = ButtonUsage(it.description, it.button, it.controller, referTo)
                        if (toReturn.containsKey(clazz.simpleName)) {
                            toReturn[clazz.simpleName] = toReturn[clazz.simpleName]!!.toMutableList().apply { add(newButtonUsage) }
                        } else {
                            toReturn[clazz.simpleName] = listOf(newButtonUsage)
                        }
                    }
                }
            }
        }
        return toReturn
    }
}

data class ButtonUsage(val description: String, val button: String, val controller: String, val obtainedFrom: String? = null)