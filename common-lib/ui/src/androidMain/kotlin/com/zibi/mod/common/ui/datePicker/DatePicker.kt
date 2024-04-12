package com.zibi.mod.common.ui.datePicker

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.time.LocalDate

class DatePicker(private val context: Context) {

  fun show(
    initialLocalDate: LocalDate,
    minDate: Long? = null,
    onDateChange: (LocalDate) -> Unit
  ) {
    val initialYear = initialLocalDate.year
    val initialMonth = initialLocalDate.monthValue - 1
    val initialDayOfMonth = initialLocalDate.dayOfMonth
    val datePickerDialog = DatePickerDialog(
      context,
      { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
        val newLocalDate = LocalDate.of(
          year,
          month + 1,
          dayOfMonth
        )
        onDateChange(newLocalDate)
      },
      initialYear,
      initialMonth,
      initialDayOfMonth
    )

    if (minDate != null) datePickerDialog.datePicker.minDate = minDate

    datePickerDialog.show()
  }
}