package com.override0330.teamim.view

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.override0330.teamim.R
import com.override0330.teamim.base.BaseActivity
import com.override0330.teamim.model.ShowOrHideProgressBarEvent
import com.override0330.teamim.view.adapter.HomeViewPagerAdapter
import com.override0330.teamim.view.contact.ContactHomeActivity
import com.override0330.teamim.view.home.MessageHomeFragment
import com.override0330.teamim.view.home.InformationFragment
import com.override0330.teamim.view.home.TaskFragment
import com.override0330.teamim.view.message.MessageCreateTeamActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.bottom_navigation_main
import kotlinx.android.synthetic.main.fragment_main.iv_toolbar_left
import kotlinx.android.synthetic.main.fragment_main.iv_toolbar_right
import kotlinx.android.synthetic.main.fragment_main.progress_bar_main
import kotlinx.android.synthetic.main.fragment_main.tv_toolbar_title
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        val adapter = HomeViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(MessageHomeFragment())
        adapter.addFragment(TaskFragment())
        adapter.addFragment(InformationFragment())
        vp_main.adapter = adapter
        vp_main.offscreenPageLimit=3
        bottom_navigation_main.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_message ->{
                    tv_toolbar_title.text = "消息"
                    iv_toolbar_left.setImageResource(R.mipmap.ic_contact)
                    iv_toolbar_left.setOnClickListener {
                        val intent = Intent(this,ContactHomeActivity::class.java)
                        startActivity(intent)
                    }
                    iv_toolbar_right.setImageResource(R.mipmap.ic_add)
                    iv_toolbar_right.setOnClickListener {
                        val intent = Intent(this, MessageCreateTeamActivity::class.java)
                        startActivity(intent)
                    }
                    vp_main.setCurrentItem(0,false)
                }
                R.id.navigation_task ->{
                    tv_toolbar_title.text = "任务"
                    vp_main.setCurrentItem(1,false)
                    iv_toolbar_right.setImageResource(0)
                }
                R.id.navigation_square ->{
                    tv_toolbar_title.text = "广场"
                    vp_main.setCurrentItem(2,false)
                    iv_toolbar_right.setImageResource(0)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        vp_main.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                bottom_navigation_main.menu.getItem(position).isChecked = true
            }

            override fun onPageSelected(position: Int) {}
        })
        bottom_navigation_main.selectedItemId = R.id.navigation_message

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun ShowProgressBar(showOrHideProgressBarEvent: ShowOrHideProgressBarEvent){
        if (showOrHideProgressBarEvent.isShow){
            progress_bar_main.show()
        }else{
            progress_bar_main.hide()
        }
    }
}