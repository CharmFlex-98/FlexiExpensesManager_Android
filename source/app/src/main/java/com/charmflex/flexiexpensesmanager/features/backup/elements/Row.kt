package com.charmflex.flexiexpensesmanager.features.backup.elements

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate

internal class Row(
    override val xssfWorkbook: XSSFWorkbook,
    xssfSheet: XSSFSheet,
    rowIndex: Int
) : ExcelElement {

    private val row = xssfSheet.createRow(rowIndex)
    private var currentCellIndex = 0

    fun stringCell(value: String) {
        Cell(
            xssfWorkbook,
            row,
            Content.StringContent(value),
            currentCellIndex++
        )
    }

    fun doubleCell(value: Double) {
        Cell(
            xssfWorkbook,
            row,
            Content.DoubleContent(value),
            currentCellIndex++
        )
    }

    fun dateCell(value: LocalDate) {
        Cell(
            xssfWorkbook,
            row,
            Content.DateContent(value),
            currentCellIndex++
        )
    }

    fun booleanCell(value: Boolean) {
        Cell(
            xssfWorkbook,
            row,
            Content.BooleanContent(value),
            currentCellIndex++
        )
    }

    fun emptyCell() {
        Cell(
            xssfWorkbook,
            row,
            Content.EmptyContent,
            currentCellIndex++
        )
    }

}