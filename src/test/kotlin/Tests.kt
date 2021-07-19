import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class Tests {

    @Test
    fun test() {

        val notionLastUpdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse("2021-07-18T16:19:44Z")
        val notionLastUpdate2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-07-18T16:19:44Z")
        val notionLastUpdate3 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply { timeZone = TimeZone.getTimeZone("GMT") }.parse("2021-07-18T16:19:44Z")

        println("A: ${SimpleDateFormat("HH:mm:ss").format(notionLastUpdate)}")
        println("B: ${SimpleDateFormat("HH:mm:ss").format(notionLastUpdate2)}")
        println("B: ${SimpleDateFormat("HH:mm:ss").format(notionLastUpdate3)}")

    }

}
