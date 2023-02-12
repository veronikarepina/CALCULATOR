package com.example.calculator

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.MathContext

class CalcViewModel: ViewModel(){
    val answer = MutableLiveData<String>()
    private var firstText = ""
    private var secondText = ""
    private val actions = listOf("+", "-", "*", "/", "=")
    private var lastButton = ""
    private var lastAction = ""
    private var lastDigit = ""

    init {
        answer.value = "0"
    }

//    fun deleteButtonC(){
//        binding.deleteButton.text = "C"
//    }
//    fun deleteButtonAC(){
//        binding.deleteButton.text = "AC"
//    }
    fun numberSplit(text: String): String{ //функция разбивает число на сотни (1000 -> 1 000)
        var result = text
        val textList: MutableList<Char> = result.toMutableList() //создание изменяемого списка
        val space = ' '
        for (i in 0 until textList.size - 1){ //цикл удаляет из строки уже имеющиеся пробелы
            textList.remove(space)
        }
        val countThree: Int = textList.size / 3 // считаем число сотенных разрядов
        var index: Int
        for (i in 1..countThree){ // цикл вставляет пробел перед каждой сотней
            index = textList.size - 3 * (countThree - (i - 1))
            textList.add(index, space)
        }
        if (textList[0] == ' ') //если образовался пробел в начале строки, удаляем его
            textList.remove(textList[0])
        if(textList[0] == '-' && textList[1] == ' ')
            textList.remove(textList[1])
        val separator = ""
        result = textList.joinToString(separator) //преобразуем список в строку
        return result
    }
    fun textToShow(text: String):String{ //функция преобразовывает число в нужный формат для вывода. Это число для отображения
        var result = text
        if ("." in result || "," in result){
            val textList: MutableList<Char> = result.toMutableList()
            if ("." in result){
                val indexComma = textList.indexOf('.')
                Log.d("MyLog", "indexComma $indexComma")
                when(indexComma == -1){
                    true -> {}
                    false -> textList[indexComma] = ','
                }
            }
            val indexPoint = textList.indexOf(',')
            val firstList = textList.slice(0.. indexPoint - 1)
            val secondList = textList.slice(indexPoint + 1 .. textList.size - 1)
            Log.d("MyLog", "Before $textList")
            Log.d("MyLog", "First list $firstList")
            Log.d("MyLog", "Second list $secondList")
            val separator = ""
            val firstString = firstList.joinToString(separator)
            val secondString = secondList.joinToString(separator)
            result = numberSplit(firstString) + "," + secondString
            Log.d("MyLog", "First list $result")
        }
        else if (text.length > 3){ //выполняем преобразования в случае, если число состоит более чем из 3 цифр
            result = numberSplit(result)
        }
        else
        {
            result = text
        }
        return result
    }
    fun textToCount(text: String): String{ //функция удаляет все пробелы. Число для подсчетов
        val textList: MutableList<Char> = text.toMutableList()
        Log.d("MyLog", "textList before $textList")
        val space = ' '
        for (i in 0 .. textList.size - 1){
            textList.remove(space)
        }
        Log.d("MyLog", "textList after $textList")
        if ("," in text){                               //меняем запятую на точку для подсчетов
            val indexComma = textList.indexOf(',')
            textList[indexComma] = '.'
            Log.d("MyLog", "index comma $indexComma")
        }
        val separator = ""
        return textList.joinToString(separator)
    }
    fun addText(buttonNumber: String){ //функция добавления нажатой цифры
        var textNow = answer.value
        if (lastButton in actions && textNow != "0" && textNow != "-0") { //если последняя нажатая кнопка была оператором
            if (textNow == "Ошибка") lastAction = ""                      //и не была нажата кнопка смены знака
            textNow = buttonNumber   //то начинаем писать новое число
        }
        else {
            when(textNow == "0" || textNow == "-0"){
                true -> {
                    when(textNow == "0"){
                        true -> {
                            //deleteButtonC()
                            textNow = buttonNumber
                        }
                        false -> {
                            //deleteButtonC()
                            textNow = (-(buttonNumber.toInt())).toString()
                        }
                    }
                }
                false -> textNow = textNow + buttonNumber
            }
        }
        answer.value = textToShow(textNow)
        lastButton = buttonNumber
        Log.d("MyLog", "lastButton in numbers:$lastButton")
    }
    fun addPoint(fraction: String){
        var textNow = answer.value.toString()
        if ("," in textNow){
            answer.value = textToShow(textNow)
        }
        else {
            textNow = textNow + fraction
            answer.value = textToShow(textNow)
        }
    }
    fun delete(){
        //deleteButtonAC()
        firstText = ""
        secondText = ""
        lastButton = ""
        lastAction = ""
        lastDigit = ""
        answer.value = "0"
    }
    fun textLong(text: Double): String{
        val resultLong = text.toLong()
        var resultText = ""
        Log.d("MyLog", "resultLong $resultLong")
        if(text == resultLong.toDouble()){
            resultText = resultLong.toString()
            Log.d("MyLog", "resultText in true $resultText")
        }
        else{
            resultText = text.toString()
            Log.d("MyLog", "resultText in false $resultText")
        }
        return resultText
    }
    fun calculation(text: String): String{
        var resultText = ""
        try{
            val expression = ExpressionBuilder(text).build()
            val result = expression.evaluate()
            Log.d("MyLog", "result $result")
            resultText = textLong(result)
        }
        catch (e:Exception) {
            answer.value = "Ошибка"
            Log.d("Ошибка", "${e.message}")
        }
        return resultText
    }
    fun countResult(firstOperand: String, secondOperand: String, operator: String): String{ //функция вычисляет
        var resultText = ""                                                                 //результат операции
        if (operator == "/" && secondOperand == "0"){
            Log.d("MyLog", "operator $operator")
            Log.d("MyLog", "secondOperand $secondOperand")
            var str = "Error" // REPLACE
            Log.d("MyLog", "str: $str")
            resultText = str
        }
        else {
            val fullText = firstOperand + operator + secondOperand
            Log.d("MyLog", "fullText $fullText")
            resultText = calculation(fullText)
        }
        return resultText
    }
    fun actionTo(operator: String){
        val textNow = answer.value.toString()
        if (firstText == "" && (textNow == "0" || textNow == "-0") && lastDigit != "" && lastAction == "=") { // если первый операнд пуст и в рабочее поле ничего не было введено
            answer.value == textNow                      //то оставляем как есть
        }
        else {
            when(firstText == ""){ //первый операнд пуст
                true -> {
                    firstText = textToCount(textNow) //в первый операнд записываем текущее число
                    Log.d("MyLog", "firstText:$firstText")             //с рабочего поля
                    lastAction = operator       //фиксируем нажатый оператор в памяти
                    lastButton = operator
                    Log.d("MyLog", "lastAction with space:$lastAction")
                }
                false -> {  //первый операнд не пуст
                    if (lastButton in actions){ //если предыдущая нажатая кнопка была также каким-либо оператором
                        Log.d("MyLog", "last button before:$lastButton")
                        lastAction = operator   //переопределяем оператор
                        lastButton = operator   //переопределяем последнюю нажатую кнопку
                        Log.d("MyLog", "last action after:$lastAction")
                        answer.value = textNow //в рабочем поле ничего не меняется
                    }
                    else { //иначе, предыдущая кнопка - не оператор
                        Log.d("MyLog", "lastAction until count:$lastAction")
                        secondText = textToCount(textNow) //фиксируем второй операнд
                        firstText = countResult(firstText, secondText, lastAction)   //в первый операнд записываем результат вычисления
                        if (firstText == "Error"){
                            answer.value = firstText
                            firstText = ""
                            lastButton = "="
                        }
                        else{
                            val dec = BigDecimal(firstText, MathContext.DECIMAL32)
                            Log.d("MyLog", "dec $dec")
                            answer.value = textToShow(dec.toString())  //выводим на экран этот результат
                            lastAction = operator //переопределяем оператор
                            lastButton = operator //переопределяем последнюю нажатую кнопку
                            Log.d("MyLog", "lastAction after count:$lastAction")
                        }
                        secondText = "" //очищаем второй операнд
                    }
                }
            }
        }
    }
    fun countPercent(){
        var textNow = answer.value.toString()
        var textCount = 0.0
        when(textNow == "Error"){
            true -> {} //если в рабочем поле "ошибка/error" - ничего не делаем
            false -> {  //иначе
                when(firstText == ""){
                    true -> { //если первый операнд пуст
                        if (textNow == "0" || textNow == "-0"){} // и в рабочем поле находится 0 или -0 - ничего не делаем
                        else {
                            textCount = textToCount(textNow).toDouble() / 100
                            Log.d("MyLog", "textResult $textCount")
                        }
                    }
                    false -> {
                        if(lastAction == "-" || lastAction == "+"){
                            textCount = textToCount(firstText).toDouble() / 100 * textToCount(textNow).toDouble()
                        }
                        else{
                            textCount = textToCount(textNow).toDouble() / 100
                            Log.d("MyLog", "textResult $textCount")
                        }
                    }
                }
                answer.value = textToShow(textLong(textCount))
            }
        }
    }
    fun equal(){
        Log.d("MyLog", "LB: $lastButton")
        val textNow = answer.value.toString()
        when(textNow == "Error"){
            true -> {}
            false -> {
                if(lastAction == ""){ //если не была нажата кнопка оператора
                    answer.value = textNow //оставляем в поле все как есть
                }
                else if (lastButton == "=" && lastDigit != ""){
                    firstText = textToCount(textNow)
                    when(lastButton in actions && lastButton != "="){
                        true -> secondText = firstText
                        false -> secondText = lastDigit
                    }
                    val result = countResult(firstText, secondText, lastAction)
                    val decResult = BigDecimal(result, MathContext.DECIMAL32)
                    Log.d("MyLog", "dec $decResult")
                    answer.value = textToShow(decResult.toString())
                    firstText = ""
                    secondText = ""
                    lastButton = "="
                    Log.d("MyLog", "im here")
                }
                else{
                    secondText = textToCount(textNow) //фиксируем второй операнд
                    Log.d("MyLog", "SecondText in result: $secondText")
                    Log.d("MyLog", "firstText in result: $firstText")
                    val result = countResult(firstText, secondText, lastAction)
                    if (result == "Error"){
                        answer.value = result
                    }
                    else {
                        val decResult = BigDecimal(result, MathContext.DECIMAL32)
                        Log.d("MyLog", "dec $decResult")
                        answer.value = textToShow(decResult.toString()) //считаем и выводим на экран результат
                        lastDigit = secondText
                        Log.d("MyLog", "lastDigit $lastDigit")
                        Log.d("MyLog", "lastAction $lastAction")
                    }
                    firstText = ""
                    secondText = ""
                    lastButton = "="
                    Log.d("MyLog", "No, im here")
                }
            }
        }
    }
    fun signChange(){
        var textNow = answer.value.toString()
        when(textNow == "Error"){
            true -> {answer.value = "-0"}
            false -> {
                if (firstText == "" && textNow == "0" || textNow == "-0") { // если первый операнд пуст и в рабочее поле ничего не было введено
                    when(textNow == "0"){                                 // то меняем знак нуля, ожидая ввода числа
                        true -> textNow = "-0"
                        false -> textNow = "0"
                    }
                }
                else {
                    when (firstText == ""){
                        true -> textNow = (-(textToCount(textNow).toInt())).toString()
                        false -> {
                            when(lastButton in actions){
                                true -> {
                                    if (textNow == "0" || textNow == "-0"){
                                        when(textNow == "0"){
                                            true -> textNow = "-0"
                                            false -> textNow = "0"
                                        }
                                    }
                                    else textNow = "-0"
                                }
                                false -> textNow = (-(textToCount(textNow).toInt())).toString()
                            }
                        }
                    }
                }
                answer.value = textToShow(textNow)
            }
        }
    }
}