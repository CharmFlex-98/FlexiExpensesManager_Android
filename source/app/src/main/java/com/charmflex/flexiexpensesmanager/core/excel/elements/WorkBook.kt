package com.charmflex.flexiexpensesmanager.core.excel.elements

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

internal class WorkBook(
    override val xssfWorkbook: XSSFWorkbook
) : ExcelElement {

    fun sheet(
        name: String,
        builder: Sheet.() -> Unit
    ) {
        Sheet(
            xssfWorkbook,
            name
        ).apply(builder)
    }

    fun write(file: File) {
        FileOutputStream(file).use { out ->
            xssfWorkbook.write(out)
        }
    }
}

internal fun workbook(
    xssfWorkbook: XSSFWorkbook,
    builder: WorkBook.() -> Unit
) : WorkBook {
    return WorkBook(xssfWorkbook).apply(builder)
}