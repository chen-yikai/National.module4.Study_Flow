package com.example.nationalmodule4.room

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val path: String,
    val duration: Long,
    val date: Long,
)

@Dao
interface RecordDao {
    @Query("SELECT 1")
    suspend fun comeAlive(): Int

    @Query("SELECT * FROM records")
    fun getAll(): Flow<List<Record>>

    @Insert
    suspend fun add(record: Record)

    @Query("UPDATE records SET name = :name WHERE id = :id")
    suspend fun updateName(id: String, name: String)
}

@Database(entities = [Record::class], version = 2)
abstract class StudyFlowDataBase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}

object StudyFlowRepo {
    private var instance: StudyFlowDataBase? = null

    fun getDataBase(context: Context): StudyFlowDataBase {
        return instance ?: synchronized(this) {
            val db = Room.databaseBuilder(context, StudyFlowDataBase::class.java, "study_flow_db")
                .fallbackToDestructiveMigration().build()
            instance = db
            db
        }
    }
}

class RecordDataModal(private val dao: RecordDao) : ViewModel() {
    val data = dao.getAll()

    init {
        viewModelScope.launch {
            dao.comeAlive()
        }
    }

    suspend fun add(record: Record) {
        dao.add(record)
    }

    fun updateName(id: String, name: String) {
        viewModelScope.launch {
            dao.updateName(id, name)
        }
    }
}