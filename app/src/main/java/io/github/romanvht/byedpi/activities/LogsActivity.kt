package io.github.romanvht.byedpi.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import io.github.romanvht.byedpi.R
import io.github.romanvht.byedpi.utility.InAppLog

class LogsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        val textView = findViewById<TextView>(R.id.logsText)
        val scroll = findViewById<ScrollView>(R.id.logsScroll)
        val clearBtn = findViewById<Button>(R.id.logsClear)
        val refreshBtn = findViewById<Button>(R.id.logsRefresh)

        fun refresh() {
            textView.text = InAppLog.readAll(this)
            scroll.post { scroll.fullScroll(ScrollView.FOCUS_DOWN) }
        }

        refreshBtn.setOnClickListener { refresh() }
        clearBtn.setOnClickListener {
            InAppLog.clear(this)
            refresh()
        }

        refresh()
    }
}
