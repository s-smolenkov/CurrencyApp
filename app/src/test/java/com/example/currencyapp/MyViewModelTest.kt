package com.example.currencyapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class MyViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var uiStateObserver: Observer<MyViewModel.UIState>

    private lateinit var viewModel: MyViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MyViewModel(repository).apply {
            uiState.observeForever(uiStateObserver)
        }
    }

    @After
    fun tearDown() {
        viewModel.uiState.removeObserver(uiStateObserver)
        Dispatchers.resetMain()
    }
    @Test
    fun verify_UIState_is_Result_when_data_is_successfully_fetched() = runTest {
        val mockData = Data("1", "BTC", "₿", "50000")
        val mockResponse = Response.success(BitcoinResponse(mockData))
        whenever(repository.getCurrencyByName("bitcoin")).thenReturn(mockResponse)

        val uiStateCaptor = argumentCaptor<MyViewModel.UIState>()

        viewModel.getData()

        verify(uiStateObserver, times(3)).onChanged(uiStateCaptor.capture())
        val capturedValues = uiStateCaptor.allValues
        assertEquals(MyViewModel.UIState.Empty, capturedValues[0])
        assertEquals(MyViewModel.UIState.Processing, capturedValues[1])
        assertEquals("1 BTC ₿ 50000", (capturedValues[2] as MyViewModel.UIState.Result).title)
    }

    @Test
    fun verify_UIState_is_Error_when_request_fails() = runTest {
        // Arrange
        val errorMessage = "Network Error"
        whenever(repository.getCurrencyByName("bitcoin")).thenThrow(RuntimeException(errorMessage))

        val uiStateCaptor = argumentCaptor<MyViewModel.UIState>()

        viewModel.getData()

        verify(uiStateObserver, times(3)).onChanged(uiStateCaptor.capture())
        val capturedValues = uiStateCaptor.allValues
        assertEquals(MyViewModel.UIState.Empty, capturedValues[0])
        assertEquals(MyViewModel.UIState.Processing, capturedValues[1])
        assertEquals(errorMessage, (capturedValues[2] as MyViewModel.UIState.Error).description)
    }
}





