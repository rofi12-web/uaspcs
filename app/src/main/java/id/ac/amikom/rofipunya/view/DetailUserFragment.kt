package id.ac.amikom.rofipunya.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.ac.amikom.rofipunya.viewModel.DetailUserViewModel
import id.ac.amikom.rofipunya.adapter.SectionsPagerAdapter
import id.ac.amikom.rofipunya.model.User
import id.ac.amikom.rofipunya.model.UserDetail
import id.ac.amikom.rofipunya.R
import kotlinx.android.synthetic.main.fragment_detail_user.*
import kotlinx.android.synthetic.main.layout_followers_and_following.*

class DetailUserFragment : Fragment() {

    private lateinit var detailUserViewModel: DetailUserViewModel
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_detail_user, container, false)

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar()
        setHasOptionsMenu(true)
        getSelectedUser()
        showLoadingUserDetails(true)
        setViewPager()
        detailUserViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(DetailUserViewModel::class.java)
        username.let { detailUserViewModel.setDetailUser(it, requireContext()) }
        detailUserViewModel.getDetailUser().observe(viewLifecycleOwner, Observer { detailUserItems ->
            if (detailUserItems != null) {
                showUserDetails(detailUserItems)
                showLoadingUserDetails(false)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.menu_share, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onNavigateUp(item.itemId)
        //shareUserDetail(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setActionBar() {
        (activity as AppCompatActivity?)?.supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.title =
            getString(R.string.app_title_detail)
    }

    private fun onNavigateUp(itemId: Int) {
        if (itemId == android.R.id.home) activity?.onBackPressed()
    }

    private fun getSelectedUser() {
        val user = arguments?.getParcelable<User>(HomeFragment.EXTRA_USER) as User
        val image = user.avatar
        val userType = user.type
        username = user.username.toString()
        Glide.with(this).load(image).into(civ_avatar_received)
        //when (userType) {
            //"User" -> Glide.with(this).load(R.drawable.ic_user).into(iv_type_account)
           // else -> Glide.with(this).load(R.drawable.ic_organization).into(iv_type_account)
       // }
        tv_username_received.text = username
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setViewPager() {
        val sectionsPagerAdapter = context?.let {
            SectionsPagerAdapter(it, childFragmentManager)
        }
        sectionsPagerAdapter?.setUsername(username)
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
        activity?.actionBar?.elevation = 0f
    }
    
    private fun showUserDetails(detailUserItems: UserDetail) {
        if (detailUserItems.name?.isEmpty() == true || detailUserItems.name == "null") {
            tv_name_detail.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            tv_name_detail.text = getString(R.string.no_name_detail)
        } else tv_name_detail.text = detailUserItems.name
        if (detailUserItems.company?.isEmpty() == true || detailUserItems.company == "null") {
            tv_company_detail.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            tv_company_detail.text = getString(R.string.no_company_detail)
        } else tv_company_detail.text = detailUserItems.company
        if (detailUserItems.location?.isEmpty() == true || detailUserItems.location == "null") {
            tv_location_detail.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            tv_location_detail.text = getString(R.string.no_location_detail)
        } else tv_location_detail.text = detailUserItems.location
        tv_repositories_detail.text = detailUserItems.repositories.toString()
        tv_tab_item_followers.text = detailUserItems.followers.toString()
        tv_tab_item_following.text = detailUserItems.following.toString()
    }

    private fun showLoadingUserDetails(state: Boolean) {
        if (state) group_loading_bar_user.visibility = View.VISIBLE
        else group_loading_bar_user.visibility = View.GONE
    }
}
