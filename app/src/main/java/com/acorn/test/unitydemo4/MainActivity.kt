package com.acorn.test.unitydemo4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.unity3d.player.UnityPlayerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testTv.setOnClickListener {
            startActivity(Intent(this,UnityPlayerActivity::class.java))
        }
    }
}