package com.acorn.test.unitydemo4.bean

/**
 * Created by acorn on 2021/7/21.
 */

/**
 *
{
"header": {
"namespace": "SpeechRecognizer",
"name": "RecognitionCompleted",
"status": 20000000,
"message_id": "f1fc681f74b543d7a7f07c692e9802a8",
"task_id": "7fd7340dd86244d1bd43f6046c787163",
"status_text": "Gateway:SUCCESS:Success."
},
"payload": {
"result": "没有一二三四五上山打老虎",
"duration": 3990
}
}
 */
data class ASRBean(
    val header: Header,
    val payload: Payload
)

data class Header(
    val message_id: String,
    val name: String,
    val namespace: String,
    val status: Int,
    val status_text: String,
    val task_id: String
)

data class Payload(
    val duration: Int,
    val result: String
)