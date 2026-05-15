package com.example.shishu_sneh_healthcare.presentation.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.local.entity.HealthRecordEntity
import com.example.shishu_sneh_healthcare.domain.repository.HealthRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    private val repository: HealthRecordRepository
) : ViewModel() {

    // ---------------------------------------------------
    // Health Records State
    // ---------------------------------------------------

    private val _healthRecords =
        MutableStateFlow<List<HealthRecordEntity>>(emptyList())

    val healthRecords: StateFlow<List<HealthRecordEntity>> =
        _healthRecords.asStateFlow()

    // ---------------------------------------------------
    // Loading State
    // ---------------------------------------------------

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    // ---------------------------------------------------
    // Error State
    // ---------------------------------------------------

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    // ---------------------------------------------------
    // Load Records
    // ---------------------------------------------------

    fun loadHealthRecords(
        babyId: Long
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                repository
                    .getHealthRecords(babyId)
                    .collectLatest { records ->

                        _healthRecords.value = records

                        _isLoading.value = false
                    }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Unknown error"

                _isLoading.value = false
            }
        }
    }

    // ---------------------------------------------------
    // Add Record
    // ---------------------------------------------------

    fun addHealthRecord(
        record: HealthRecordEntity
    ) {

        viewModelScope.launch {

            try {

                repository.insertHealthRecord(record)

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Failed to save record"
            }
        }
    }

    // ---------------------------------------------------
    // Clear Error
    // ---------------------------------------------------

    fun clearError() {
        _error.value = null
    }
}
