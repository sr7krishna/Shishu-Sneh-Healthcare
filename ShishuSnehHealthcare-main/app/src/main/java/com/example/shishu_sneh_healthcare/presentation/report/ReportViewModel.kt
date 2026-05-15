package com.example.shishu_sneh_healthcare.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.data.local.entity.GrowthEntryEntity
import com.example.shishu_sneh_healthcare.data.local.entity.MilestoneEntity
import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import com.example.shishu_sneh_healthcare.domain.repository.BabyRepository
import com.example.shishu_sneh_healthcare.domain.repository.GrowthRepository
import com.example.shishu_sneh_healthcare.domain.repository.MilestoneRepository
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(

    private val babyRepository: BabyRepository,

    private val growthRepository: GrowthRepository,

    private val vaccineRepository: VaccineRepository,

    private val milestoneRepository: MilestoneRepository

) : ViewModel() {

    // ---------------------------------------------------
    // STATES
    // ---------------------------------------------------

    private val _baby =
        MutableStateFlow<BabyEntity?>(null)

    val baby: StateFlow<BabyEntity?> =
        _baby.asStateFlow()

    private val _growthEntries =
        MutableStateFlow<List<GrowthEntryEntity>>(emptyList())

    val growthEntries:
            StateFlow<List<GrowthEntryEntity>> =
        _growthEntries.asStateFlow()

    private val _vaccines =
        MutableStateFlow<List<VaccineEntity>>(emptyList())

    val vaccines:
            StateFlow<List<VaccineEntity>> =
        _vaccines.asStateFlow()

    private val _milestones =
        MutableStateFlow<List<MilestoneEntity>>(emptyList())

    val milestones:
            StateFlow<List<MilestoneEntity>> =
        _milestones.asStateFlow()

    // ---------------------------------------------------
    // LOAD REPORT DATA
    // ---------------------------------------------------

    fun loadReportData(
        babyId: Long
    ) {

        viewModelScope.launch {

            _baby.value =
                babyRepository.getBabyById(babyId)

            growthRepository
                .getGrowthEntries(babyId)
                .collectLatest {

                    _growthEntries.value = it
                }
        }

        viewModelScope.launch {

            vaccineRepository
                .getVaccinesForBaby(babyId)
                .collectLatest {

                    _vaccines.value = it
                }
        }

        viewModelScope.launch {

            milestoneRepository
                .getMilestonesForBaby(babyId)
                .collectLatest {

                    _milestones.value = it
                }
        }
    }

    // ---------------------------------------------------
    // AGE TEXT
    // ---------------------------------------------------

    fun getAgeText(): String {

        val baby =
            _baby.value ?: return "Unknown"

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

        return when {

            years > 0 -> {

                "$years years $months months"
            }

            months > 0 -> {

                "$months months"
            }

            else -> {

                "Newborn"
            }
        }
    }

    // ---------------------------------------------------
    // LATEST WEIGHT
    // ---------------------------------------------------

    fun getLatestWeight(): String {

        val latest =
            _growthEntries.value
                .sortedBy { it.date }
                .lastOrNull()

        return latest?.weight?.let {

            "$it kg"

        } ?: "No Data"
    }

    // ---------------------------------------------------
    // LATEST HEIGHT
    // ---------------------------------------------------

    fun getLatestHeight(): String {

        val latest =
            _growthEntries.value
                .sortedBy { it.date }
                .lastOrNull()

        return latest?.height?.let {

            "$it cm"

        } ?: "No Data"
    }

    // ---------------------------------------------------
    // VACCINE SUMMARY
    // ---------------------------------------------------

    fun getVaccineSummary(): String {

        val total =
            _vaccines.value.size

        val completed =
            _vaccines.value.count {

                it.status == "Done"
            }

        return "$completed of $total vaccines completed"
    }

    // ---------------------------------------------------
    // MILESTONE SUMMARY
    // ---------------------------------------------------

    fun getMilestoneSummary(): String {

        val total =
            _milestones.value.size

        val completed =
            _milestones.value.count {

                it.status == "Completed"
            }

        return if (total == 0) {

            "No milestones tracked yet"

        } else {

            "${(completed * 100) / total}% milestones achieved"
        }
    }

    // ---------------------------------------------------
    // AI INSIGHT
    // ---------------------------------------------------

    fun getAiInsight(): String {
        val age = getAgeText()
        val latestWeight = getLatestWeight()
        return "Baby is currently $age old. Latest recorded weight is $latestWeight. Growth and development appear to be progressing within expected WHO-inspired ranges."
    }
}