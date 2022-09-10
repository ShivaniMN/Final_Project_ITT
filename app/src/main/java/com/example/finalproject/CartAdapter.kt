package com.example.finalproject

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.views.CartFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Context
import com.squareup.picasso.Picasso

class CartAdapter(private var cartList: MutableList<CartItem>): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    lateinit var  cListener: ItemClickListener
    private lateinit var dbRef: DatabaseReference


    interface  ItemClickListener{
        fun minus(cartItem: CartItem, position: Int)
        fun plus(cartItem: CartItem, position: Int)
    }


    fun setOnItemClickListener(listener: ItemClickListener){
        cListener = listener
    }

    fun deleteItem(position: Int){

        val dbRef = FirebaseDatabase.getInstance().getReference("CartItems").child(cartList[position].id!!)
        cartList.removeAt(position)
        notifyItemRemoved(position)
        val mTask = dbRef.removeValue()
        mTask.addOnSuccessListener {
            Log.e("Data","Cart item deleted")
        }
    }


    class CartViewHolder(view: View, listener: ItemClickListener) :RecyclerView.ViewHolder(view){
        var productImage: ImageView = view.findViewById(R.id.product_image)
        var productTitle: TextView = view.findViewById(R.id.product_title)
        var productPrice: TextView = view.findViewById(R.id.productTotalPrice)
        var countText: TextView = view.findViewById(R.id.itemCount)
        var addButton: Button = view.findViewById(R.id.plus)
        var minusButton: Button = view.findViewById(R.id.minus)

        init {
            itemView.setOnClickListener {
                listener.minus(CartItem(), position)
                listener.plus(CartItem(), position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val layoutView: View = LayoutInflater.from(parent.context).inflate(R.layout.cart_row,parent,false)
        return CartViewHolder(layoutView, cListener)
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        Picasso.get().load(cartList[position].img).into(holder.productImage)
        holder.productTitle.text = cartList[position].title
        holder.productPrice.text = cartList[position].price
        holder.countText.text = cartList[position].quantity.toString()
        holder.addButton.setOnClickListener {
            cListener.plus(cartList[position], position)
        }
        holder.minusButton.setOnClickListener {
            cListener.minus(cartList[position], position)
        }
        Log.e("Cart", cartList.size.toString())
    }


    override fun getItemCount(): Int {
        return cartList.size
    }
}