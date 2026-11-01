package lt.gencevicius.cv.data

import androidx.annotation.StringRes
import lt.gencevicius.cv.R

/**
 * Built-in CV and portfolio content. Texts come from string resources so they follow the
 * selected language; only the entries the user adds are stored in the database.
 */
data class BuiltInCvEntry(
    val section: CvSection,
    @StringRes val title: Int,
    @StringRes val organization: Int?,
    @StringRes val period: Int?,
    @StringRes val description: Int,
)

data class BuiltInPortfolioItem(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val imageUrl: String,
    val link: String,
)

object DefaultContent {
    val cv = listOf(
        BuiltInCvEntry(CvSection.EXPERIENCE, R.string.cv_exp1_title, R.string.cv_exp1_org, R.string.cv_exp1_period, R.string.cv_exp1_desc),
        BuiltInCvEntry(CvSection.EXPERIENCE, R.string.cv_exp2_title, R.string.cv_exp2_org, null, R.string.cv_exp2_desc),
        BuiltInCvEntry(CvSection.EXPERIENCE, R.string.cv_exp3_title, R.string.cv_exp3_org, R.string.cv_exp3_period, R.string.cv_exp3_desc),
        BuiltInCvEntry(CvSection.EDUCATION, R.string.cv_edu1_title, R.string.cv_edu1_org, R.string.cv_edu1_period, R.string.cv_edu1_desc),
        BuiltInCvEntry(CvSection.SKILLS, R.string.cv_skill1_title, null, null, R.string.cv_skill1_desc),
        BuiltInCvEntry(CvSection.SKILLS, R.string.cv_skill2_title, null, null, R.string.cv_skill2_desc),
        BuiltInCvEntry(CvSection.SKILLS, R.string.cv_skill3_title, null, null, R.string.cv_skill3_desc),
        BuiltInCvEntry(CvSection.SKILLS, R.string.cv_skill4_title, null, null, R.string.cv_skill4_desc),
        BuiltInCvEntry(CvSection.SKILLS, R.string.cv_skill5_title, null, null, R.string.cv_skill5_desc),
    )

    // Publicly hosted images, downloaded at runtime by Coil. These are placeholder photos:
    // replace them with your own renders (e.g. the image URLs of your ArtStation projects).
    val portfolio = listOf(
        BuiltInPortfolioItem(
            R.string.portfolio_3d_title,
            R.string.portfolio_3d_desc,
            imageUrl = "https://picsum.photos/seed/umbravulpes3d/800/450",
            link = "https://www.artstation.com/umbravulpes",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_echelon_title,
            R.string.portfolio_echelon_desc,
            imageUrl = "https://picsum.photos/seed/echelon2134/800/450",
            link = "",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_vireka_title,
            R.string.portfolio_vireka_desc,
            imageUrl = "https://picsum.photos/seed/vireka/800/450",
            link = "",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_terminai_title,
            R.string.portfolio_terminai_desc,
            imageUrl = "https://picsum.photos/seed/terminudetektyvas/800/450",
            link = "",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_carbs_title,
            R.string.portfolio_carbs_desc,
            imageUrl = "https://picsum.photos/seed/carbcalculator/800/450",
            link = "",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_glucoface_title,
            R.string.portfolio_glucoface_desc,
            imageUrl = "https://picsum.photos/seed/glucoface/800/450",
            link = "https://github.com/G3RWW/DiabetsWatchface",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_cvcard_title,
            R.string.portfolio_cvcard_desc,
            imageUrl = "https://picsum.photos/seed/cvcard/800/450",
            link = "https://github.com/G3RWW/Cv_app",
        ),
    )
}
