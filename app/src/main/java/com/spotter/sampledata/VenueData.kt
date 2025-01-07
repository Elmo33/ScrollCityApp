package com.spotter.sampledata

import com.spotter.R

/**
 * Data class representing a venue.
 */
data class Venue(
    val name: String,
    val distance: String,
    val description: String,
    val peopleCount: String,
    val costIndicator: String,
    val contentResIds: List<Int> // List of resource IDs for videos/images
)

/**
 * Provide a list of sample venues with mixed content (videos and images).
 */
fun provideSampleVenues(): List<Venue> {
    return listOf(
        Venue(
            name = "Joe mama carting",
            distance = "12 kms away",
            description = "Joe mama loves carting.",
            peopleCount = "5 People",
            costIndicator = "$$",
            contentResIds = listOf(
                R.raw.testvid1, // Video
                R.drawable.image1, // Image
                R.drawable.sample_image1  // Image
            )
        ),
        Venue(
            name = "Adventure Park",
            distance = "5 kms away",
            description = "An outdoor park perfect for thrill-seekers!",
            peopleCount = "4+ People",
            costIndicator = "Free",
            contentResIds = listOf(
                R.raw.testvid2, // Video
                R.drawable.adventure_image1, // Image
            )
        ),
        Venue(
            name = "Jazz Club",
            distance = "8 kms away",
            description = "Enjoy live jazz with cocktails in this club.",
            peopleCount = "3 People",
            costIndicator = "$$$",
            contentResIds = listOf(
                R.raw.testvid3, // Video
                R.drawable.jazzclub_image1, // Image
                R.drawable.jazzclub_image2  // Image
            )
        ),
        Venue(
            name = "Sunset Cafe",
            distance = "3 kms away",
            description = "The best place for morning coffee with a view.",
            peopleCount = "2 People",
            costIndicator = "$",
            contentResIds = listOf(
                R.raw.testvid4,  // Video
                R.drawable.sunset_image1, // Image
            )
        )
    )
}
