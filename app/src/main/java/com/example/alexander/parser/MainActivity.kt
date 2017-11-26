package com.example.alexander.parser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val incomplete = ArrayList<Statement>()
    private val complete = ArrayList<Statement>()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        output.adapter = adapter

        RxTextView.textChanges(input)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map { parse(it.toString()) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.text = it
                    adapter.clear()
                    adapter.addAll(complete.map { it.toString() })
                    adapter.notifyDataSetChanged()
                }, { it.printStackTrace() })
    }

    private fun parse(text: String): String {
        return if (text.isEmpty()) getString(R.string.error_empty_string)
        else {
            incomplete.clear()
            complete.clear()

            for (i in 0 until text.length) {
                val element = text[i].toString()
                val open = Statement.Companion.Type.open(element)
                if (open != null) {
                    if (open) {
                        incomplete.add(Statement(Statement.Companion.Type.get(element), i))
                    } else {
                        val last = incomplete.last()
                        if (last.type == Statement.Companion.Type.get(element)) {
                            last.end = i
                            incomplete.remove(last)
                            complete.add(last)
                        } else return getString(R.string.invalid)
                    }
                } else return getString(R.string.bad_element)
            }
            if (incomplete.isNotEmpty()) return getString(R.string.error_uncomplite)
            return getString(R.string.valid)
        }

    }
}

