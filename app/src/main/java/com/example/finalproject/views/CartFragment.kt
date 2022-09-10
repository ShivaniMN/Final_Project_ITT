package com.example.finalproject.views

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.*
import com.example.finalproject.R
import com.google.firebase.database.*
import com.google.firebase.database.core.Context
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlin.math.truncate


class CartFragment : Fragment() {

    var cartList: MutableList<CartItem> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference

    var itemCount = 1
    var minValue = 1
    var maxValue = 4
    var changedPrice = 0
    var initialPrice = 0
    var flag = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.rvCart)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        getCartItem()
        swipeToDelete()

        view.orderBtn.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("CartItems")
            dbRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnap in snapshot.children) {
                        snapshot.ref.removeValue();
                    }
                    setVisibility(cartList.size)
                    val cartAdapter = CartAdapter(cartList)
                    recyclerView.adapter = cartAdapter

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", "DatabaseError")
                }
            })
            val action = CartFragmentDirections.actionCartFragment2ToOrderFragment2()
            findNavController().navigate(action)
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun getCartItem() {
        val dbRef = FirebaseDatabase.getInstance().getReference("CartItems")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                cartList.clear()
                if(snapshot.exists()){
                    for(cartSnap in snapshot.children){
                        val cartData = cartSnap.getValue(CartItem::class.java)
                        cartList.add(cartData!!)
                    }
                    setVisibility(cartList.size)
                    val cartAdapter = CartAdapter(cartList)
                    recyclerView.adapter = cartAdapter


                    cartAdapter.setOnItemClickListener(object : CartAdapter.ItemClickListener{
                        override fun minus(cartItem: CartItem, position: Int) {
                            itemCount = cartList[position].quantity!!.toInt()
                            if (itemCount > minValue){
                                itemCount -= 1
                                cartList[position].quantity = itemCount.toString()
                                priceChange(itemCount,position)
                                getTotalPrice()
                                cartAdapter.notifyDataSetChanged()
                            }
                        }
                        override fun plus(cartItem: CartItem, position: Int) {
                            itemCount = cartList[position].quantity!!.toInt()
                            if(itemCount <= maxValue){
                                itemCount += 1
                                cartList[position].quantity = itemCount.toString()
                                priceChange(itemCount, position)
                                getTotalPrice()
                                cartAdapter.notifyDataSetChanged()
                            }
                        }
                    })
                    getTotalPrice()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun swipeToDelete() {
        val cartAdapter = CartAdapter(cartList)

        val swipeGesture = object : SwipeGesture(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                cartAdapter.deleteItem(viewHolder.adapterPosition)
                setVisibility(cartList.size)
                getTotalPrice()
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setVisibility(size: Int) {
        flag = size
        if(flag == 0){
            view?.rvCart?.visibility = View.INVISIBLE
            view?.totalOrder?.visibility = View.INVISIBLE
            view?.orderBtn?.visibility = View.INVISIBLE
            view?.message?.visibility = View.VISIBLE
        }else{
            view?.totalOrder?.visibility = View.VISIBLE
            view?.orderBtn?.visibility = View.VISIBLE
            view?.rvCart?.visibility = View.VISIBLE
            view?.message?.visibility = View.INVISIBLE
        }
    }


    private fun getTotalPrice() {
        var totalCartPrice = view?.findViewById<TextView>(R.id.totalOrder)
        var totalPrice = 0
        val size = cartList.size
        for (i in 0 until size){
            val splitPrice = cartList[i].price
            val delimeter = " "
            val array = splitPrice?.split(delimeter)?.toTypedArray()
            totalPrice += array?.get(1)!!.toInt()
        }
        totalCartPrice?.text= "Total Price ₹ $totalPrice"
    }


    private fun priceChange(itemCount: Int, position: Int) {
        val originalPrice = cartList[position].originalPrice.toString()
        val delimeter = " "
        val array = originalPrice.split(delimeter).toTypedArray()
        initialPrice = array[1].toInt()
        changedPrice = initialPrice * itemCount
        updateCart(cartList[position].id!!, cartList[position].img!!, cartList[position].title!!, "₹ $changedPrice", cartList[position].originalPrice!!, itemCount.toString())
        cartList[position].price = "₹ $changedPrice"

    }

    private fun updateCart(id: String, img: String, title: String, price: String, originalPrice: String, quantity: String) {
        val item = HashMap<String, Any>()
        item["img"] = img
        item["title"] = title
        item["price"]= price
        item["originalPrice"] = originalPrice
        item["quantity"] = quantity

        dbRef = FirebaseDatabase.getInstance().getReference("CartItems")
        dbRef.child(id).updateChildren(item).addOnCompleteListener{
            Toast.makeText(context, getString(R.string.quantity_changed_successfully), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, getString(R.string.failed_to_add_quantity), Toast.LENGTH_SHORT).show()
        }
    }
}

