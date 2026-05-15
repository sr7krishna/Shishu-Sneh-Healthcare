package com.example.shishu_sneh_healthcare.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.data.local.entity.MilestoneEntity
import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import com.example.shishu_sneh_healthcare.domain.repository.BabyRepository
import com.example.shishu_sneh_healthcare.domain.repository.MilestoneRepository
import com.example.shishu_sneh_healthcare.domain.repository.VaccineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(

    private val babyRepository: BabyRepository,
    private val vaccineRepository: VaccineRepository,
    private val milestoneRepository: MilestoneRepository,
    private val preferenceManager: com.example.shishu_sneh_healthcare.data.preferences.PreferenceManager

) : ViewModel() {

    fun saveBabyDetails(
        baby: BabyEntity,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            try {
                println("PROFILE_VM: Starting saveBabyDetails for ${baby.name}")

                // Insert baby and get generated ID
                val babyId = babyRepository.insertBaby(baby)
                println("PROFILE_VM: Baby saved with ID: $babyId")

                // Create updated baby object with real ID
                val savedBaby = baby.copy(id = babyId)

                // Seed vaccines
                println("PROFILE_VM: Seeding vaccines...")
                seedVaccines(savedBaby)

                // Seed milestones
                println("PROFILE_VM: Seeding milestones...")
                seedMilestones(savedBaby)

                // Mark profile setup as completed
                preferenceManager.setProfileSetupCompleted(true)
                
                println("PROFILE_VM: All data seeded successfully")

            } catch (e: Exception) {
                println("PROFILE_VM: ERROR during save/seed: ${e.message}")
                e.printStackTrace()
            } finally {
                // Navigate anyway so the user isn't stuck
                println("PROFILE_VM: Triggering onSuccess navigation")
                onSuccess()
            }
        }
    }

    // ---------------------------------------------------
    // Seed Vaccines
    // ---------------------------------------------------

    private suspend fun seedVaccines(
        baby: BabyEntity
    ) {

        val dob = baby.dob

        val vaccines = listOf(

            VaccineEntity(
                babyId = baby.id,
                name = "BCG",
                disease = "Tuberculosis",
                scheduledDate = dob,
                givenDate = null,
                hospital = null,
                doctor = null,
                status = "Upcoming"
            ),

            VaccineEntity(
                babyId = baby.id,
                name = "OPV 1",
                disease = "Polio",
                scheduledDate = dob + TimeUnit.DAYS.toMillis(45),
                givenDate = null,
                hospital = null,
                doctor = null,
                status = "Upcoming"
            ),

            VaccineEntity(
                babyId = baby.id,
                name = "DPT 1",
                disease = "Diphtheria, Pertussis, Tetanus",
                scheduledDate = dob + TimeUnit.DAYS.toMillis(45),
                givenDate = null,
                hospital = null,
                doctor = null,
                status = "Upcoming"
            ),

            VaccineEntity(
                babyId = baby.id,
                name = "Hepatitis B",
                disease = "Hepatitis B",
                scheduledDate = dob + TimeUnit.DAYS.toMillis(45),
                givenDate = null,
                hospital = null,
                doctor = null,
                status = "Upcoming"
            ),

            VaccineEntity(
                babyId = baby.id,
                name = "OPV 2",
                disease = "Polio",
                scheduledDate = dob + TimeUnit.DAYS.toMillis(75),
                givenDate = null,
                hospital = null,
                doctor = null,
                status = "Upcoming"
            ),

            VaccineEntity(
                babyId = baby.id,
                name = "DPT 2",
                disease = "Diphtheria, Pertussis, Tetanus",
                scheduledDate = dob + TimeUnit.DAYS.toMillis(75),
                givenDate = null,
                hospital = null,
                doctor = null,
                status = "Upcoming"
            ),

            VaccineEntity(
                babyId = baby.id,
                name = "Measles",
                disease = "Measles",
                scheduledDate = dob + TimeUnit.DAYS.toMillis(270),
                givenDate = null,
                hospital = null,
                doctor = null,
                status = "Upcoming"
            )
        )

        vaccineRepository.insertVaccines(vaccines)
    }

    // ---------------------------------------------------
    // Seed Milestones
    // ---------------------------------------------------

    private suspend fun seedMilestones(
        baby: BabyEntity
    ) {

        val milestones = listOf(

            MilestoneEntity(
                babyId = baby.id,
                month = 2,
                description = "Smiles socially",
                status = "Not Yet",
                achievedDate = null,
                notes = null
            ),

            MilestoneEntity(
                babyId = baby.id,
                month = 3,
                description = "Holds head up steadily",
                status = "Not Yet",
                achievedDate = null,
                notes = null
            ),

            MilestoneEntity(
                babyId = baby.id,
                month = 4,
                description = "Rolls over",
                status = "Not Yet",
                achievedDate = null,
                notes = null
            ),

            MilestoneEntity(
                babyId = baby.id,
                month = 6,
                description = "Sits without support",
                status = "Not Yet",
                achievedDate = null,
                notes = null
            ),

            MilestoneEntity(
                babyId = baby.id,
                month = 8,
                description = "Starts crawling",
                status = "Not Yet",
                achievedDate = null,
                notes = null
            ),

            MilestoneEntity(
                babyId = baby.id,
                month = 12,
                description = "Says first words",
                status = "Not Yet",
                achievedDate = null,
                notes = null
            )
        )

        milestoneRepository.insertMilestones(milestones)
    }
}