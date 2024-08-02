package com.example.fyproject.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.fyproject.R
import com.example.fyproject.listener.UserMainPageListener


class AdminMainPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_main_page, container, false)

        val visitorRegAdmin: Button = view.findViewById(R.id.visRegBtnAdmin)
        val visitorListAdmin: Button= view.findViewById(R.id.visListBtnAdmin)
        val parkingResAdmin:Button = view.findViewById(R.id.parkingResBtnAdmin)
        val parkingListAdmin:Button = view.findViewById(R.id.parkingListBtnAdmin)
        val allUserListAdmin:Button = view.findViewById(R.id.allUserBtnAdmin)
        val uploadBtn : Button = view.findViewById(R.id.uploadAnnocBtnAdmin)


        visitorRegAdmin.setOnClickListener{
            changeFragment(AdminVisitorRegistrationFragment())
            updateToolbarTitle("Visitor Registration")
        }

        parkingResAdmin.setOnClickListener{
            changeFragment(AdminParkingReservationFragment())
            updateToolbarTitle("Parking Reservation")
        }

        uploadBtn.setOnClickListener{
            changeFragment(AdminUploadAnnouncementFragment())
            updateToolbarTitle("Upload Announcement")
        }

        visitorListAdmin.setOnClickListener{
            changeFragment(AdminVisitorListFragment())
            updateToolbarTitle("Visitor List")
        }

        parkingListAdmin.setOnClickListener{
            changeFragment(AdminParkingListFragment())
            updateToolbarTitle("Parking Reservation List")
        }

        allUserListAdmin.setOnClickListener{
            changeFragment(AdminGetUserListFragment())
            updateToolbarTitle("User List")
        }


        return view
    }

    private fun changeFragment(frag : Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.adminFragmentContainer, frag)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun updateToolbarTitle(title: String) {
        (activity as? UserMainPageListener)?.onFragmentAction(title)
    }

}