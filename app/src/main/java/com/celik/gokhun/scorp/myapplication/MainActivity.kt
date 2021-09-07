package com.celik.gokhun.scorp.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    val numberList : MutableList<String> = ArrayList()
    var dataSource = DataSource()

    lateinit var adapter: NumberAdapter
    lateinit var  layoutManager: LinearLayoutManager

    private lateinit var  swipeRefresh : SwipeRefreshLayout
    private lateinit var  textView: TextView

    var idle : String = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager =layoutManager

        textView = findViewById(R.id.textView)

        swipeRefresh = findViewById(R.id.swipeToRefresh)

        swipeRefresh.setOnRefreshListener{
            if (idle == "null")
            {
                getPage()
            }else {getMore()}

        }

    }

    private fun getMore() {
        numberList.clear()

        dataSource.fetch(idle) { fetchResponse, fetchError ->

            if(fetchResponse != null)
            {
                try
                {
                    var peopleIdName = arrayOfNulls<String?>(fetchResponse.next.toString().toInt()-idle.toInt())
                    idle = fetchResponse.next.toString()


                    for (i in 0..peopleIdName.size-1)
                    {
                        peopleIdName[i] = fetchResponse.people[i].fullName + " (" + fetchResponse.people[i].id +")"
                    }

                    fillList(peopleIdName.distinct().toTypedArray())

                } catch (nfe : NumberFormatException) {reloadData()}

            }
            else {
                Toast.makeText(this, fetchError?.errorDescription.toString(),Toast.LENGTH_LONG).show()
                reloadData()}
        }

    }

    private fun getPage() {
        numberList.clear()
        textView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        dataSource.fetch(null) { fetchResponse, fetchError ->

            if(fetchResponse != null)
            {
                try
                {
                    var peopleIdName = arrayOfNulls<String?>(fetchResponse.next.toString().toInt())
                    idle = fetchResponse.next.toString()

                    for (i in 0..peopleIdName.size-1)
                    {
                        peopleIdName[i] = fetchResponse.people[i].fullName + " (" + fetchResponse.people[i].id +")"
                    }

                    fillList(peopleIdName.distinct().toTypedArray())

                } catch (nfe : NumberFormatException) {reloadData()}

            }
            else {
                Toast.makeText(this, fetchError?.errorDescription.toString(),Toast.LENGTH_LONG).show()
                reloadData()}
        }

    }

    private fun fillList(listOfPeople : Array<String?>) {

        numberList.clear()

        for (element in listOfPeople)
        {
            numberList.add(element.toString())
        }

        Handler().postDelayed({
            if (::adapter.isInitialized)
            {
                adapter.notifyDataSetChanged()
            }

            else
            {
                adapter = NumberAdapter(this)
                recyclerView.adapter = adapter
            }

            if (swipeRefresh.isRefreshing)
            {
                swipeRefresh.isRefreshing = false
            }

        },2000)

    }

    private fun reloadData()    {
        idle ="null"
        numberList.clear()

        if (swipeRefresh.isRefreshing)
        {
            swipeRefresh.isRefreshing = false
        }

        textView.visibility = View.VISIBLE
        textView.text = "Pull to meet new people!"
        recyclerView.visibility = View.GONE
    }

    class NumberAdapter(private val activity: MainActivity) : RecyclerView.Adapter<NumberAdapter.NumberViewHolder>(){

        class NumberViewHolder(v : View) : RecyclerView.ViewHolder(v) {
            val tvNumberAdapter =  v.findViewById<TextView>(R.id.tv_number)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            return NumberViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_number,parent,false) )
        }

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.tvNumberAdapter.text = activity.numberList[position]
        }

        override fun getItemCount(): Int {
            return activity.numberList.size

        }
    }

}