package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentHomeBinding
import android.content.Intent
import com.example.myapplication.ui.activity.FaceRecognitionActivity
import com.example.myapplication.ui.activity.StartAttendanceActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

//        val startAttendance: TextView = binding.startAttendance
//        startAttendance.setOnClickListener {
//            val intent = Intent(context, StartAttendanceActivity::class.java).apply {
//            putExtra("START ATTENDANCE", "Starting start attendance")
//        }
//            startActivity(intent)
//        }
        val startAttendance: TextView = binding.startAttendance
        startAttendance.setOnClickListener {
            val intent = Intent(context, StartAttendanceActivity::class.java).apply {
            putExtra("START ATTENDANCE", "Starting start attendance")
        }
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}