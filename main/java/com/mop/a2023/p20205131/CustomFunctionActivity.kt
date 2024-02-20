package com.mop.a2023.p20205131

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.mop.a2023.p20205131.databinding.ActivityCustomFunctionBinding
import com.mop.a2023.p20205131.databinding.ActivityMainBinding

class CustomFunctionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩 처리
        val binding = ActivityCustomFunctionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var customFunctionString = intent.getStringExtra("customFunctionString")
        binding.customFunctionText.text = customFunctionString?:"x"

        binding.apply{
            // 입력완료 버튼 클릭시 되돌려줄 값을 intent에 저장
            completeButton.setOnClickListener {
                var text = customFunctionText.text
                intent.putExtra("customFunctionString",text?:"x")
                setResult(RESULT_OK, intent)
                finish() // 창 종료
            }

            // 버튼들에게 문자를 텍스트뷰에 넣는 이벤트 리스너를 등록시킵니다.
            oneButton.setOnClickListener {addCharacterToText("1",customFunctionText)}
            twoButton.setOnClickListener {addCharacterToText("2",customFunctionText)}
            threeButton.setOnClickListener {addCharacterToText("3",customFunctionText)}
            fourButton.setOnClickListener {addCharacterToText("4",customFunctionText)}
            fiveButton.setOnClickListener {addCharacterToText("5",customFunctionText)}
            sixButton.setOnClickListener {addCharacterToText("6",customFunctionText)}
            sevenButton.setOnClickListener {addCharacterToText("7",customFunctionText)}
            eightButton.setOnClickListener {addCharacterToText("8",customFunctionText)}
            nineButton.setOnClickListener {addCharacterToText("9",customFunctionText)}
            zeroButton.setOnClickListener {addCharacterToText("0",customFunctionText)}
            plusButton.setOnClickListener {addCharacterToText("+",customFunctionText)}
            minusButton.setOnClickListener {addCharacterToText("-",customFunctionText)}
            timesButton.setOnClickListener {addCharacterToText("*",customFunctionText)}
            dotButton.setOnClickListener {addCharacterToText(".",customFunctionText)}
            divideButton.setOnClickListener {addCharacterToText("/",customFunctionText)}
            leftBraceButton.setOnClickListener {addCharacterToText("(",customFunctionText)}
            rightBraceButton.setOnClickListener {addCharacterToText(")",customFunctionText)}
            xButton.setOnClickListener {addCharacterToText("x",customFunctionText)}
            deleteButton.setOnClickListener {delCharacterFromText(customFunctionText)}
            clearButton.setOnClickListener {clearText(customFunctionText)}
        }
    }

    // 수식 textview 에서 문자 하나 삭제하는 함수
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
                newText = newText.substring(0,cursor) // 문자 지우기
                if(newText.isEmpty()) newText = "0" // 모두 비면 0으로 세팅
                text = newText
            }

        }
    }

    // 수식 textView 초기화 하는 함수
    private fun clearText(textView: TextView) {
        textView.text = "0"
    }

    // 수식 textView에 문자를 하나 뒤에 추가하는 함수
    fun addCharacterToText(character:String, textView: TextView) {
        textView.apply{
            var newText: String? = text.toString()
            if(newText == null || newText == "0") // 문자열에 정보 없는 경우
                newText = character
            else
                newText += character

            text = newText
        }
    }
}