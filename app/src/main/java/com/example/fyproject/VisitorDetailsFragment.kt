package com.example.fyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.fyproject.data.visitor

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VisitorDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisitorDetailsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_visitor_details, container, false)

        val visName = view.findViewById<TextView>(R.id.visDetailsName)
        val visPlate = view.findViewById<TextView>(R.id.visDetailsPlate)
        val visOwner = view.findViewById<TextView>(R.id.visDetailsOwner)
        val visDate = view.findViewById<TextView>(R.id.visDetailsVisitDate)

        val selectedVisitor = arguments?.getSerializable("selectedVisitor") as? visitor
        if (selectedVisitor != null) {
            Toast.makeText(requireContext(),"I got the result", Toast.LENGTH_SHORT).show()
            visName.text = selectedVisitor.name
        }else{
            visName.text = "not found"
        }

        return view
    }


}