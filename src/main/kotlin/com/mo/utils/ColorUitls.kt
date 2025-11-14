// 建议的导入路径（基于 Minecraft Java Edition 1.14+）
// import net.minecraft.util.FastColor.ARGB

/**
 * 颜色工具类，用于创建和处理 32 位 ARGB 颜色整数值。
 * 颜色值范围为 0-255。
 */
object ColorUtils {

    /**
     * 方式一：传入 A, R, G, B 四个分量，返回一个完整的 ARGB 整数。
     *
     * 格式：(A << 24) | (R << 16) | (G << 8) | B
     *
     * @param alpha Alpha（透明度）分量 (0-255)，0 为完全透明，255 为完全不透明。
     * @param red 红色分量 (0-255)。
     * @param green 绿色分量 (0-255)。
     * @param blue 蓝色分量 (0-255)。
     * @return 32 位整数 ARGB 颜色值。
     */
    @JvmStatic
    fun color(alpha: Int, red: Int, green: Int, blue: Int): Int {
        // 使用位运算将四个 8 位分量组合成一个 32 位整数
        // 使用 & 0xFF 确保每个分量只有 8 位有效数据，防止负数或其他溢出问题
        return (alpha and 0xFF shl 24) or
                (red and 0xFF shl 16) or
                (green and 0xFF shl 8) or
                (blue and 0xFF)
    }

    /**
     * 方式二：只传入 R, G, B 三个分量，默认 Alpha 值为 255 (完全不透明)，返回一个 ARGB 整数。
     *
     * @param red 红色分量 (0-255)。
     * @param green 绿色分量 (0-255)。
     * @param blue 蓝色分量 (0-255)。
     * @return 32 位整数 ARGB 颜色值 (A=255)。
     */
    @JvmStatic
    fun color(red: Int, green: Int, blue: Int): Int {
        val defaultAlpha = 255 // 默认完全不透明
        return color(defaultAlpha, red, green, blue)
    }

    // 额外的辅助方法：从 ARGB 整数中提取分量

    /** 提取 Alpha 分量 (0-255) */
    @JvmStatic
    fun getAlpha(color: Int): Int = (color shr 24) and 0xFF

    /** 提取 Red 分量 (0-255) */
    @JvmStatic
    fun getRed(color: Int): Int = (color shr 16) and 0xFF

    /** 提取 Green 分量 (0-255) */
    @JvmStatic
    fun getGreen(color: Int): Int = (color shr 8) and 0xFF

    /** 提取 Blue 分量 (0-255) */
    @JvmStatic
    fun getBlue(color: Int): Int = color and 0xFF
}