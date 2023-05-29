package com.uniuwo.simpledict.ui.help

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uniuwo.simpledict.R


class HelpFragment : Fragment() {
    lateinit var helpView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_help, container, false)
         helpView = view.findViewById(R.id.helpContent)
        initHelpContent()
        return view
    }

    private fun initHelpContent() {
        if(helpView == null) return

        val html = Html.fromHtml(requireContext().getString(R.string.help_content))
        helpView.text = html
    }
}