package com.bismark.currency.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bismark.currency.extensions.empty
import java.util.Date

@Entity(tableName = "conversion")
data class ConversionResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: Date?,
    val rate: Long,
    val timestamp: Long,
    val from: String,
    val to: String,
    val amount: Long,
    val success: Boolean
){
    companion object{
        fun empty() = ConversionResultEntity(
            id = 0L,
            date = null,
            rate = 0L,
            timestamp = 0L,
            from = String.empty(),
            to = String.empty(),
            amount = 0L,
            success = false
        )
    }
}
