package com.override0330.teamim

import android.content.Intent
import android.util.Log
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * @data 2019-08-16
 * @author Override0330
 * @description
 */

//BottomNavigation扩展函数
fun BottomNavigationView.setWithNavgationIds(
    navGraphIdList: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent
): LiveData<NavController>{

    // Map of tags
    val graphIdToTagMap = SparseArray<String>()
    // Result. Mutable live data with the selected controlled
    val selectedNavController = MutableLiveData<NavController>()

    var firstFragmentGraphId = 0

    //首先根据传进来的导航图的列表进行初始化操作，如果导航图对象已经存在，则提取
    navGraphIdList.forEachIndexed {index,navGraphId->

        val fragmentTag = "mainBottomNavigation_$index"

        val navHostFragment = getNavHostFragmnet(
            fragmentManager,
            fragmentTag,
            navGraphId,
            containerId)

        //拿到图的id
        val graphId = navHostFragment.navController.graph.id

        if(index==0){
            firstFragmentGraphId = graphId
        }

        //id和tag一一对应
        graphIdToTagMap[graphId] = fragmentTag

        if (this.selectedItemId == graphId){
            selectedNavController.value = navHostFragment.navController
            attachNavHostFragment(fragmentManager,navHostFragment,index==0)
        }else{
            detachNavHostFragment(fragmentManager,navHostFragment)
        }
    }

    //将新建好的navFragment和bottomNavigation链接起来
    var selectedItemTag = graphIdToTagMap[this.selectedItemId]
    val firstFragmentTag = graphIdToTagMap[firstFragmentGraphId]
    var isOnFirstFragment = selectedItemTag == firstFragmentTag

    //当一个地步导航被选中时
    setOnNavigationItemSelectedListener { item->
        if (fragmentManager.isStateSaved){
            false
        }else{
            val newlySelectedItemTag = graphIdToTagMap[item.itemId]
            if (selectedItemTag != newlySelectedItemTag){
                fragmentManager.popBackStack(firstFragmentTag,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                        as NavHostFragment

                if (firstFragmentTag != newlySelectedItemTag){
                    fragmentManager.beginTransaction()
                        .attach(selectedFragment)
                        .setPrimaryNavigationFragment(selectedFragment)
                        .apply {
                            graphIdToTagMap.forEach { _, fragmentTagIter ->
                                if (fragmentTagIter != newlySelectedItemTag) {
                                    detach(fragmentManager.findFragmentByTag(firstFragmentTag)!!)
                                }
                            }
                        }
                        .addToBackStack(firstFragmentTag)
                        .setReorderingAllowed(true)
                        .commit()
                    }
                selectedItemTag = newlySelectedItemTag
                isOnFirstFragment = selectedItemTag == firstFragmentTag
                selectedNavController.value = selectedFragment.navController
                true
            }else{
                false
            }

        }
    }

    fragmentManager.addOnBackStackChangedListener {
        if (!isOnFirstFragment && !fragmentManager.isOnBackStack(firstFragmentTag)) {
            this.selectedItemId = firstFragmentGraphId
        }

        // Reset the graph if the currentDestination is not valid (happens when the back
        // stack is popped after using the back button).
        selectedNavController.value?.let { controller ->
            if (controller.currentDestination == null) {
                controller.navigate(controller.graph.id)
            }
        }
    }
    return selectedNavController
}

fun getNavHostFragmnet(fragmentManager: FragmentManager,
                       fragmentTag: String,
                       navGraphId:Int,
                       containerId:Int):NavHostFragment{
    //查找是否存在这个图，存在就返回
    val existingNavHostFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
    existingNavHostFragment?.let { return it }

    //不存在就新建一个
    val newNavHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager.beginTransaction()
        .add(containerId,newNavHostFragment,fragmentTag)
        .commitNow()

    return newNavHostFragment
}

private fun attachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment,
    isPrimaryNavFragment: Boolean
) {
    fragmentManager.beginTransaction()
        .attach(navHostFragment)
        .apply {
            if (isPrimaryNavFragment) {
                setPrimaryNavigationFragment(navHostFragment)
            }
        }
        .commitNow()
}

private fun detachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment
) {
    fragmentManager.beginTransaction()
        .detach(navHostFragment)
        .commitNow()
}

private fun BottomNavigationView.setupItemReselected(
    graphIdToTagMap: SparseArray<String>,
    fragmentManager: FragmentManager
) {
    setOnNavigationItemReselectedListener { item ->
        Log.d("debug", "${item.itemId} ${graphIdToTagMap.toString()}")
        val newlySelectedItemTag = graphIdToTagMap[item.itemId]
        val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                as NavHostFragment
        val navController = selectedFragment.navController
        // Pop the back stack to the start destination of the current navController graph
        navController.popBackStack(
            navController.graph.startDestination, false
        )
    }
}

private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
    val backStackCount = backStackEntryCount
    for (index in 0 until backStackCount) {
        if (getBackStackEntryAt(index).name == backStackName) {
            return true
        }
    }
    return false
}