package com.techyourchance.multithreading.common

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.techyourchance.multithreading.FragmentContainerWrapper

class FragmentHelper(
    private val mActivity: Activity,
    fragmentContainerWrapper: FragmentContainerWrapper,
    fragmentManager: FragmentManager
) {
    private val mFragmentContainerWrapper: FragmentContainerWrapper
    private val mFragmentManager: FragmentManager
    fun replaceFragment(newFragment: Fragment) {
        replaceFragment(newFragment, true, false)
    }

    fun replaceFragmentAndRemoveCurrentFromHistory(newFragment: Fragment) {
        replaceFragment(newFragment, false, false)
    }

    fun replaceFragmentAndClearHistory(newFragment: Fragment) {
        replaceFragment(newFragment, false, true)
    }

    fun navigateBack() {
        if (mFragmentManager.isStateSaved) {
            // BACK NAVIGATION CAN BE SILENTLY ABORTED
            // since this flow involves popping the backstack, we can't execute it safely after
            // the state is saved
            // I asked a question about this: https://stackoverflow.com/q/52165653/2463035
            return
        }
        if (goBackInFragmentsHistory()) {
            return  // up navigation resulted in going back in fragments history
        }
        finishActivity()
    }

    fun navigateUp() {
        if (mFragmentManager.isStateSaved) {
            // UP NAVIGATION CAN BE SILENTLY ABORTED
            // since this flow involves popping the backstack, we can't execute it safely after
            // the state is saved
            // I asked a question about this: https://stackoverflow.com/q/52165653/2463035
            return
        }
        if (goBackInFragmentsHistory()) {
            return  // up navigation resulted in going back in fragments history
        }
        val currentFragment = currentFragment
        if (HierarchicalFragment::class.java.isInstance(currentFragment)) {
            val parentFragment =
                (currentFragment as HierarchicalFragment?)!!.hierarchicalParentFragment
            if (parentFragment != null) {
                replaceFragment(parentFragment, false, true)
                return  // up navigation resulted in going to hierarchical parent fragment
            }
        }
        if (mActivity.onNavigateUp()) {
            return  // up navigation resulted in going to hierarchical parent activity
        }
        finishActivity() // no "up" navigation targets - just finish the activity
    }

    private fun goBackInFragmentsHistory(): Boolean {
        if (fragmentsHistoryCount > 0) {
            // A call to popBackStack can leave the currently visible fragment on screen. Therefore,
            // we start with manual removal of the current fragment.
            // Description of the issue can be found here: https://stackoverflow.com/q/45278497/2463035
            removeCurrentFragment()
            mFragmentManager.popBackStackImmediate()
            return true
        }
        return false
    }

    private fun replaceFragment(
        newFragment: Fragment,
        addToBackStack: Boolean,
        clearBackStack: Boolean
    ) {
        val ft = mFragmentManager.beginTransaction()
        if (clearBackStack) {
            if (mFragmentManager.isStateSaved) {
                // If the state is saved we can't clear the back stack. Simply not doing this, but
                // still replacing fragment is a bad idea. Therefore we abort the entire operation.
                return
            }

            /*
              Due to the way backstack works, either just clearing backstack or removing fragments
              won't work reliably:
              Just pop backstack -> existing fragment on the backstack can become visible during transition
              Just remove fragments -> screws up default back button behavior
              Therefore, we need to do both.
              Remove all entries from back stack
            */if (mFragmentManager.backStackEntryCount > 0) {
                mFragmentManager.popBackStack(
                    mFragmentManager.getBackStackEntryAt(0).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }

            // Remove all fragments
            for (fragment in mFragmentManager.fragments) {
                ft.remove(fragment)
            }
        }
        if (addToBackStack) {
            ft.addToBackStack(null)
        }

        // Change to a new fragment
        ft.replace(fragmentFrameId, newFragment)
        commitFragmentTransactionSafely(ft)
    }

    private fun removeCurrentFragment() {
        val ft = mFragmentManager.beginTransaction()
        ft.remove(currentFragment!!)
        commitFragmentTransactionSafely(ft)

        // not sure it is needed; will keep it as a reminder to myself if there will be problems
        // mFragmentManager.executePendingTransactions();
    }

    private fun commitFragmentTransactionSafely(ft: FragmentTransaction) {
        // TODO: add mechanism for notifications about commits that allow state loss
        if (mFragmentManager.isStateSaved) {
            // We acknowledge the possibility of losing this transaction if the app undergoes
            // save&restore flow after it is committed.
            ft.commitAllowingStateLoss()
        } else {
            ft.commit()
        }
    }

    // TODO: double check that fragments history count equals to backstack entry count
    private val fragmentsHistoryCount: Int
        get() =// TODO: double check that fragments history count equals to backstack entry count
            mFragmentManager.backStackEntryCount

    private val currentFragment: Fragment?
        get() = mFragmentManager.findFragmentById(fragmentFrameId)
    private val fragmentFrameId: Int
        get() = mFragmentContainerWrapper.fragmentContainer.id

    private fun finishActivity() {
        ActivityCompat.finishAfterTransition(mActivity)
    }

    init {
        mFragmentContainerWrapper = fragmentContainerWrapper
        mFragmentManager = fragmentManager
    }
}