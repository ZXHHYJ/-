package studio.mandysa.music.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import studio.mandysa.music.R
import studio.mandysa.music.databinding.FragmentControllerBinding
import studio.mandysa.music.logic.utils.DefaultPlayManagerUtils.getInstance
import studio.mandysa.music.ui.base.BaseFragment

class ControllerFragment : BaseFragment() {

    private val mBinding: FragmentControllerBinding by bindView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInstance().changeMusicLiveData().observe(viewLifecycleOwner) {
            mBinding.run {
                controllerTitle.text = it.title
            }
        }
        getInstance().pauseLiveData().observe(viewLifecycleOwner) {
            mBinding.controllerPlayOrPause.setImageResource(if (it) R.drawable.ic_play else R.drawable.ic_pause)
        }
        mBinding.controllerPlayOrPause.setOnClickListener {
            getInstance().run {
                if (pauseLiveData().value == true)
                    play()
                else pause()
            }
        }
        mBinding.controllerNextSong.setOnClickListener {
            getInstance().skipToNext()
        }
    }

}