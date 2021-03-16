package com.maibaapp.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRVAdapterWithDiff<T, R : ViewBinding>(callback: DiffUtil.ItemCallback<T>): ListAdapter<T, ViewHolder<R>>(
    callback
) {
    private var itemClickListener: OnItemClickListener? = null
    fun setItemClickListener(block: ItemClickListener.() -> Unit): BaseRVAdapterWithDiff<T, R> {
        itemClickListener = object : ItemClickListener(){}.apply(block)
        return this
    }
    fun setOnItemClickListener(block: OnItemClickListener): BaseRVAdapterWithDiff<T, R> {
        itemClickListener =block
        return this
    }
    abstract fun getViewHolder(parent: ViewGroup, viewType: Int):R
    open fun  convert(holder: ViewHolder<R>, data: T, position: Int):Unit{
        onclick?.invoke(holder, data, position)
    }
    open fun convertClick(holder: ViewHolder<R>, data: T, position: Int):Unit{}
    fun convertClick(block: (holder: ViewHolder<R>, data: T, position: Int) -> Unit){
        onclick=block
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<R> {
        val holder= ViewHolder(getViewHolder(parent, viewType))
        holder.itemView.setOnClickListener{
            itemClickListener?.onItemClick(it, holder, holder.absoluteAdapterPosition)
        }
        holder.itemView.setOnLongClickListener {
            return@setOnLongClickListener itemClickListener?.onItemLongClick(
                it,
                holder,
                holder.absoluteAdapterPosition
            )?:false
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder<R>, position: Int) {
        convert(holder, getItem(position), position)
        convertClick(holder, getItem(position), position)
    }
    private var onclick:((holder: ViewHolder<R>, data: T, position: Int)->Unit)?=null
}

class ViewHolder<S : ViewBinding>(var viewBinding: S):RecyclerView.ViewHolder(viewBinding.root)


abstract class BaseRVAdapter<T, R : ViewBinding>(var dataList: MutableList<T> = mutableListOf<T>()): RecyclerView.Adapter<ViewHolder<R>>() {
    private var itemClickListener: OnItemClickListener? = null
    fun setItemClickListener(block: ItemClickListener.() -> Unit): BaseRVAdapter<T, R> {
        itemClickListener = object : ItemClickListener(){}.apply(block)
        return this
    }
    fun setOnItemClickListener(block: OnItemClickListener): BaseRVAdapter<T, R> {
        itemClickListener =block
        return this
    }
    abstract fun getViewHolder(parent: ViewGroup, viewType: Int):R
    open fun  convert(holder: R, data: T, position: Int):Unit{
        onclick?.invoke(holder, data, position)
    }
    open fun convertClick(holder: R, data: T, position: Int):Unit{}
    fun convertClick(block: (holder: R, data: T, position: Int) -> Unit){
        onclick=block
    }
    private var onclick:((holder: R, data: T, position: Int)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<R> {
        val holder= ViewHolder(getViewHolder(parent, viewType))
        holder.itemView.setOnClickListener{
            itemClickListener?.onItemClick(it, holder, holder.absoluteAdapterPosition)
        }
        holder.itemView.setOnLongClickListener {
            return@setOnLongClickListener itemClickListener?.onItemLongClick(
                it,
                holder,
                holder.absoluteAdapterPosition
            )?:false
        }
        return holder
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder<R>, position: Int) {
        convert(holder.viewBinding, dataList[position], position)
        convertClick(holder.viewBinding, dataList[position], position)
    }
}
typealias ClickType = (view: View?, holder: RecyclerView.ViewHolder?, position: Int) -> Unit
typealias LongClickType = (view: View?, holder: RecyclerView.ViewHolder?, position: Int) -> Boolean

abstract class ItemClickListener: OnItemClickListener {
    private var click: ClickType?= null
    private var longClick: LongClickType?= null
    override fun onItemClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int) {
        click?.invoke(view, holder, position)
    }

    override fun onItemLongClick(
        view: View?,
        holder: RecyclerView.ViewHolder?,
        position: Int
    ): Boolean {
        return longClick?.invoke(view, holder, position)?:false
    }
    fun onItemClick(block: ClickType){
        click = block
    }
    fun onItemLongClick(block: LongClickType){
        longClick= block
    }

}

fun addItemClickCallback(block: ItemClickListener.() -> Unit) = object : ItemClickListener(){}.also(
    block
)
interface OnItemClickListener {
    fun onItemClick(
        view: View?,
        holder: RecyclerView.ViewHolder?,
        position: Int
    )

    fun onItemLongClick(
        view: View?,
        holder: RecyclerView.ViewHolder?,
        position: Int
    ): Boolean
}


/**
 * 多类型item布局管理器
 */
class MultiItemViewDelegateManager<T>{
    private val delegates: SparseArrayCompat<ItemViewDelegate<T, ViewBinding>> = SparseArrayCompat()
    fun getItemViewBindingByType(parent: ViewGroup, viewType: Int):ViewBinding{
        val viewBinding=delegates.get(viewType)?.getItemViewBinding(parent)
        return viewBinding!!
    }

    fun getItemClickListener(viewType: Int) = getItemViewDelegate(viewType).itemClickListener
    fun getItemViewDelegateCount(): Int {
        return delegates.size()
    }

    fun addDelegate(delegate: ItemViewDelegate<T, ViewBinding>): MultiItemViewDelegateManager<T> {
        var viewType = delegates.size()
        delegates.put(viewType, delegate)
        viewType++
        return this
    }

    fun addDelegate(
        viewType: Int,
        delegate: ItemViewDelegate<T, ViewBinding>
    ): MultiItemViewDelegateManager<T> {
        require(delegates[viewType] == null) {
            ("An ItemViewDelegate is already registered for the viewType = "
                    + viewType
                    + ". Already registered ItemViewDelegate is "
                    + delegates[viewType])
        }
        delegates.put(viewType, delegate)
        return this
    }

    fun removeDelegate(delegate: ItemViewDelegate<T, ViewBinding>): MultiItemViewDelegateManager<T> {
        val indexToRemove = delegates.indexOfValue(delegate)
        if (indexToRemove >= 0) {
            delegates.removeAt(indexToRemove)
        }
        return this
    }

    fun removeDelegate(itemType: Int): MultiItemViewDelegateManager<T> {
        val indexToRemove = delegates.indexOfKey(itemType)
        if (indexToRemove >= 0) {
            delegates.removeAt(indexToRemove)
        }
        return this
    }

    fun getItemViewType(item: T, position: Int): Int {
        val delegatesCount = delegates.size()
        for (i in delegatesCount - 1 downTo 0) {
            val delegate = delegates.valueAt(i)
            if (delegate.isForViewType(item, position)) {
                return delegates.keyAt(i)
            }
        }
        throw IllegalArgumentException(
            "No ItemViewDelegate added that matches position=$position in data source"
        )
    }

    fun convert(holder: ViewHolder<ViewBinding>, item: T, position: Int) {
        val delegatesCount = delegates.size()
        for (i in 0 until delegatesCount) {
            val delegate= delegates.valueAt(i)
            if (delegate.isForViewType(item, position)) {
                delegate.convert(holder, item, position)
                return
            }
        }
        throw IllegalArgumentException(
            "No ItemViewDelegateManager added that matches position=$position in data source"
        )
    }


    fun getItemViewDelegate(viewType: Int): ItemViewDelegate<T, ViewBinding> {
        val delegate= delegates[viewType]
        checkNotNull(delegate){
             "viewType:$viewType does not have a delegate"
        }
        return delegate
    }

    fun getItemViewType(itemViewDelegate: ItemViewDelegate<T, ViewBinding>): Int {
        return delegates.indexOfValue(itemViewDelegate)
    }
}

/**
 * 单一类型布局委托接口
 */
interface ItemViewDelegate<T,V:ViewBinding>{
    var itemClickListener: OnItemClickListener?

    fun getItemViewBinding(parent: ViewGroup): V

    fun isForViewType(item: T, position: Int): Boolean

    fun convert(holder: ViewHolder<V>, item: T, position: Int)

    fun setItemClickListener(block: ItemClickListener.() -> Unit): OnItemClickListener {
        return object : ItemClickListener(){}.apply(block)
    }
}

/**
 * 多类型布局适配 RecyclerView
 */
abstract class BaseMultiItemRVAdapter<T>(private val dataList: MutableList<T> = mutableListOf()):
    RecyclerView.Adapter<ViewHolder<ViewBinding>>() {
    protected val multiItemManager = MultiItemViewDelegateManager<T>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ViewBinding> {
        val viewBinding = multiItemManager.getItemViewBindingByType(parent, viewType)
        val viewHolder = ViewHolder(viewBinding)
        multiItemManager.getItemClickListener(viewType)?.let {
            viewHolder.itemView.setOnClickListener{
                multiItemManager.getItemClickListener(viewType)?.onItemClick(
                    it,
                    viewHolder,
                    viewHolder.absoluteAdapterPosition
                )
            }
            viewHolder.itemView.setOnLongClickListener {
                return@setOnLongClickListener multiItemManager.getItemClickListener(viewType)?.onItemLongClick(
                    it,
                    viewHolder,
                    viewHolder.absoluteAdapterPosition
                )?:false
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder<ViewBinding>, position: Int) {
        multiItemManager.convert(holder, dataList[position], holder.absoluteAdapterPosition)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return multiItemManager.getItemViewType(dataList[position], position)
    }

    fun addItemViewDelegate(itemViewDelegate: ItemViewDelegate<T, ViewBinding>): BaseMultiItemRVAdapter<T> {
        multiItemManager.addDelegate(itemViewDelegate)
        return this
    }

    fun addItemViewDelegate(
        viewType: Int,
        itemViewDelegate: ItemViewDelegate<T, ViewBinding>
    ): BaseMultiItemRVAdapter<T> {
        multiItemManager.addDelegate(viewType, itemViewDelegate)
        return this
    }

}

abstract class BaseItemViewAdapter<T,V: ViewBinding>(dataList: MutableList<T>):
    BaseMultiItemRVAdapter<T>(dataList){
    var listener: OnItemClickListener? = null
    init {
        addItemViewDelegate(object : ItemViewDelegate<T, ViewBinding> {
            override var itemClickListener: OnItemClickListener? = listener
            override fun getItemViewBinding(parent: ViewGroup): V {
                  return getViewBinding()
            }

            override fun isForViewType(item: T, position: Int): Boolean {
                return true
            }

            override fun convert(holder: ViewHolder<ViewBinding>, item: T, position: Int) {
                holder as ViewHolder<V>
                convertView(holder,item,position)
            }

        })
    }
    fun setItemClickListener(listener: OnItemClickListener){
          this.listener = listener
    }
    abstract fun convertView(holder: ViewHolder<V>, item: T, position: Int)

    abstract fun getViewBinding():V

}
