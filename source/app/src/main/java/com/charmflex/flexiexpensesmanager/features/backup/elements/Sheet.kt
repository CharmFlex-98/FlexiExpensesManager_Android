package com.charmflex.flexiexpensesmanager.features.backup.elements

import org.apache.poi.xssf.usermodel.XSSFWorkbook

internal class Sheet(
    override val xssfWorkbook: XSSFWorkbook,
    sheetName: String
) : ExcelElement {
    private val sheet = xssfWorkbook.createSheet(sheetName)
    private var currentRowIndex = 0

    fun row(builder: Row.() -> Unit) {
        Row(
            xssfWorkbook,
            sheet,
            currentRowIndex++
        ).apply(builder)
    }
}