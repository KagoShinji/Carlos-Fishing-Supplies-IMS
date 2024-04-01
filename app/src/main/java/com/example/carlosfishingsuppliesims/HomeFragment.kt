package com.example.carlosfishingsuppliesims

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cardView = view.findViewById<CardView>(R.id.productsNotification)
        cardView.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productsFragment)
        }

        return view
    }

}
