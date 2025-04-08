package com.example.randomstringgenerator

import android.app.Application
import com.example.randomstringgenerator.model.RandomStringData
import com.example.randomstringgenerator.repository.RandomStringRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class RandomStringViewModelTest {

    @Mock
    private lateinit var repository: RandomStringRepository

    private lateinit var viewModel: RandomStringViewModel

    @Before
    fun setup(){
        viewModel = RandomStringViewModel(Application())
    }

    @Test
    fun test_fetchString_with_valid_input(){

        val randomStringData = RandomStringData("abcd1234", 8, "31 March, 2025 11:41")
        whenever(repository.getRandomString(8, any())).thenReturn(randomStringData)

        viewModel.fetchRandomString(8){}

        assertEquals(1, viewModel.randomStringList.value.size)
        assertEquals(randomStringData, viewModel.randomStringList.value[0])
    }
}