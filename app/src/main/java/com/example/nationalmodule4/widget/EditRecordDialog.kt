package com.example.nationalmodule4.widget

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.nationalmodule4.LocalRecordDataModal

@Composable
fun EditRecordDialog(id: String, dismiss: () -> Unit) {
    var filename by remember { mutableStateOf("") }
    val recordDataModal = LocalRecordDataModal.current

    AlertDialog(
        onDismissRequest = { dismiss() },
        confirmButton = {
            Button(onClick = {
                if (filename.isNotEmpty()) recordDataModal.updateName(
                    id,
                    filename
                )
                dismiss()
                filename = ""
            }) { Text("Name it") }
        },
        title = { Text("Enter Filename") },
        text = { OutlinedTextField(filename, onValueChange = { filename = it }) })
}