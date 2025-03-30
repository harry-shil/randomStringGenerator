package com.example.randomstringgenerator.ui.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.randomstringgenerator.R
import com.example.randomstringgenerator.RandomStringViewModel
import com.example.randomstringgenerator.model.RandomStringData
import com.example.randomstringgenerator.ui.ScreenUtils.DeleteDialogBox
import kotlinx.coroutines.launch

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
    val errorMessage = viewModel.errorMessage.collectAsState()
    val keyBoardController = LocalSoftwareKeyboardController.current
    val errorShakeAnimation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = modifier
                .padding(
                    vertical = if (isPortrait) 16.dp else 10.dp,
                    horizontal = if (isPortrait) 16.dp else 40.dp
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                        .animateContentSize()
                        .padding(if (isPortrait) 16.dp else 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        LaunchedEffect(key1 = errorMessage.value) {
                            if (errorMessage.value.isNotEmpty()) {
                                coroutineScope.launch {
                                    repeat(2) {
                                        errorShakeAnimation.animateTo(
                                            -5f,
                                            animationSpec = tween(50)
                                        )
                                        errorShakeAnimation.animateTo(
                                            5f,
                                            animationSpec = tween(50)
                                        )
                                    }
                                    errorShakeAnimation.animateTo(0f)
                                }
                            }
                        }
                        OutlinedTextField(
                            isError = errorMessage.value != "",
                            label = { Text(text = stringResource(R.string.enter_size)) },
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = context.getString(R.string.max_size),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onValueChange = { newText ->
                                viewModel.updateInputField(newText, updateError = {
                                    viewModel.updateErrorMessage(context.getString(it))
                                })
                            },
                            value = inputValue.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(x = errorShakeAnimation.value.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(onSearch = {
                                onSearchString(
                                    hideKeyBoard = { keyBoardController?.hide() },
                                    inputValue = inputValue.value,
                                    updateErrorMessage = { messageResId ->
                                        viewModel.updateErrorMessage(context.getString(messageResId))

                                    },
                                    fetchRandomString = {
                                        viewModel.fetchRandomString(inputValue.value.toInt()) { stringResId ->
                                            Toast.makeText(
                                                context,
                                                context.getString(stringResId),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    }, updateInputField = { input ->
                                        viewModel.updateInputField(
                                            context.getString(input), updateError = {
                                                viewModel.updateErrorMessage(context.getString(it))
                                            }
                                        )
                                    }
                                )
                            })
                        )
                        AnimatedVisibility(
                            visible = errorMessage.value != "",
                            enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                                animationSpec = tween(1000)
                            )
                        ) {
                            Text(
                                text = errorMessage.value,
                                modifier = Modifier.padding(vertical = 4.dp),
                                fontSize = 12.sp,
                                color = Color(0xFFFF0000),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .animateContentSize(),
                        horizontalArrangement = if (randomStringList.value.isEmpty()) Arrangement.Center else if (!isPortrait) Arrangement.SpaceAround else Arrangement.SpaceBetween
                    ) {
                        Button(modifier = Modifier.padding(10.dp),
                            onClick = {
                                onSearchString(
                                    hideKeyBoard = { keyBoardController?.hide() },
                                    inputValue = inputValue.value,
                                    updateErrorMessage = { messageResId ->
                                        viewModel.updateErrorMessage(context.getString(messageResId))
                                    },
                                    fetchRandomString = {
                                        viewModel.fetchRandomString(inputValue.value.toInt()) { stringResId ->
                                            Toast.makeText(
                                                context,
                                                context.getString(stringResId),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    }, updateInputField = { input ->
                                        viewModel.updateInputField(
                                            context.getString(input),
                                            updateError = {
                                                viewModel.updateErrorMessage(context.getString(it))
                                            })
                                    }
                                )
                            }) {
                            Text(text = context.getString(R.string.fetch_string))
                        }
                        AnimatedVisibility(visible = randomStringList.value.isNotEmpty()) {
                            Button(
                                onClick = {
                                    showDeleteDialog.value = true
                                },
                                modifier = Modifier.padding(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red
                                )
                            ) {
                                Text(text = stringResource(R.string.clear_all))
                            }
                        }

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


@Composable
fun StringItem(item: RandomStringData, viewModel: RandomStringViewModel) {
    val showItemDeleteDialog = remember { mutableStateOf(false) }
    Card(modifier = Modifier.padding(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.created, modifier = Modifier
                    .padding(horizontal = 2.dp)
            )
            IconButton(onClick = {
                showItemDeleteDialog.value = true
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color.Red
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = "Length: ${item.length}"
            )
            Text(
                text = item.value, modifier = Modifier
                    .padding(vertical = 8.dp)
            )
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

fun onSearchString(
    hideKeyBoard: () -> Unit,
    inputValue: String,
    fetchRandomString: () -> Unit,
    updateInputField: (Int) -> Unit,
    updateErrorMessage: (Int) -> Unit
) {
    hideKeyBoard()
    try {
        if (inputValue.isNotEmpty() && inputValue.trim().toInt() != 0) {
            if (inputValue.trim().toInt() > 10000) {
                updateErrorMessage(R.string.out_of_bounds)
            } else {
                updateErrorMessage(R.string.empty_string)
                fetchRandomString()
            }
        } else {
            updateErrorMessage(R.string.field_can_not_be_empty)
            updateInputField(R.string.empty_string)
        }
    } catch (error: NumberFormatException) {
        updateErrorMessage(R.string.out_of_bounds)
        updateInputField(R.string.empty_string)
    } catch (error: Exception) {
        updateErrorMessage(R.string.invalid_input)
        updateInputField(R.string.empty_string)
    }
}
