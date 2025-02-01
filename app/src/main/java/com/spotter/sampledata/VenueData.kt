package com.spotter.sampledata

import com.spotter.R

/**
 * Data class representing a venue.
 */
data class Venue(
    val id: Int, // Unique ID for navigation purposes
    val name: String,
    val distance: String,
    val description: String,
    val peopleCount: String,
    val costIndicator: String,
    val contentResIds: List<Int>, // List of resource IDs for videos/images
    val amenities: List<String> = listOf(), // Optional amenities list
    val reviews: List<Review> = listOf(), // Optional user reviews
    val address: String, // New property for venue address
    val phoneNum: String // New property for venue phone number
)

/**
 * Data class representing a user review.
 */
data class Review(
    val username: String,
    val comment: String,
    val rating: Int // Rating out of 5
)

/**
 * Provide a list of sample venues with mixed content (videos and images).
 */
fun provideSampleVenues(): List<Venue> {
    return listOf(
        Venue(
            id = 1,
            name = "Joe Mama Karting",
            distance = "12 km away",
            description = "Feel the adrenaline rush as you race through the best go-karting track in the city!",
            peopleCount = "Suitable for 5+ people",
            costIndicator = "$$",
            contentResIds = listOf(
                R.raw.testvid1, // Video showcasing the race track
                R.drawable.sample_image1  // Aerial shot of the venue
            ),
            amenities = listOf("Free Parking", "On-site Cafe", "Beginner Friendly"),
            reviews = listOf(
                Review("Alex", "Loved the fast karts and friendly staff!", 5),
                Review("Mia", "A bit pricey, but the track is excellent!", 4)
            ),
            address = "123 Race Track Lane, Speedyville",
            phoneNum = "+995557557775",

        ),
        Venue(
            id = 2,
            name = "Adventure Park",
            distance = "5 km away",
            description = "A perfect outdoor park with zip lines, climbing walls, and more for thrill-seekers!",
            peopleCount = "Ideal for groups of 4+",
            costIndicator = "Free",
            contentResIds = listOf(
                R.raw.testvid2, // Video of someone on the zip line
                R.drawable.adventure_image1 // A beautiful image of the trail
            ),
            amenities = listOf("Pet-Friendly", "Picnic Areas", "Restrooms"),
            reviews = listOf(
                Review("Chris", "Great place for the family!", 5),
                Review("Sam", "Some activities were closed, but still fun.", 3)
            ),
            address = "456 Adventure Road, Thrilltown",
            phoneNum = "+995557557775",
        ),
        Venue(
            id = 3,
            name = "Jazz Club",
            distance = "8 km away",
            description = "Immerse yourself in live jazz music while enjoying cocktails in a cozy setting.",
            peopleCount = "Best for 2-3 people",
            costIndicator = "$$$",
            contentResIds = listOf(
                R.raw.testvid3, // Video of the band performing live
                R.drawable.jazzclub_image1, // Interior shot with ambient lighting
                R.drawable.jazzclub_image2  // Close-up of cocktails and snacks
            ),
            amenities = listOf("Live Music", "Cocktail Bar", "Reservations Recommended"),
            reviews = listOf(
                Review("Emily", "Fantastic music and ambiance!", 5),
                Review("Liam", "Drinks were expensive but worth it.", 4)
            ),
            address = "789 Jazz Street, Melodyville",
            phoneNum = "+995557557775",
        ),
        Venue(
            id = 4,
            name = "Sunset Cafe",
            distance = "3 km away",
            description = "Relax with a morning coffee or evening tea while enjoying stunning sunset views.",
            peopleCount = "Cozy for 2 people",
            costIndicator = "$",
            contentResIds = listOf(
                R.raw.testvid4,  // Video showing the sunset over the cafe
                R.drawable.sunset_image1 // Outdoor seating photo
            ),
            amenities = listOf("Wi-Fi", "Outdoor Seating", "Vegetarian Options"),
            reviews = listOf(
                Review("Noah", "Great coffee and view!", 5),
                Review("Sophia", "Service was slow, but the sunset made up for it.", 3)
            ),
            address = "321 Sunset Blvd, Viewtown",
            phoneNum = "+995557557775",
        )
    )
}
