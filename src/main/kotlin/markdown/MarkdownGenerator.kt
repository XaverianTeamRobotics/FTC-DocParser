package markdown

import decompilation.ButtonUsage

/**
 * Generates a page for the given title and buttons.
 * @param title The title of the page.
 * @param buttons The buttons to generate the page for.
 */
fun generatePage(title: String, buttons: List<ButtonUsage>): String {
    val builder = StringBuilder()
    val splitBySource = buttons.groupBy { it.obtainedFrom }
    builder.appendLine("# $title\n\n----------")
    for ((source, buttonUsages) in splitBySource) {
        builder.appendLine("\n## ${if (source == null) "Native" else "*Inherited from $source*"}")
        for (buttonUsage in buttonUsages) {
            builder.appendLine("\n### ${buttonUsage.description}")
            builder.appendLine("${buttonUsage.button} - ${buttonUsage.controller}")
        }
    }
    return builder.toString().trimEnd('\n')
}

/**
 * Generates multiple pages for the given pages.
 * @param pages The pages to generate. A map of title to buttons.
 */
fun generateMultiplePages(pages: Map<String, List<ButtonUsage>>): Map<String, String> {
    val toReturn = mutableMapOf<String, String>()
    for ((title, buttons) in pages) {
        toReturn[title] = generatePage(title, buttons)
    }
    return toReturn
}

/**
 * Generates an index page for the given pages.
 * @param pages The pages to generate an index for. A map of title to page.
 */
fun generateIndexPage(pages: Map<String, String>): String {
    val pageTitles = pages.keys.toList().sorted()
    val builder = StringBuilder()
    builder.appendLine("# Index\n")
    for (title in pageTitles) {
        builder.appendLine("- [$title](./$title.html)")
    }
    return builder.toString().trimEnd('\n')
}