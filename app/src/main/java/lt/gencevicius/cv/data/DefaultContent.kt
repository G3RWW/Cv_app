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

    // Publicly hosted images, downloaded at runtime by Coil. Projects without their own
    // image yet use placeholder photos (picsum.photos) until a real render is uploaded.
    val portfolio = listOf(
        BuiltInPortfolioItem(
            R.string.portfolio_3d_title,
            R.string.portfolio_3d_desc,
            imageUrl = "https://cdna.artstation.com/p/users/avatars/013/690/158/large/a282ce659854528f5298239ad5f5f8df.jpg?1764067399",
            link = "https://www.artstation.com/umbravulpes",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_vocaloid_title,
            R.string.portfolio_vocaloid_desc,
            imageUrl = "https://cdnb.artstation.com/p/assets/images/images/099/454/087/large/umbravulpes-screenshot-2026-05-28-192901.webp?1779985912",
            link = "https://www.youtube.com/watch?v=saCOMcwuRCA",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_luna_title,
            R.string.portfolio_luna_desc,
            imageUrl = "https://cdnb.artstation.com/p/assets/images/images/099/450/893/large/umbravulpes-luna-hero2.webp?1779978746",
            link = "https://www.artstation.com/umbravulpes",
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
            R.string.portfolio_piston_title,
            R.string.portfolio_piston_desc,
            imageUrl = "https://opengraph.githubassets.com/1/G3RWW/PistonLauncher",
            link = "https://github.com/G3RWW/PistonLauncher",
        ),
        BuiltInPortfolioItem(
            R.string.portfolio_cvcard_title,
            R.string.portfolio_cvcard_desc,
            imageUrl = "https://picsum.photos/seed/cvcard/800/450",
            link = "https://github.com/G3RWW/Cv_app",
        ),
    )
}
