package com.example.randomstringgenerator

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RandomStringTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private val validTestString = "1234"
    private val invalidTestString = "abcd"
    private val invalidSize = "10001"

    @Test
    fun test_Initial_UI_State() {
        rule.onNodeWithText(rule.activity.getString(R.string.enter_size)).assertExists()
        rule.onNodeWithText(rule.activity.getString(R.string.fetch_string)).assertExists()
        rule.onNodeWithText(rule.activity.getString(R.string.max_size)).assertExists()
        rule.onNodeWithText(rule.activity.getString(R.string.no_data)).assertExists()
        rule.onNodeWithText(rule.activity.getString(R.string.clear_all)).assertDoesNotExist()

    }

    @Test
    fun test_InputField_validInput() {
        rule.onNodeWithText(rule.activity.getString(R.string.enter_size))
            .performTextInput(validTestString)
        rule.onNodeWithText(validTestString).assertExists()
    }

    @Test
    fun test_InputField_invalidInput() {
        rule.onNodeWithText(rule.activity.getString(R.string.enter_size))
            .performTextInput(invalidTestString)
        rule.onNodeWithText(rule.activity.getString(R.string.invalid_input)).assertExists()
    }

    @Test
    fun test_Fetch_Button_isFunctional() {
        rule.onNodeWithText(rule.activity.getString(R.string.enter_size))
            .performTextInput(validTestString)
        rule.onNodeWithText(rule.activity.getString(R.string.fetch_string)).performClick()
    }

    @Test
    fun test_Input_Validation_ForValue0() {
        rule.onNodeWithText(rule.activity.getString(R.string.enter_size))
            .performTextInput("0")
        rule.onNodeWithText(rule.activity.getString(R.string.fetch_string))
            .performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.field_can_not_be_empty))
            .assertExists()
    }

    @Test
    fun test_Input_Validation_ForValueEmpty() {
        rule.onNodeWithText(rule.activity.getString(R.string.fetch_string))
            .performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.field_can_not_be_empty))
            .assertExists()
    }

    @Test
    fun test_Input_InvalidSize() {
        rule.onNodeWithText(rule.activity.getString(R.string.enter_size))
            .performTextInput(invalidSize)
        rule.onNodeWithText(rule.activity.getString(R.string.fetch_string))
            .performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.out_of_bounds))
            .assertExists()
    }
}