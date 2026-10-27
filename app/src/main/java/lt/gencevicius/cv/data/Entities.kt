package lt.gencevicius.cv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CvSection { EXPERIENCE, EDUCATION, SKILLS }

/** A CV entry. Entries added by the user are stored in Room; built-in ones have id < 0. */
@Entity(tableName = "cv_entries")
data class CvEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val section: CvSection,
    val title: String,
    val organization: String,
    val period: String,
    val description: String,
) {
    val isUserAdded: Boolean get() = id > 0
}

/** A portfolio item; the image is loaded from [imageUrl] at runtime, never bundled in the app. */
@Entity(tableName = "portfolio_items")
data class PortfolioItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val imageUrl: String,
    val link: String,
) {
    val isUserAdded: Boolean get() = id > 0
}
