package com.example.finalproject.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Product
import com.example.finalproject.ProductAdapter
import com.example.finalproject.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_shop.*

class ShopFragment : Fragment() {

    var productList = ArrayList<Product>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_shop, container, false)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("product")

        val firebaseListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val child = snapshot.children
                child.forEach{
                    var products = Product(
                        it.child("description").value.toString(),
                        it.child("img").value.toString(),
                        it.child("name").value.toString(),
                        it.child("price").value.toString())
                    productList.add(products)
                }
                productAdapter = ProductAdapter(productList)
                recyclerView.adapter = productAdapter
                productAdapter.setOnItemClickListener(object : ProductAdapter.onItemClickListener{
                    override fun onItemClick(position: Int) {
                        val description = productList[position].description.toString()
                        val image = productList[position].img
                        val title = productList[position].title
                        val price = productList[position].price
                        val details = Product(description,image, title, price)
                        val action = ShopFragmentDirections.actionShopFragment2ToProductDetailFragment2(details)
                        findNavController().navigate(action)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("test", error.message)
            }

        }
        reference.addValueEventListener(firebaseListener)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context,2)
        return view
    }


}