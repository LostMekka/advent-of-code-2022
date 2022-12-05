package util

class Matrix3(
    val aa: Int, val ba: Int, val ca: Int,
    val ab: Int, val bb: Int, val cb: Int,
    val ac: Int, val bc: Int, val cc: Int,
) {
    //    other aa ba ca
    //          ab bb cb
    // this     ac bc cc
    // aa ba ca __ __ __
    // ab bb cb __ __ __
    // ac bc cc __ __ __
    operator fun times(other: Matrix3) =
        Matrix3(
            aa * other.aa + ba * other.ab + ca * other.ac,
            aa * other.ba + ba * other.bb + ca * other.bc,
            aa * other.ca + ba * other.cb + ca * other.cc,

            ab * other.aa + bb * other.ab + cb * other.ac,
            ab * other.ba + bb * other.bb + cb * other.bc,
            ab * other.ca + bb * other.cb + cb * other.cc,

            ac * other.aa + bc * other.ab + cc * other.ac,
            ac * other.ba + bc * other.bb + cc * other.bc,
            ac * other.ca + bc * other.cb + cc * other.cc,
        )

    //    other x
    //          y
    // this     z
    // aa ba ca _
    // ab bb cb _
    // ac bc cc _
    operator fun times(other: Point3) =
        Point3(
            aa * other.x + ba * other.y + ca * other.z,
            ab * other.x + bb * other.y + cb * other.z,
            ac * other.x + bc * other.y + cc * other.z,
        )

    companion object {
        val Identity =
            Matrix3(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1,
            )
        val Rotate90DegreesAroundX =
            Matrix3(
                1, 0, 0,
                0, 0, -1,
                0, 1, 0,
            )
        val Rotate90DegreesAroundY =
            Matrix3(
                0, 0, 1,
                0, 1, 0,
                -1, 0, 0,
            )
        val Rotate90DegreesAroundZ =
            Matrix3(
                0, -1, 0,
                1, 0, 0,
                0, 0, 1,
            )
    }
}
