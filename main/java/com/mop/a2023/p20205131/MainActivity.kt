package com.mop.a2023.p20205131

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import com.mop.a2023.p20205131.databinding.ActivityMainBinding
import java.util.EmptyStackException
import java.util.Stack
import kotlin.math.sqrt
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private var customFunctionString:String = "x"  // 사용자 지정함수가 저장될 멤버 변수
    private var resultTextView:TextView? = null    // 계산기 수식 & 결과 보여줄 text View를 저장할 멤버변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바인딩 처리
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 사용자정의함수 편집창에서 돌려받은 인텐트를 처리하기 위한 requestLauncher
        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult( // 퍈집창으로 부터 받아와야 하기 때문에 registerForActivityResult로 등록
            ActivityResultContracts.StartActivityForResult()){
            val customFunctionString = it.data?.getStringExtra("customFunctionString") // customFunctionString 키로 편집창에서 수식을 받아옴
            this.customFunctionString = (customFunctionString?:"x").toString() // 수식이 만약 없다면, 기본 수식 (f(x) = x)를 저장하고, 아니면 받아온 값을 멤버변수에 저장
            Log.d("MainActivity","사용자정의함수 불러옴 : ${this.customFunctionString}")
        }

        // func(사용자정의함수) 버튼에 대한 처리
        var function1Buttontimer = 0L // 버튼 시간 측정 변수

        // 길게 누를 경우 사용자정의함수 편집 화면으로, 짧게 누를 경우 사용자 정의함수로 계산
        binding.customFunctionButton?.setOnTouchListener { _, event ->
            when(event?.action){

                // 눌렸을 경우, 시간 체크
                MotionEvent.ACTION_DOWN -> {
                    function1Buttontimer = System.currentTimeMillis()
                    false // 추가 이벤트 처리 안함
                }

                // 버튼에서 손가락 똈을 경우
                MotionEvent.ACTION_UP -> {
                    val duration = System.currentTimeMillis() - function1Buttontimer
                    if(duration >= 1000) { // 1초 이상 눌렀을 때 사용자 지정함수 편집창으로 이동
                        Log.d("MainActivity", "사용자정의함수 편집")

                        val intent: Intent = Intent(this, CustomFunctionActivity::class.java) // 편집창 인텐트 생성
                        intent.putExtra("customFunctionString",customFunctionString) // customFunctionStriong 키로 현재 저장된 사용자지정함수 값을 전달

                        requestLauncher.launch(intent) // 인텐트 Launch
                    } else { // 1초 미만으로 눌렀을 경우 사용자 지정함수 즉각 실행
                        binding.resultText.text = "func("+binding.resultText.text.toString()+")"
                        calculateResultText(binding.resultText)
                    }
                    false // 추가 이벤트 처리 안함
                }
            }
            true// 추가 이벤트 처리 넘김
        }

        binding.apply{
            // 키패드 이벤트 리스너 등록하는 코드입니다.

            // 버튼들에게 문자를 텍스트뷰에 넣는 이벤트 리스너를 등록시킵니다.
            // 각 이벤트 리스너는 수식 textview에 각 숫자, 연산자를 집어넣습니다.
            oneButton.setOnClickListener {addCharacterToText("1",resultText)}
            twoButton.setOnClickListener {addCharacterToText("2",resultText)}
            threeButton.setOnClickListener {addCharacterToText("3",resultText)}
            fourButton.setOnClickListener {addCharacterToText("4",resultText)}
            fiveButton.setOnClickListener {addCharacterToText("5",resultText)}
            sixButton.setOnClickListener {addCharacterToText("6",resultText)}
            sevenButton.setOnClickListener {addCharacterToText("7",resultText)}
            eightButton.setOnClickListener {addCharacterToText("8",resultText)}
            nineButton.setOnClickListener {addCharacterToText("9",resultText)}
            zeroButton.setOnClickListener {addCharacterToText("0",resultText)}
            plusButton.setOnClickListener {addCharacterToText("+",resultText)}
            minusButton.setOnClickListener {addCharacterToText("-",resultText)}
            timesButton.setOnClickListener {addCharacterToText("*",resultText)}
            dotButton.setOnClickListener {addCharacterToText(".",resultText)}
            divideButton.setOnClickListener {addCharacterToText("/",resultText)}
            doubleZeroButton.setOnClickListener {addCharacterToText("00",resultText)}
            leftBraceButton.setOnClickListener {addCharacterToText("(",resultText)}
            rightBraceButton.setOnClickListener {addCharacterToText(")",resultText)}
            customFunctionAddButton.setOnClickListener { addCharacterToText("func(",resultText)}
            sqrtButton?.setOnClickListener {addCharacterToText("sqrt(",resultText)} // 가로 모드 전용
            powerButton?.setOnClickListener {addCharacterToText("^",resultText)}    // 가로 모드 전용
            log10Button?.setOnClickListener {addCharacterToText("log(",resultText)} // 가로 모드 전용
            log2Button?.setOnClickListener {addCharacterToText("logb(",resultText)} // 가로 모드 전용

            // 기능 버튼
            deleteButton.setOnClickListener {delCharacterFromText(resultText)}
            clearButton.setOnClickListener {clearText(resultText)}
            resultButton.setOnClickListener {calculateResultText(resultText)}
        }

        this.resultTextView = binding.resultText // view를 멤버변수로 등록
    }

    // 상태 저장
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // resultTextView가 null이거나, 내부 text가 null인경우, 0을 전달
        outState.putString("resultTextString", (this.resultTextView?.text ?: "0").toString())
        outState.putString("function1String",this.customFunctionString)
    }

    // 상태 불러오기
    override fun onRestoreInstanceState(outState: Bundle){
        super.onRestoreInstanceState(outState)
        var resultTextString = outState.getString("resultTextString")?: "0" // 계산기 화면 불러오기
        this.resultTextView?.text = resultTextString
        this.customFunctionString = outState.getString("function1String")?: "x" // 사용자지정함수 불러오기
    }

    // 텍스트뷰에 문자열 추가하는 함수
    private fun addCharacterToText(character:String, textView: TextView) {
        textView.apply{
            var newText: String? = text.toString()
            if(newText == null || newText == "0") { // 문자열에 정보 없는 경우
                if(character == ".") newText = "0."
                else newText = character
            }
            else
                newText += character

            text = newText
        }
    }

    // 텍스트뷰에서 문자열을 하나 삭제하는 함수
    private fun delCharacterFromText(textView: TextView){
        textView.apply{
            var newText: String? = text.toString()
            if(newText != null && newText.lastIndex >= 0){
                var cursor:Int = newText.lastIndex; // 마지막 인덱스

                // 숫자는 한개씩 지우고, 문자는 단어 채로 지우기 (즉 숫자는 한 글자씩 지워지고, 함수나 문자열은 단어 채로 지워집니다.)
                if(!newText[cursor].isDigit()) {
                    while(cursor > 0 && !newText[cursor-1].isDigit())
                        cursor -= 1

                }
                newText = newText.substring(0,cursor)
                if(newText.isEmpty()) newText = "0" // 모두 비면 0으로 세팅
                text = newText
            }

        }
    }

    // 텍스트뷰를 초기화하는 함수
    private fun clearText(textView: TextView) {
        textView.text = "0"
    }

    // 텍스트뷰의 수식을 해석하여 결과를 텍스트뷰로 보여주는 함수
    private fun calculateResultText(textView: TextView) {
        textView.apply {
            if(text != null){
                val resultText = evaluateExpression(text as String) // 텍스트뷰의 수식을 해석한 결과 (숫자)를 받아옵니다.
                if(resultText == "Error") // 오류라면
                    Toast.makeText(context, "에러 발생!!", Toast.LENGTH_SHORT).show() // 토스트 메시지 출력
                else // 오류가 없다면
                    text = resultText // 결과값을 텍스트뷰에 출력합니다.
            }


        }
    }

    // --- 수식 해석 기능 ---

    // 연산자 우선순위 맵
    private val operators = mapOf(
        ")" to 0,
        "(" to 0,
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2,
        "^" to 3,
        "sqrt" to 4,
        "func" to 4,
        "log" to 4,
        "logb" to 4) // 우선순위 맵

    // 실제 수식을 해석하는 함수 - 내부적으로는 중위식을 후위식으로 변환한 후, 연산을 수행합니다.
    private fun evaluateExpression(expression: String): String {
        fun checkOperatorPriority(a:String, b:String): Boolean { // 연산자의 우선순위 비교 함수
            return (operators[a] ?: -1) >= (operators[b] ?: -1)
        }

        var newExpression = expression.replace("%","/100") // 퍼센트기호는 1/100으로 처리

        val parsedExpression: Array<String> = stringParse(newExpression) // 문자열에서 숫자와 함수, 연산자를 분리하여 배열에 저장
        Log.d("expression", "중위수식 : ${parsedExpression.contentToString()}")

        //  중위 -> 후위 표기식 변환
        val operatorStack = Stack<String>()
        var postfixExpression = arrayOf<String>()
        for(e in parsedExpression) {
            when(e) {
                "+", "-", "*", "/", "^" -> {
                    while(operatorStack.isNotEmpty()) {
                        if((checkOperatorPriority(e, operatorStack.peek())))
                            break
                        postfixExpression += operatorStack.pop()
                    }
                    operatorStack.push(e)
                }
                ")" -> { // 괄호처리
                    try{
                        while(operatorStack.peek() != "(") {
                            postfixExpression += operatorStack.pop() // 대응 괄호 없으면 Error
                        }
                        operatorStack.pop() // 남아있는 ( pop
                    } catch (e: EmptyStackException) {
                        return "Error" // 대응되는 )가 없는 경우 에러 발생
                    }
                }
                "(", "sqrt", "func", "log", "logb" -> {
                    operatorStack.push(e)
                }
                else -> {
                    postfixExpression += e
                }
            }
        }

        // 후위식으로 변환된 스택을 배열화 함
        while(operatorStack.isNotEmpty())
            postfixExpression += operatorStack.pop()

        Log.d("expression", "후위수식 : ${postfixExpression.contentToString()}")

        // 후위표기식연산
        val valueStack = Stack<Double>()

        // 연산 실시, 오류 발생시 catch
        try{
            for(element in postfixExpression){
                if(element.toDoubleOrNull() != null){ // 만약 현재 token이 숫자데이터면 스택 push
                    valueStack.push(element.toDouble())
                } else {
                    // 각 연산자 실제 기능 구현
                    when(element) {
                        "+" -> valueStack.push(valueStack.pop() + valueStack.pop())         // 덧셈
                        "-" -> valueStack.push(-1*(valueStack.pop() - valueStack.pop()))    // 뺄셈
                        "*" -> valueStack.push(valueStack.pop() * valueStack.pop())         // 곱셈
                        "/" -> {                                                                 // 나눗셈
                            val a = valueStack.pop()
                            val b = valueStack.pop()
                            if(a == 0.0) { // 0으로 나누는 경우
                                Log.d("expression", "Zero Divided")
                                return "Error"
                            } else {
                                valueStack.push(b/a)
                            }
                        }
                        "sqrt" -> valueStack.push(sqrt(valueStack.pop()))                        // 제곱근
                        "^" -> {                                                                 // 제곱
                            val a = valueStack.pop()
                            val b = valueStack.pop()
                            valueStack.push(Math.pow(b, a))
                        }
                        "func" -> { // 사용자 기능 함수 구현, 저장된 사용자 기능 함수를 불러와서 해당 문자열로 대체하고 실행
                            val x = valueStack.pop()
                            val expression = this.customFunctionString.replace("x",x.toString()) // func() 문을 저장된 사용자기능함수로 변환
                            val result = evaluateExpression(expression) // 재귀호출
                            if(result.toDoubleOrNull() != null) // 만약 결과값이 숫자라면 성공
                                valueStack.push(result.toDouble())
                            else
                                return "Error" // 아니면 에러
                        }
                        "log" -> {                                                               // 상용로그
                            valueStack.push(Math.log10(valueStack.pop()))
                        }
                        "logb" -> {                                                              // 이진로그
                            valueStack.push(Math.log(valueStack.pop())/Math.log(2.0))
                        }
                    }
                }
            }
        } catch(e: EmptyStackException) { // 배열 비었을 때(오류 발생)
            return "Error";
        }


        if(valueStack.isEmpty()) // 결과값이 비었다면(=없다면) 0 출력
            return "0"
        else {
            var result = valueStack.peek()
            result = round(result*10000000000)/10000000000 // 소수점 아래 10자리로 반올림

            var resultString = result.toString() // 결과값을 string변환
            return resultString
        }
    }

    // 문자열을 숫자, 기호, 함수별로 분리하여 배열에 저장하는 함수
    private fun stringParse(expression: String) : Array<String> {
        // 각 수식 문자( +, -, ), ( ... ) 등 검출하는 정규표현식
        val regex = "(?<=\\+)|(?=\\+)|(?<=-)|(?=-)|(?<=\\*)|(?=\\*)|(?<=/)|(?=/)|(?<=\\^)|(?=\\^)|(?<=logb)|(?=logb)|(?<=log)|(?=log)|(?<=sqrt)|(?=sqrt)|(?<=func)|(?=func)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))"
        var splitText = expression.split(Regex(regex)) // 수식문자별로 쪼개기
        splitText = splitText.filter { it.isNotEmpty() } // 공백문자열 제거

        // 음수 처리, 만약 -2+3의 수식의 경우, -가 연산자가 아닌 -2로, -2 자체가 숫자표현이 되어야 함.
        var index = 0 // 전체 텍스트 순회
        while(index < splitText.size-1) {
            // 음수기호 처리 처리
            if(splitText[index] == "-") {
                if(index == 0 || splitText[index-1] in setOf("*","-","/","%","^","(")){ // 음수기호 바로 전 문자열이 위 문자라면,
                    splitText = splitText.subList(0,index) + listOf<String>("-1","*") + splitText.subList(index+1,splitText.size) // -기호를 -1 * 로 바꿔준다. 즉 음수처리한다.
                }
            }
            index++
        }
        return splitText.toTypedArray() // 배열로 변환하여 반환
    }
}