package lt.gencevicius.cv.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Local SQLite database (Room) holding the CV entries and portfolio items the user adds. */
@Database(entities = [CvEntry::class, PortfolioItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cvDao(): CvDao
    abstract fun portfolioDao(): PortfolioDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "cv.db").build()
    }
}
