package hu.a1sttech.headword
data class OnboardingItem(
    val title: String,      // A súgó oldal címe
    val description: String, // A súgó oldal leírása
    val image: Int          // Kép erőforrás azonosítója (R.drawable.xxx)
)