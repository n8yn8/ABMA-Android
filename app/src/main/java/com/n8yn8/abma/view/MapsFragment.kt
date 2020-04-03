package com.n8yn8.abma.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.n8yn8.abma.R
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.view.adapter.MapsAdapter
import com.n8yn8.abma.view.adapter.MapsAdapter.OnMapClickListener

class MapsFragment : Fragment() {

    lateinit var adapter: MapsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        val listView: RecyclerView = view.findViewById(R.id.mapsListView)
        adapter = MapsAdapter(OnMapClickListener { map -> MapDetailActivity.start(context, map) })
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context!!.applicationContext)
        listView.layoutManager = mLayoutManager
        listView.itemAnimator = DefaultItemAnimator()
        listView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.year.observe(viewLifecycleOwner, Observer { year ->
            val db = AppDatabase.getInstance(requireActivity().applicationContext)
            db.mapDao().getMaps(year.objectId).observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }
}