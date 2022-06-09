package io.github.ppdzm.utils.spark.udf

/**
 * Created by Stuart Alex on 2017/5/26.
 */
object RegisterUDF {

    //  /**
    //    * 将汉语转换为不带声调的拼音，使用v代表ü
    //    *
    //    * @return String
    //    */
    //  def mandarin2Phoneticization(hanyu: String): String = {
    //    val defaultFormat = new HanyuPinyinOutputFormat()
    //    defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE)
    //    defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE)
    //    defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V)
    //    hanyu.map {
    //      case ch if (ch >= 48 && ch <= 57) || (ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122) => ch.toString
    //      case ch if ch == '、' || ch == '。' || ch == '（' || ch == '）' => ""
    //      case ch if ch > 128 => PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat)(0)
    //      case _ => ""
    //    }.mkString
    //  }

    /**
     * 将数值表达的日期yyyyMMdd转换为yyyy-MM-dd格式
     *
     * @param date yyyyMMdd
     * @return
     */
    def castIntToFormattedDate(date: Int): String = {
        val chars = date.toString.toCharArray
        chars.slice(0, 4).mkString + "-" + chars.slice(4, 6) + "-" + chars.slice(6, 8)
    }

}