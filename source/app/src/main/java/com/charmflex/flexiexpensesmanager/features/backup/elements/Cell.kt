package com.charmflex.flexiexpensesmanager.features.backup.elements

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate

internal class Cell(
    override val xssfWorkbook: XSSFWorkbook,
    private val xssfRow: XSSFRow,
    private val content: Content,
    private val columnIndex: Int
) : ExcelElement {
    init {
        xssfRow.createCell(columnIndex).apply {
            when (content) {
                is Content.DoubleContent -> {
                    setCellValue(content.value)
                }
                is Content.StringContent -> {
                    setCellValue(content.value)
                }
                is Content.BooleanContent -> {
                    setCellValue(content.value)
                }
                is Content.DateContent -> {
                    setCellValue(content.value)
                }
                Content.EmptyContent -> {
                    setBlank()
                }
            }
        }
    }
}

internal sealed interface Content {
    data class DoubleContent(
        val value: Double
    ) : Content

    data class StringContent(
        val value: String
    ) : Content

    data class BooleanContent(
        val value: Boolean
    ) : Content

    data class DateContent(
        val value: LocalDate
    ) : Content

    object EmptyContent : Content
}