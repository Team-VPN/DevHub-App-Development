package com.teamvpn.devhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main_chat.*

class MainChat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)
        setSupportActionBar(toolbar1)

        val toolbar1: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar1)
        supportActionBar!!.title=""

        val tabLayout: TabLayout = findViewById(R.id.tablyt)
        val viewPager: ViewPager = findViewById(R.id.view_pager1)
    }
}