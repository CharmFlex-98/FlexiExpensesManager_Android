package com.charmflex.flexiexpensesmanager.features.backup.elements

import com.charmflex.flexiexpensesmanager.core.utils.DATE_ONLY_DEFAULT_PATTERN
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
                    setCellValue(content.value).apply {
                        val cellStyle = xssfWorkbook.createCellStyle()
                        val creationHelper = xssfWorkbook.creationHelper
                        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(DATE_ONLY_DEFAULT_PATTERN))
                        setCellStyle(cellStyle)
                    }
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