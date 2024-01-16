import decompilation.ButtonUsageParser
import markdown.generateIndexPage
import markdown.generateMultiplePages
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.io.File

fun main() {
    if (!File("APK-extracted").exists()) {
        throw IllegalStateException("There is no APK-extracted folder. Please run the unzipAPK.sh file first.")
    }
    val parser = ButtonUsageParser("APK-extracted")
    val allButtonUsages = parser.getAllButtonUsages()
    val pages = generateMultiplePages(allButtonUsages).toMutableMap()
    val indexPage = generateIndexPage(pages)
    pages["index"] = indexPage

    if (!File("doc-out").exists()) {
        File("doc-out").mkdir()
    } else {
        File("doc-out").deleteRecursively()
        File("doc-out").mkdir()
    }

    for ((title, page) in pages) {
        val html = HtmlGenerator(
            page, MarkdownParser(CommonMarkFlavourDescriptor()).buildMarkdownTreeFromString(page),
            CommonMarkFlavourDescriptor()).generateHtml()
        File("doc-out/$title.html").writeText(html)
    }

    // In every page, add a title and a link to the index page
    for (file in File("doc-out").listFiles()!!) {
        if (file.name.endsWith(".html") && file.name != "index.html") {
            val html = file.readText()
            file.writeText("<head><title>${file.nameWithoutExtension}</title></head>"
                    + html.replace("</body>", "<a href=\"./index.html\">Back to index</a></body>"))
        } else if (file.name == "index.html") {
            file.writeText("<head><title>Index</title></head>" + file.readText())
        }
    }

    // If the doc-style.css file exists, copy it to the output folder and add a link to it in each page
    if (File("doc-style.css").exists()) {
        File("doc-out/doc-style.css").writeText(File("doc-style.css").readText())
        for (file in File("doc-out").listFiles()!!) {
            if (file.name.endsWith(".html")) {
                val html = file.readText()
                file.writeText(html.replace("</head>", "<link rel=\"stylesheet\" href=\"./doc-style.css\"></head>"))
            }
        }
    }
}