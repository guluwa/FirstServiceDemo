package cn.guluwa.firstservicedemo

import cn.guluwa.firstservicedemo.utils.UkUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

//    var b = function (a, b) {
//        for (var d = 0; d < b.length - 2; d += 3) {
//        var c = b.charAt(d + 2),
//        c = "a" <= c ? c.charCodeAt(0) - 87 : Number(c),
//        c = "+" == b.charAt(d + 1) ? a >>> c : a << c;
//        a = "+" == b.charAt(d) ? a + c & 4294967295 : a ^ c
//    }
//        return a
//    }

//    fun ab(a: String, b: String) {
//        for (i in 0 until b.length - 2 step 3) {
//            var c = b[i + 2]
//            c = if ('a' <= c) '0' - 87 else '1'
//            c= if ('+'==b[i+1]) a c else a<<c
//
//        }
//    }
//
//    fun tk(a: String, tkk: String) {
//
//    }

    @Test
    fun addition_isCorrect() {
//        println(UkUtils.ab("422462880", "+-a^+6"))
        println(UkUtils.tk("中国国家主席习近平在十九大报告中明确提出了“实施共赢开发战略”的发展方针", "424305.783424377"))
    }
}
