package io.github.romanvht.byedpi.activities

import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
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
        val copyBtn = findViewById<Button>(R.id.logsCopy)

        fun refresh() {
            textView.text = InAppLog.readAll(this)
            scroll.post { scroll.fullScroll(ScrollView.FOCUS_DOWN) }
        }

        refreshBtn.setOnClickListener { refresh() }
        copyBtn.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("logs", textView.text)
            clipboard.setPrimaryClip(clip)
        }
        clearBtn.setOnClickListener {
            InAppLog.clear(this)
            refresh()
        }

        refresh()
    }
}
