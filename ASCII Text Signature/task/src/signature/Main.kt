package signature

import java.io.File
import java.util.Scanner

const val BORDER_SYMBOL = "8"
const val BORDER_WIDTH = 2
const val BORDER_PADDING = 2

val NAME_FONT = Font.ROMAN
val STATUS_FONT = Font.MEDIUM

const val FONT_PATH = "C:/Users/david/IdeaProjects/ASCII Text Signature/ASCII Text Signature/ASCII New Roman/"

fun main() {
    val scanner = Scanner(System.`in`)
    print("Enter name and surname: ")
    val firstName = scanner.next()
    val lastName = scanner.next()
    print("Enter person's status: ")
    val status = readLine()!!
    val nameTag = NameTag(firstName, lastName, status)
    for (line in nameTag.lines) println(line)
}

/**
 * The name tag constructed from the firstName, lastName and status string values. Provides the [lines] property,
 * each member of which is the string representation of the corresponding text line, ready for printing.
 */
class NameTag(firstName: String, lastName: String, status: String) {

    private val nameText = StyledText("$firstName $lastName", NAME_FONT)
    private val statusText = StyledText(status, STATUS_FONT)

    private val tagLength = maxOf(nameText.length, statusText.length) + 2 * (BORDER_WIDTH + BORDER_PADDING)

    val lines: Array<String> = Array(nameText.font.height + statusText.font.height + 2) { buildLine(it) }

    private fun buildLine(tagLineNum: Int): String {
        return when {
            tagLineNum == 0 -> buildBorder()
            tagLineNum <= nameText.font.height -> buildTagLine(nameText.lines[tagLineNum - 1])
            tagLineNum <= nameText.font.height + statusText.font.height ->
                buildTagLine(statusText.lines[tagLineNum - 1 - nameText.font.height])
            tagLineNum == nameText.font.height + statusText.font.height + 1 ->
                buildBorder()
            else -> "Error!"
        }
    }

    private fun buildBorder(): String {
        return BORDER_SYMBOL.repeat(tagLength)
    }

    private fun buildTagLine(textLine: String): String {
        val tagLine = BORDER_SYMBOL.repeat(BORDER_WIDTH) +
                " ".repeat(tagLength - 2 * BORDER_WIDTH) + BORDER_SYMBOL.repeat(BORDER_WIDTH)
        val startIndex = (tagLine.length - textLine.length) / 2
        val endIndex = startIndex + textLine.length
        return tagLine.replaceRange(startIndex, endIndex, textLine)
    }
}

/**
 * A string of text, to be output using the specified font. Available properties are [lines], each member of which
 * represents one line of symbols to be printed, the [font] to be used and the [length] in symbols of the text.
 */
class StyledText(private val text: String, val font: Font) {

    val lines: Array<String>
    val length: Int

    init {
        lines = Array(font.height) { getLine(it) }
        length = lines[0].length
    }

    private fun getLine(lineNumber: Int): String {
        var line = ""
        for (ch in text) {
            line += if (ch == ' ') {
                " ".repeat(font.spaceWidth)
            } else {
                font.glyph(ch).lines[lineNumber]
            }
        }
        return line
    }
}

/**
 * An ASCII font as specified by fileName, and using spaceWidth. Provides the [height] in lines of the font and the
 * [glyph] function to return the glyph for the specified character.
 */
enum class Font(fileName: String, val spaceWidth: Int) {

    MEDIUM("medium.txt", 5),
    ROMAN("roman.txt", 10);

    val height: Int
    private val glyphs: Array<Glyph>

    init {
        val scanner = Scanner(File(FONT_PATH + fileName))
        val inputLine = scanner.nextLine()
        val lineScanner = Scanner(inputLine)
        height = lineScanner.nextInt()
        val numberOfGlyphs = lineScanner.nextInt()
        glyphs = Array(numberOfGlyphs) { Glyph(height, scanner) }
    }

    fun glyph(character: Char): Glyph {
        for (glyph in glyphs) {
            if (character == glyph.character) return glyph
        }
        return glyphs[0]
    }

    class Glyph(height: Int, scanner: Scanner) {

        val character: Char
        private val width: Int

        init {
            val inputLine = scanner.nextLine()
            val lineScanner = Scanner(inputLine)
            character = lineScanner.next().first()
            width = lineScanner.nextInt()
        }

        val lines: Array<String> = Array(height) {
            scanner.nextLine()
        }
    }
}