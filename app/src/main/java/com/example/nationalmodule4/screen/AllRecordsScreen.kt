package com.example.nationalmodule4.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.nationalmodule4.LocalPlayerModal
import com.example.nationalmodule4.LocalRecordDataModal
import com.example.nationalmodule4.LocalRecordViewNavController
import com.example.nationalmodule4.LocalShareModal
import com.example.nationalmodule4.helper.PlayerModal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllRecords(innerPadding: PaddingValues) {
    val recordsModal = LocalRecordDataModal.current
    var searchText by remember { mutableStateOf("") }
    val records by recordsModal.get("%$searchText%", true).collectAsState(initial = emptyList())
    val nav = LocalRecordViewNavController.current
    val config = LocalConfiguration.current
    val playerModal = LocalPlayerModal.current

    Row {
        LazyColumn(
            contentPadding = PaddingValues(top = innerPadding.calculateTopPadding()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 10.dp)
                ) {
                    Text("All Records", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search for records") },
                        trailingIcon = {
                            if (searchText.isNotEmpty())
                                IconButton(onClick = {
                                    searchText = ""
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            else
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null
                                )
                        },
                        shape = CircleShape
                    )
                }
            }
            items(records) {
                Card(
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    onClick = {
                        playerModal.updateRecordId(it.id)
                        if (config.screenWidthDp.dp <= 800.dp) nav.navigate(ManageRecordScreenRoute.Preview.name)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sdf = SimpleDateFormat("MMMM d hh:mm a", Locale.ENGLISH)
                        Column {
                            Text(it.name, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(10.dp))
                            Text(sdf.format(Date(it.date)))
                        }
                        IconButton(onClick = {
                            recordsModal.deleteRecord(it)
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}