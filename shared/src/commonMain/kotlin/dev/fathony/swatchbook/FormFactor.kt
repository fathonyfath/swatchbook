package dev.fathony.swatchbook

/**
 * The kind of device the app is running on, which the window size alone cannot tell: a tablet
 * stays a tablet in a small window. Each platform works it out for itself.
 */
enum class FormFactor {
    Phone,
    Tablet,
    Desktop,
}
