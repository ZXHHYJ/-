package studio.mandysa.music

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sothree.slidinguppanel.PanelSlideListener
import com.sothree.slidinguppanel.PanelState
import com.yanzhenjie.sofia.Sofia
import mandysax.fragment.Fragment
import mandysax.fragment.FragmentActivity
import mandysax.fragmentpage.widget.FragmentPage
import mandysax.navigation.fragment.NavHostFragment
import mandysax.tablayout.BottomNavigationItem
import mandysax.tablayout.setActiveColor
import mandysax.tablayout.setInActiveColor
import studio.mandysa.music.databinding.ActivityMainBinding
import studio.mandysa.music.logic.utils.getInstance
import studio.mandysa.music.logic.utils.inflate
import studio.mandysa.music.logic.utils.viewModels
import studio.mandysa.music.ui.event.UserViewModel
import studio.mandysa.music.ui.home.HomeFragment
import studio.mandysa.music.ui.login.LoginFragment
import studio.mandysa.music.ui.me.MeFragment


class MainActivity : FragmentActivity() {

    private val mBinding: ActivityMainBinding by inflate()

    private val mEvent: MainViewModel by viewModels()

    private val mUserViewModel: UserViewModel by viewModels()

    private fun isNightMode() =
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sofia = Sofia.with(this@MainActivity)
        sofia.let {
            it.statusBarBackground(android.R.color.transparent)
                .navigationBarBackground(android.R.color.transparent)
            it.invasionStatusBar().invasionNavigationBar()
            if (isNightMode())
                it.statusBarLightFont().navigationBarLightFont()
            else it.statusBarDarkFont().navigationBarDarkFont()
        }
        setContentView(mBinding.root)
        //判断有没有登录，没有登录的话就打开登录界面
        //println(mUserViewModel.getCookieLiveData().value)
        if (mUserViewModel.getCookieLiveData().value == null)
            LoginFragment().show(fragmentPlusManager)
        mBinding.apply {
            //解决playFragment点击无事件view会关闭播放界面的问题
            playFragment.setOnTouchListener { _, _ -> mBinding.mainSlidingView.panelState == PanelState.EXPANDED }
            //不把shadowHeight设置为0的话后续修改shadowHeight都将失效！！
            mainSlidingView.shadowHeight = 0
            mainFragmentPage.setAdapter(object : FragmentPage.Adapter {
                override fun onCreateFragment(position: Int): Fragment =
                    NavHostFragment.create(
                        when (position) {
                            0 -> HomeFragment()
                            1 -> MeFragment()
                            else -> null
                        }
                    )
            })
            bottomNavigationBar.models = listOf(
                BottomNavigationItem(
                    R.drawable.ic_round_contactless_24,
                    getString(R.string.home)
                ),
                BottomNavigationItem(
                    R.drawable.ic_round_account_circle_24,
                    getString(R.string.me)
                )
            )
                .setActiveColor(getColor(R.color.main))
                .setInActiveColor(getColor(R.color.default_unchecked_color))
            bottomNavigationBar.setSelectedPosition(mEvent.homePosLiveData.value)
            mEvent.homePosLiveData.observe(
                this@MainActivity
            ) {
                mainFragmentPage.position = it
            }
            bottomNavigationBar.getSelectedPosition().observe(this@MainActivity) {
                mEvent.homePosLiveData.value = it
            }
            ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
                //避免底部导航遮挡内容
                if (mainSlidingView.panelHeight == 0) {
                    mainSlidingView.panelHeight =
                        resources.getDimensionPixelOffset(R.dimen.nav_height)
                }
                val startBarSize = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                val navigationBarSize =
                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                mainFragmentPage.setPadding(
                    0,
                    startBarSize,
                    0,
                    navigationBarSize
                )
                bottomNavLayout.setPadding(
                    0,
                    0,
                    0,
                    navigationBarSize
                )
                mainSlidingView.isTouchEnabled = false
                getInstance().changeMusicLiveData().lazy(this@MainActivity) {
                    mBinding.apply {
                        mainSlidingView.isTouchEnabled = true
                        bottomNavigationBar.post {
                            mainSlidingView.apply {
                                panelHeight =
                                    (context.resources.getDimensionPixelOffset(R.dimen.nav_height) * 2) + navigationBarSize
                                mBinding.mainFragmentPage.setPadding(
                                    0,
                                    mBinding.mainFragmentPage.paddingTop,
                                    0,
                                    0
                                )
                                shadowHeight =
                                    resources.getDimensionPixelSize(R.dimen.umano_shadow_height)
                                postDelayed({
                                    val y =
                                        mBinding.root.bottom - mBinding.bottomNavLayout.height
                                    addPanelSlideListener(object :
                                        PanelSlideListener {
                                        override fun onPanelSlide(panel: View, slideOffset: Float) {
                                            val by: Float =
                                                y + bottomNavLayout.height * slideOffset * 8
                                            if (y < y + bottomNavLayout.height) {
                                                bottomNavLayout.y = by
                                            }
                                            val alpha = slideOffset * 12
                                            playFragment.alpha = alpha
                                            controllerFragment.alpha = 1 - alpha
                                        }

                                        override fun onPanelStateChanged(
                                            panel: View,
                                            previousState: PanelState,
                                            newState: PanelState
                                        ) {
                                            when (newState) {
                                                PanelState.EXPANDED -> {
                                                    sofia
                                                        .statusBarLightFont()
                                                        .navigationBarLightFont()
                                                }
                                                PanelState.DRAGGING -> {}
                                                else -> {
                                                    sofia.let {
                                                        if (isNightMode())
                                                            it.statusBarLightFont()
                                                                .navigationBarLightFont()
                                                        else it.statusBarDarkFont()
                                                            .navigationBarDarkFont()
                                                    }
                                                }
                                            }

                                        }
                                    })
                                }, 220)
                            }
                        }
                    }
                }
                insets
            }
        }
    }

    override fun onBackPressed() {
        when (mBinding.mainSlidingView.panelState) {
            PanelState.EXPANDED, PanelState.DRAGGING -> {
                mBinding.mainSlidingView.panelState = PanelState.COLLAPSED
            }
            else -> super.onBackPressed()
        }
    }

}