package com.jayghodasara.inappbilling

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.util.BillingHelper

class myadapter(var context: Context, var array: ArrayList<String>) : RecyclerView.Adapter<myadapter.Vholder>(), PurchasesUpdatedListener {

    lateinit var billingClient: BillingClient
    var hashMap: HashMap<String, String> = HashMap()
    var hashMap2: HashMap<String, String> = HashMap()

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {
                // handlePurchase(purchase)

                billingClient.consumeAsync(purchase.purchaseToken) { responseCode, purchaseToken -> Toast.makeText(context, "Consumed", Toast.LENGTH_LONG).show() }
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        } else {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vholder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false)





        return Vholder(v)
    }

    fun queryskudetails() {

        billingClient = BillingClient.newBuilder(context).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.i("Disconnected", "billing client")
            }

            override fun onBillingSetupFinished(responseCode: Int) {

                billingClient.let { billingClient ->

                    val skulist = ArrayList<String>()
                    skulist.add("books")
                    skulist.add("pens")
                    skulist.add("keychains")

                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skulist).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build(), { responseCode, skuDetailsList ->

                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            for (skuDetails in skuDetailsList) {
                                val sku = skuDetails.sku
                                val price = skuDetails.price
                                Log.i("skudetails", sku)
                                Log.i("skuprice", price)
                                hashMap[sku] = price


                            }
                        }

                    })
                }


            }

        })
    }


    override fun getItemCount(): Int {
        return array.size
    }


    override fun onBindViewHolder(holder: Vholder, position: Int) {
        queryskudetails()

        var text: String = array[position]
        Log.i("text", text)
        holder.textView.text = text
        holder.Price.text = hashMap[text.toLowerCase()].toString()
        Log.i("price", hashMap["books"].toString())
        holder.btn.setOnClickListener(View.OnClickListener {
            Log.i("button", text.toLowerCase())
            var skuid = hashMap2[text]
            val flowParams = BillingFlowParams.newBuilder()
                    .setSku(text.toLowerCase())
                    .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription

                    .build()
            val responseCode = billingClient.launchBillingFlow(context as Activity?, flowParams)


        })
    }


    inner class Vholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textView: TextView = itemView.findViewById(R.id.textView1)
        var btn: ImageView = itemView.findViewById(R.id.add_to_cart)
        var Price: TextView = itemView.findViewById(R.id.price)

    }
}