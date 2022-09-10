package com.example.finalproject.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.finalproject.*
import com.example.finalproject.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_product_detail.view.*


class ProductDetailFragment : Fragment() {

    private val args by navArgs<ProductDetailFragmentArgs>()
    private lateinit var dbRef: DatabaseReference
    var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        var productImage: ImageView = view.findViewById(R.id.product_image)
        var productTitle: TextView = view.findViewById(R.id.product_title)
        var productPrice: TextView = view.findViewById(R.id.product_price)
        var productDescription: TextView = view.findViewById(R.id.description)

        productDescription.text =  args.currentProductDetail.description!!.replace("_b", "\n")
//        productDescription.text = text.replace("_b", "\n")
        Picasso.get().load(args.currentProductDetail.img).into(productImage)
        productTitle.text = args.currentProductDetail.title
        productPrice.text = args.currentProductDetail.price

        dbRef = FirebaseDatabase.getInstance().getReference("CartItems")
        view.cart_button.setOnClickListener {
            saveCartItems()
        }

        dbRef = FirebaseDatabase.getInstance().getReference("CartItems")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                if (snapshot.exists()) {
                    for (cartSnap in snapshot.children) {
                        val cartData = cartSnap.getValue(CartItem::class.java)
                        cartItems.add(cartData!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", "DatabaseError")
            }
        })
        return view
    }

    private fun saveCartItems() {
        val image = args.currentProductDetail.img
        val title= args.currentProductDetail.title
        val price = args.currentProductDetail.price
        val key = dbRef.push().key!!

        if(cartItems.size > 0) {
            var tempTitle = mutableListOf<String>()
            for (i in 0 until cartItems.size) {
                tempTitle.add(cartItems[i].title.toString())
//                Log.d("Title", tempTitle.toString())
            }
            var position = getItemPosition(tempTitle, title)
            if (position == -1) {
                val items = CartItem(key, image, title, price, price, "1")
                dbRef.child(key).setValue(items)
                    .addOnCompleteListener {
                        Toast.makeText(
                            context,
                            getString(R.string.item_added_successfully),
                            Toast.LENGTH_SHORT
                        ).show()

                    }.addOnFailureListener { err ->
                        Toast.makeText(context, "Error ${err.message}", Toast.LENGTH_SHORT).show()

                    }
            } else {
                var quantity = cartItems[position].quantity?.toInt()
                val price = cartItems[position].originalPrice.toString()
                val delimeter = " "
                val array = price.split(delimeter).toTypedArray()
                val initialPrice = array[1].toInt()

                if (quantity!! < 5) {
                    quantity += 1
//                    Log.d("Data", quantity.toString())
                    updateCartItem(cartItems[position].id!!, cartItems[position].img!!,cartItems[position].title!!, "₹ ${(initialPrice * quantity)}" ,cartItems[position].originalPrice!!, quantity.toString())
                } else {
                    Toast.makeText(context, getString(R.string.limit_exceed), Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            val items = CartItem(key, image, title, price, price, "1")
            dbRef.child(key).setValue(items)
                .addOnCompleteListener {
                    Toast.makeText(
                        context,
                        getString(R.string.item_added_successfully),
                        Toast.LENGTH_SHORT
                    ).show()

                }.addOnFailureListener { err ->
                    Toast.makeText(context, "Error ${err.message}", Toast.LENGTH_SHORT).show()

                }
        }
    }

    private fun updateCartItem(id: String, img: String, title:String, price: String, originalPrice: String, quantity: String) {
        val user = HashMap<String, Any>()
        user["img"] = img
        user["title"] = title
        user["price"]= price
        user["originalPrice"] = originalPrice
        user["quantity"] = quantity


        dbRef = FirebaseDatabase.getInstance().getReference("CartItems")
        dbRef.child(id).updateChildren(user).addOnCompleteListener{
            Toast.makeText(context, getString(R.string.quantity_added_successfully), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, getString(R.string.failed_to_add_quantity), Toast.LENGTH_SHORT).show()
        }

    }

    private fun getItemPosition(tempTitle: MutableList<String>, title: String): Int {
        for(i in 0 until tempTitle.size) {
            if (title == tempTitle[i]) {
                return i
            }
        }
        return -1
    }
}