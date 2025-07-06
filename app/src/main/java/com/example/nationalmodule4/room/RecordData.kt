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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

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

    @Query("SELECT * FROM records ORDER BY date DESC")
    fun getAll(): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE name LIKE :search ORDER BY date DESC")
    fun getAllLatest(search: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE name LIKE :search ORDER BY date ASC")
    fun getAllOldest(search: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id LIMIT 1")
    suspend fun get(id: String): Record

    @Insert
    suspend fun add(record: Record)

    @Query("UPDATE records SET name = :name WHERE id = :id")
    suspend fun updateName(id: String, name: String)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteRecord(id: String)
}

@Database(entities = [Record::class], version = 2)
abstract class StudyFlowDataBase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}

fun getDataBase(context: Context): StudyFlowDataBase {
    return Room.databaseBuilder(context, StudyFlowDataBase::class.java, "study_flow_db")
        .fallbackToDestructiveMigration().build()
}

class RecordDataModal(private val dao: RecordDao) : ViewModel() {
    val data = dao.getAll()

    init {
        viewModelScope.launch {
            dao.comeAlive()
        }
    }

    fun get(search: String, latest: Boolean): Flow<List<Record>> {
        if (latest) {
            return dao.getAllLatest(search)
        }
        return dao.getAllOldest(search)

    }

    fun getById(id: String): Flow<Record?> {
        return kotlinx.coroutines.flow.flow {
            if (id.isNotEmpty()) {
                try {
                    val record = dao.get(id)
                    emit(record)
                } catch (e: Exception) {
                    emit(null)
                }
            } else {
                emit(null)
            }
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

    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            dao.deleteRecord(record.id)
            File(record.path).delete()
        }
    }

}