package studio.mandysa.jiuwo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.BindingViewHolder>() {

    private var onBind: (BindingViewHolder.() -> Unit)? = null

    private var onCreate: (ViewCreate.() -> Unit)? = null

    var headers: List<Any?>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            if (field != null)
                notifyItemRangeChanged(0, field!!.size)
        }

    var models: List<Any?>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            if (field != null)
                notifyItemRangeChanged(headers?.size ?: 0, headers?.size ?: 0 + field!!.size)
        }

    var footers: List<Any?>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            if (field != null)
                notifyItemRangeChanged(headers?.size ?: 0 + (models?.size ?: 0), itemCount)
        }

    var mModel: Any? = null

    fun onBind(block: BindingViewHolder.() -> Unit) {
        onBind = block
    }

    fun onCreate(block: ViewCreate.() -> Unit) {
        onCreate = block
    }

    val type = HashMap<Class<*>, Int>()

    inline fun <reified M> addType(@LayoutRes id: Int) {
        type[M::class.java] = id
    }

    inline fun <reified M> getModel(): M = mModel as M

    inline fun <reified M> getModelOrNull(): M? = mModel as? M

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val viewCreate = ViewCreate(viewType)
        onCreate?.invoke(viewCreate)
        if (viewCreate.view == null) {
            viewCreate.view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        }
        return BindingViewHolder(viewCreate)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        mModel = getModel(position)
        onBind?.invoke(holder)
    }

    override fun onViewAttachedToWindow(holder: BindingViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached?.invoke(holder)
    }

    override fun onViewDetachedFromWindow(holder: BindingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached?.invoke(holder)
    }

    override fun onViewRecycled(holder: BindingViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled?.invoke(holder)
    }

    override fun getItemCount(): Int {
        return (headers?.size ?: 0) + (models?.size ?: 0) + (footers?.size ?: 0)
    }

    override fun getItemViewType(position: Int): Int {
        return type[getModel(position)::class.java]!!
    }

    fun addHeader(model: Any) {
        if (headers != null) {
            val list = ArrayList(headers)
            list.add(model)
            headers = list
            return
        }
        headers = listOf(model)
    }

    fun addModels(models: List<Any>) {
        if (this.models != null) {
            val list = ArrayList(this.models)
            list.addAll(models)
            this.models = list
            return
        }
        this.models = listOf(models)
    }

    fun addFooter(model: Any) {
        if (footers != null) {
            val list = ArrayList(footers)
            list.add(model)
            footers = list
            return
        }
        footers = listOf(model)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearModels() {
        val size = itemCount
        headers = ArrayList()
        models = ArrayList()
        footers = ArrayList()
        notifyItemRemoved(size)
    }

    private fun getModel(position: Int): Any {
        val headerSize = headers?.size ?: 0
        val modelSize = models?.size ?: 0
        return if (position < headerSize) {
            headers ?: listIsEmpty("header")
            headers!![position]!!
        } else
            if (position >= headerSize && position < modelSize + headerSize) {
                models ?: listIsEmpty("model")
                models!![position - headerSize]!!
            } else {
                footers ?: listIsEmpty("footer")
                footers!![position - headerSize - modelSize]!!
            }
    }

    private fun listIsEmpty(listName: String) {
        throw NullPointerException("$listName Is Null!")
    }

    inner class BindingViewHolder(viewCreate: ViewCreate) :
        RecyclerView.ViewHolder(viewCreate.view) {

        internal var onAttached: (BindingViewHolder.() -> Unit)? = null

        internal var onDetached: (BindingViewHolder.() -> Unit)? = null

        internal var onRecycled: (BindingViewHolder.() -> Unit)? = null

        fun onAttached(block: BindingViewHolder.() -> Unit) {
            onAttached = block
        }

        fun onDetached(block: BindingViewHolder.() -> Unit) {
            onDetached = block
        }

        fun onRecycled(block: BindingViewHolder.() -> Unit) {
            onRecycled = block
        }

        val headerSize get() = headers?.size ?: 0

        val modelSize get() = this@RecyclerAdapter.models?.size ?: 0

        val footerSize get() = footers?.size ?: 0

        val models: List<Any?>?
            get() = if (layoutPosition < headerSize) {
                headers
            } else
                if (layoutPosition >= headerSize && layoutPosition < modelSize + headerSize) {
                    this@RecyclerAdapter.models
                } else {
                    footers
                }

        val modelPosition
            get():Int {
                return if (layoutPosition < headerSize) {
                    layoutPosition
                } else
                    if (layoutPosition >= headerSize && layoutPosition < modelSize + headerSize) {
                        layoutPosition - headerSize
                    } else {
                        (layoutPosition - headerSize - modelSize)
                    }
            }
    }

}