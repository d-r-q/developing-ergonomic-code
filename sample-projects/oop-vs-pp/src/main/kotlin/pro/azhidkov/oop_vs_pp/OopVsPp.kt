package pro.azhidkov.oop_vs_pp

val f = Flag(true)
val x = NonNegativePoint1D(1U)
val p = Point1D(NonNegativePoint1D(1U), Flag(true))
val np = NamedPoint1D(p, "name")

data class Flag(var enabled: Boolean)

data class NonNegativePoint1D(var x: UByte)

data class Point1D(
    var x: NonNegativePoint1D,
    var positive: Flag
)

data class NamedPoint1D(
    var p: Point1D,
    var name: String
)
