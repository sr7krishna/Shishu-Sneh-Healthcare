package com.example.shishu_sneh_healthcare.domain.repository

import com.example.shishu_sneh_healthcare.data.local.entity.HealthRecordEntity
import kotlinx.coroutines.flow.Flow

interface HealthRecordRepository {
    fun getHealthRecords(babyId: Long): Flow<List<HealthRecordEntity>>
    suspend fun insertHealthRecord(record: HealthRecordEntity)
}
