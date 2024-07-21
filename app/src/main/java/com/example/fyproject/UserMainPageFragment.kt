package com.example.fyproject

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.fyproject.adapter.ImageAdapter
import com.example.fyproject.listener.UserMainPageListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserMainPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserMainPageFragment : Fragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList:ArrayList<Int>
    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_main_page, container, false)

        init(view)
        //setUpTransformer()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable , 6000)
            }
        })

        val visitorReg: Button = view.findViewById(R.id.visitorRegBtn)
        val visitorList: Button= view.findViewById(R.id.visitorListBtn)
        val parkingRes:Button = view.findViewById(R.id.parkingResBtn)
        val parkingList:Button = view.findViewById(R.id.parkingListBtn)

        visitorList.setOnClickListener{
            changeFragment(VisitorListFragment())
            updateToolbarTitle("Visitor List")
        }

        visitorReg.setOnClickListener{
            changeFragment(VisitorRegistrationFragment())
            updateToolbarTitle("Visitor Registration Form")
        }

        parkingRes.setOnClickListener{
            changeFragment(ParkingReservationFragment())
            updateToolbarTitle("Parking Reservation Form")
        }

        parkingList.setOnClickListener{
            changeFragment(ParkingListFragment())
            updateToolbarTitle("Visitor List")
        }

        return view
    }


    private fun changeFragment(frag : Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragmentContainer, frag)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
    private fun updateToolbarTitle(title: String) {
        (activity as? UserMainPageListener)?.onFragmentAction(title)
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable , 2000)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    //setUpTransformer() <----  temporary unable the code
//    private fun setUpTransformer(){
//        val transformer = CompositePageTransformer()
//        transformer.addTransformer(MarginPageTransformer(40))
//        transformer.addTransformer{ page, position ->
//            val r = 1 - abs(position)
//            page.scaleY = 0.85f + r + 0.14f
//
//        }
//
//        viewPager2.setPageTransformer(transformer)
//    }

    private fun init(view: View){
        viewPager2 = view.findViewById(R.id.viewPager2)
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        imageList.add(R.drawable.a)
        imageList.add(R.drawable.b)
        imageList.add(R.drawable.c)

        adapter = ImageAdapter(imageList, viewPager2)

        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

}