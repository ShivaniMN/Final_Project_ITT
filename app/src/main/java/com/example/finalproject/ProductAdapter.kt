package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.views.ShopFragmentDirections
import com.squareup.picasso.Picasso

class ProductAdapter(private var productList: MutableList<Product>):RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    private lateinit var  mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    class ProductViewHolder(view : View, listener: onItemClickListener):RecyclerView.ViewHolder(view) {
        var productImage: ImageView = view.findViewById(R.id.product_image)
        var productTitle: TextView = view.findViewById(R.id.product_title)
        var productPrice: TextView = view.findViewById(R.id.product_price)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
            val layoutView: View = LayoutInflater.from(parent.context).inflate(R.layout.product_card_view,parent,false)
            return ProductViewHolder(layoutView, mListener)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        Picasso.get().load(productList[position].img).into(holder.productImage)
        holder.productTitle.text = productList[position].title
        holder.productPrice.text = productList[position].price
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}


