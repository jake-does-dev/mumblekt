import dev.jakedoes.sum
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ArithmeticTest {
    @Test
    fun simpleTest() {
        assertEquals(5, sum(2, 3))
    }
}