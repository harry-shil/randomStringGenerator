package com.example.randomstringgenerator.ui.screen

import com.example.randomstringgenerator.R
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.randomstringgenerator.RandomStringViewModel
import com.example.randomstringgenerator.model.RandomStringData
import com.example.randomstringgenerator.ui.ScreenUtils.DeleteDialogBox

@SuppressLint("NewApi")
@Composable
fun RandomStringScreen(viewModel: RandomStringViewModel, modifier: Modifier = Modifier) {
    val randomStringList = viewModel.randomStringList.collectAsState()
    val inputValue = viewModel.inputValue.collectAsState()
    val screenLoader = viewModel.screenLoader.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val showDeleteDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = modifier
                .padding(if (isPortrait) 16.dp else 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isPortrait) 20.dp else 10.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                        .padding(if (isPortrait) 16.dp else 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        label = { Text(text = stringResource(R.string.enter_size)) },
                        onValueChange = { newText -> viewModel.updateInputField(newText) },
                        value = inputValue.value,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    Button(modifier = Modifier.padding(10.dp), onClick = {
                        if (inputValue.value.isNotEmpty() && inputValue.value.toInt() != 0) {
                            viewModel.fetchRandomString(inputValue.value.toInt()) { stringResId ->
                                Toast.makeText(
                                    context,
                                    context.getString(stringResId),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        }
                        else
                            Toast.makeText(
                                context,
                                context.getString(R.string.invalid_input),
                                Toast.LENGTH_SHORT
                            ).show()
                    }) {
                        Text(text = "Fetch String")
                    }
                }
            }

            if (randomStringList.value.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_data),
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(randomStringList.value) { randomString ->
                        StringItem(randomString, viewModel)
                    }
                    if (randomStringList.value.isNotEmpty()) {
                        item {
                            Button(onClick = {
                                showDeleteDialog.value = true

                            }, modifier = Modifier.padding(10.dp)) {
                                Text(text = "Clear All")
                            }
                        }

                    }

                }
            }
            if (showDeleteDialog.value) {
                DeleteDialogBox(onConfirm = {
                    viewModel.clearAll()
                    showDeleteDialog.value = false
                }, onDismiss = {
                    showDeleteDialog.value = false
                }, message = stringResource(R.string.delete_all))
            }
        }
        if (screenLoader.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = Color.Blue
                )
            }
        }
    }
}


@Composable
fun StringItem(item: RandomStringData, viewModel: RandomStringViewModel) {
    val showItemDeleteDialog = remember { mutableStateOf(false) }
    Card(modifier = Modifier.padding(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.value, modifier = Modifier
                    .weight(0.4f)
                    .padding(horizontal = 3.dp)
            )
            Text(
                text = item.created, modifier = Modifier
                    .weight(0.8f)
                    .padding(horizontal = 2.dp)
            )
            IconButton(onClick = {
                showItemDeleteDialog.value = true
            }, modifier = Modifier.weight(0.2f)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color.Red
                )
            }
        }
    }
    if (showItemDeleteDialog.value) {
        DeleteDialogBox(onConfirm = {
            viewModel.deleteString(item)
            showItemDeleteDialog.value = false
        }, onDismiss = {
            showItemDeleteDialog.value = false
        }, message = stringResource(R.string.delete_item))
    }

}