package com.mo.utils

object AnimationUtil {

    fun easeOutQuad(x:Float): Float {
        return 1 - (1 - x) * (1 - x);
    }


    fun easeOutQuadReserve(x: Float): Float {
        return (1 - x) * (1 - x);
    }

}