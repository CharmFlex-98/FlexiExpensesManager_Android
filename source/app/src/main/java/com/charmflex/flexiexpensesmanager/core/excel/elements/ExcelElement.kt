package com.charmflex.flexiexpensesmanager.core.excel.elements

import org.apache.poi.xssf.usermodel.XSSFWorkbook

internal interface ExcelElement {
    val xssfWorkbook: XSSFWorkbook
}