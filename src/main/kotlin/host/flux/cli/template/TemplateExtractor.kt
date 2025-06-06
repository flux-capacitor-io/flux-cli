package host.flux.cli.template

import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

class TemplateExtractor(private val resourceRoot: String = "/templates") {

    fun extract(templateName: String, targetDir: Path) {
        val zipStream: InputStream = openZipStream(templateName, targetDir)
        extractZip(zipStream, targetDir)
    }

    private fun extractZip(zipStream: InputStream, targetDir: Path) {
        ZipInputStream(zipStream).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val outPath = targetDir.resolve(entry.name).normalize()

                if (entry.isDirectory) {
                    Files.createDirectories(outPath)
                } else {
                    Files.createDirectories(outPath.parent)
                    Files.newOutputStream(outPath).use { out -> zip.copyTo(out) }
                }

                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }

    private fun openZipStream(templateName: String, targetDir: Path): InputStream {
        val resourcePath = "$resourceRoot/$templateName.zip"
        val zipStream: InputStream = javaClass.getResourceAsStream(resourcePath)
            ?: throw FileNotFoundException("Template '$templateName' not found in classpath at $resourcePath")

        Files.createDirectories(targetDir)
        return zipStream
    }

    fun listTemplates(): List<String> {
        val indexPath = "$resourceRoot/templates.csv"
        val indexStream = javaClass.getResourceAsStream(indexPath)
            ?: throw FileNotFoundException("Could not find template index at $indexPath")

        return indexStream.bufferedReader().readLines()
            .filter { it.isNotBlank() }
            .map { it.trim() }
    }
}
