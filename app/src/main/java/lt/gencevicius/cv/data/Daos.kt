package lt.gencevicius.cv.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CvDao {
    @Query("SELECT * FROM cv_entries ORDER BY id")
    fun observeAll(): Flow<List<CvEntry>>

    @Insert
    suspend fun insert(entry: CvEntry)

    @Delete
    suspend fun delete(entry: CvEntry)
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_items ORDER BY id")
    fun observeAll(): Flow<List<PortfolioItem>>

    @Insert
    suspend fun insert(item: PortfolioItem)

    @Delete
    suspend fun delete(item: PortfolioItem)
}
