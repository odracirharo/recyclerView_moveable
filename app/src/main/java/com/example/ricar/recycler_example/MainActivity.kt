package com.example.ricar.recycler_example

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val exampleRecycler = findViewById<RecyclerView>(R.id.example_recycler)
        exampleRecycler.layoutManager = LinearLayoutManager(this)


        val exampleAdapter = ExampleAdapter(this, getExampleData())
        exampleRecycler.adapter = exampleAdapter

        exampleAdapter.setClickListener(object: ExampleAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.d("Hello", "Clicked Position: $position")

                if (exampleAdapter.editModeEnabled)
                {
                    exampleAdapter.items[position].isHighlighted = !exampleAdapter.items[position].isHighlighted
                    exampleAdapter.items[position].isChecked = !exampleAdapter.items[position].isChecked
                    exampleAdapter.notifyDataSetChanged()
                }
            }

            override fun onItemLongClick(view: View, position: Int) {
                Log.d("Hello", "Long Clicked Position: $position")
                if (!exampleAdapter.editModeEnabled)
                {
                    exampleAdapter.items[position].isHighlighted = true
                    exampleAdapter.items[position].isChecked = true
                    exampleAdapter.editModeEnabled = true
                    exampleAdapter.notifyDataSetChanged()
                }
            }

            override fun onItemCheckboxClick(view: View, position: Int, isChecked: Boolean) {
                super.onItemCheckboxClick(view, position, isChecked)
                exampleAdapter.items[position].isHighlighted = isChecked
                exampleAdapter.items[position].isChecked = isChecked
                exampleAdapter.notifyItemChanged(position)
            }
        })

        // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
        val touchHelper = ItemTouchHelper(object: ItemTouchHelper.Callback() {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                // get the viewHolder's and target's positions in your adapter data, swap them
                if (recyclerView!= null) {
                    val customAdapter = recyclerView.adapter as ExampleAdapter

                    if (customAdapter.editModeEnabled && viewHolder != null && target != null) {
                        Collections.swap(customAdapter.items, viewHolder.adapterPosition, target.adapterPosition)
                        recyclerView.adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                        return true
                    }
                }
                return false
            }

            override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT or ItemTouchHelper.DOWN or ItemTouchHelper.UP)
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }
        })
        touchHelper.attachToRecyclerView(exampleRecycler)
    }

    private class ExampleAdapter(val context: Context, val items: List<ExampleRecyclerDataObject>): RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>() {

        private var clickListener: ItemClickListener? = null
        var editModeEnabled: Boolean = false

        override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
            holder.bindView(items[position])
        }

        override fun getItemCount(): Int {
            return items.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ExampleViewHolder {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.example_recycler_cell, parent, false)
            return  ExampleViewHolder(view)
        }

        interface ItemClickListener {
            fun onItemClick(view: View, position: Int)
            fun onItemLongClick(view: View, position: Int) {}
            fun onItemCheckboxClick(view: View, position: Int, isChecked: Boolean) {}
        }

        fun setClickListener(itemClickListener: ItemClickListener)
        {
            clickListener = itemClickListener
        }

        inner class ExampleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

            private var title = itemView.findViewById<TextView>(R.id.example_title)
            private var checkbox = itemView.findViewById<CheckBox>(R.id.example_checkbox)

            init
            {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
                checkbox.setOnCheckedChangeListener(this)
            }

            override fun onClick(v: View?) {
                if (clickListener != null)
                {
                    clickListener!!.onItemClick(itemView, adapterPosition)
                }
            }

            override fun onLongClick(view: View): Boolean {
                if (clickListener != null)
                {
                    clickListener!!.onItemLongClick(itemView, adapterPosition)
                    return true
                }
                return true
            }

            override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
                if (clickListener != null)
                {
                    clickListener!!.onItemCheckboxClick(itemView, adapterPosition, isChecked)
                }
            }
            
            fun bindView(data: ExampleRecyclerDataObject)
            {
                this.title.text = data.title

                //We do not want to notify the listener of this binding checkbox check or we would create an infinite loop
                //Lazy solution, should be a better way
                val tempListener = clickListener
                clickListener = null
                this.checkbox.isChecked = data.isChecked
                clickListener = tempListener

                if (data.isHighlighted)
                    itemView.setBackgroundColor(Color.parseColor("#AAAAAA"))
                else
                    itemView.setBackgroundColor(Color.parseColor("#DDDDDD"))

                if (editModeEnabled)
                {
                    checkbox.visibility = View.VISIBLE
                }
                else
                {
                    checkbox.visibility = View.GONE
                }
            }

        }
    }

    private data class ExampleRecyclerDataObject(var title: String)
    {
        var isChecked: Boolean = false
        var isHighlighted: Boolean = false
    }

    private fun getExampleData() : List<ExampleRecyclerDataObject> {
        val exampleData = object : ArrayList<ExampleRecyclerDataObject>() {
            init {
                add(ExampleRecyclerDataObject("String A"))
                add(ExampleRecyclerDataObject("String B"))
                add(ExampleRecyclerDataObject("String C"))
                add(ExampleRecyclerDataObject("String D"))
                add(ExampleRecyclerDataObject("String E"))
                add(ExampleRecyclerDataObject("String F"))
                add(ExampleRecyclerDataObject("String G"))
                add(ExampleRecyclerDataObject("String H"))
                add(ExampleRecyclerDataObject("String I"))
                add(ExampleRecyclerDataObject("String J"))
                add(ExampleRecyclerDataObject("String K"))
                add(ExampleRecyclerDataObject("String L"))
                add(ExampleRecyclerDataObject("String T"))
                add(ExampleRecyclerDataObject("String V"))
            }
        }
        return exampleData
    }

}
