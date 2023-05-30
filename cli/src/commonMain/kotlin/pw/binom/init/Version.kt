package pw.binom.init

data class Version(val family: String, val version: String) {
    val propertyName
        get() = "${family.lowercase()}.version"
    val constName
        get() = "${family.uppercase()}_VERSION"
    val variableName
        get() = "${family.lowercase()}Version"
}
