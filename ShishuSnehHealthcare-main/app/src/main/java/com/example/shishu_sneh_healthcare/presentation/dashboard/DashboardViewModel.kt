package com.example.shishu_sneh_healthcare.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import com.example.shishu_sneh_healthcare.domain.use_case.GetBabiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(

    private val getBabiesUseCase: GetBabiesUseCase,

    private val vaccineRepository: VaccineRepository

) : ViewModel() {

    // ---------------------------------------------------
    // Babies
    // ---------------------------------------------------

    private val _babies =
        MutableStateFlow<List<BabyEntity>>(emptyList())

    val babies: StateFlow<List<BabyEntity>> =
        _babies.asStateFlow()

    // ---------------------------------------------------
    // Selected Baby
    // ---------------------------------------------------

    private val _selectedBaby =
        MutableStateFlow<BabyEntity?>(null)

    val selectedBaby: StateFlow<BabyEntity?> =
        _selectedBaby.asStateFlow()

    // ---------------------------------------------------
    // Vaccines
    // ---------------------------------------------------

    private val _vaccines =
        MutableStateFlow<List<VaccineEntity>>(emptyList())

    val vaccines: StateFlow<List<VaccineEntity>> =
        _vaccines.asStateFlow()

    // ---------------------------------------------------
    // Current Tab
    // ---------------------------------------------------

    private val _currentTab =
        MutableStateFlow(0)

    val currentTab: StateFlow<Int> =
        _currentTab.asStateFlow()

    // ---------------------------------------------------
    // Set Current Tab
    // ---------------------------------------------------

    fun setCurrentTab(index: Int) {
        _currentTab.value = index
    }

    // ---------------------------------------------------
    // Load Babies
    // ---------------------------------------------------

    fun loadBabies(userId: String) {

        viewModelScope.launch {

            try {

                getBabiesUseCase(userId)
                    .collectLatest { babyList ->

                        _babies.value = babyList

                        if (
                            babyList.isNotEmpty() &&
                            _selectedBaby.value == null
                        ) {

                            val baby =
                                babyList.first()

                            _selectedBaby.value = baby

                            loadVaccines(baby.id)
                        }
                    }

            } catch (_: Exception) {

            }
        }
    }

    // ---------------------------------------------------
    // Load Vaccines
    // ---------------------------------------------------

    private fun loadVaccines(
        babyId: Long
    ) {

        viewModelScope.launch {

            vaccineRepository
                .getVaccinesForBaby(babyId)
                .collectLatest {

                    _vaccines.value = it
                }
        }
    }

    // ---------------------------------------------------
    // Select Baby
    // ---------------------------------------------------

    fun selectBaby(baby: BabyEntity) {

        _selectedBaby.value = baby

        loadVaccines(baby.id)
    }

    // ---------------------------------------------------
    // Calculate Baby Age
    // ---------------------------------------------------

    fun getBabyAge(
        baby: BabyEntity
    ): String {

        return try {

            val dobCalendar =
                Calendar.getInstance()

            dobCalendar.timeInMillis =
                baby.dob

            val today =
                Calendar.getInstance()

            var years =
                today.get(Calendar.YEAR) -
                        dobCalendar.get(Calendar.YEAR)

            var months =
                today.get(Calendar.MONTH) -
                        dobCalendar.get(Calendar.MONTH)

            if (months < 0) {

                years--

                months += 12
            }

            when {

                years > 0 -> {
                    "$years years $months months"
                }

                months > 0 -> {
                    "$months months old"
                }

                else -> {

                    val days =
                        today.get(Calendar.DAY_OF_MONTH) -
                                dobCalendar.get(Calendar.DAY_OF_MONTH)

                    "$days days old"
                }
            }

        } catch (_: Exception) {

            "Unknown age"
        }
    }

    // ---------------------------------------------------
    // Upcoming Vaccine
    // ---------------------------------------------------

    fun getUpcomingVaccine(): VaccineEntity? {

        val currentTime =
            System.currentTimeMillis()

        return _vaccines.value

            .filter {

                !it.status.equals(
                    "Done",
                    ignoreCase = true
                )
            }

            .sortedBy {

                kotlin.math.abs(
                    it.scheduledDate - currentTime
                )
            }

            .firstOrNull()
    }

    // ---------------------------------------------------
    // Vaccine Alert Text
    // ---------------------------------------------------

    fun getUpcomingVaccineText(): String {

        val vaccine =
            getUpcomingVaccine()
                ?: return "No upcoming vaccines"

        val currentTime =
            System.currentTimeMillis()

        return when {

            vaccine.scheduledDate < currentTime -> {

                "${vaccine.name} overdue"
            }

            else -> {

                val days =
                    (
                            vaccine.scheduledDate -
                                    currentTime
                            ) / (1000 * 60 * 60 * 24)

                "${vaccine.name} due in $days days"
            }
        }
    }

    // ---------------------------------------------------
    // Empty State
    // ---------------------------------------------------

    fun hasNoBabies(): Boolean {

        return _babies.value.isEmpty()
    }
}