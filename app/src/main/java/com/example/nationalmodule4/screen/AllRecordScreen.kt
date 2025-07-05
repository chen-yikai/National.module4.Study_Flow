package com.example.nationalmodule4.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nationalmodule4.LocalRecordDataModal
import com.example.nationalmodule4.maxWidth

@Composable
fun AllRecordScreen(innerPadding: PaddingValues) {
    val recordsModal = LocalRecordDataModal.current
    val records by recordsModal.data.collectAsState(emptyList())
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            item {
                Text("All Records", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for records") },
                    shape = CircleShape
                )
                Spacer(Modifier.height(10.dp))
            }
            items(records) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp), border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)) {
                        Text(it.name)
                    }
                }
            }
        }
    }
}
