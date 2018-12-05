package com.stark.badge.demo

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var badgeLayout: BadgeLayout
    var colors = intArrayOf(Color.BLUE, Color.GREEN, Color.RED)
    var count = ""
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        badgeLayout = findViewById(R.id.badgeLayout)

        badgeLayout.setOnClickListener {
            count += "1"
            if (count.length > 10) {
                count = "1"
            }
            badgeLayout.setBadgeColor(colors[index++ % (colors.size)])
            badgeLayout.setBadgeText(count)
            badgeLayout.setBadgeVisible(true)
        }
    }
}
