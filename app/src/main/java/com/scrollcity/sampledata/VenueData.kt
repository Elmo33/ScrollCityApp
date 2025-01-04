package com.scrollcity.sampledata


import com.scrollcity.R

/**
 * Data class representing a venue.
 */
data class Venue(
    val name: String,
    val distance: String,
    val description: String,
    val peopleCount: String,
    val costIndicator: String,
    val videoResId: Int
)

/**
 * Provide a list of sample venues.
 */
fun provideSampleVenues(): List<Venue> {
    return listOf(
        Venue(
            "Joe mama carting",
            "12 kms away",
            "Joe mama loves carting.",
            "5 People",
            "$$",
            R.raw.testvid1
        ),
        Venue(
            "Adventure Park",
            "5 kms away",
            "An outdoor park perfect for thrill-seekers!",
            "4+ People",
            "Free",
            R.raw.testvid2
        ),
        Venue(
            "Jazz Club",
            "8 kms away",
            "Enjoy live jazz with cocktails in this club.",
            "3 People",
            "$$$",
            R.raw.testvid3
        ),
        Venue(
            "Sunset Cafe",
            "3 kms away",
            "The best place for morning coffee with a view.",
            "2 People",
            "$",
            R.raw.testvid4
        )
    )
}
