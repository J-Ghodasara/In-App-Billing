package com.jayghodasara.inappbilling

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {

    lateinit var billingClient:BillingClient
    var hashMap:HashMap<String,String> = HashMap()
    var hashMap2:HashMap<String,String> = HashMap()

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {


                billingClient.consumeAsync(purchase.purchaseToken) { responseCode, purchaseToken -> Toast.makeText(this,"Consumed", Toast.LENGTH_LONG).show() }
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
        } else {

        }
    }

    var list:ArrayList<String> = ArrayList()


    fun queryskudetails(){

        billingClient=BillingClient.newBuilder(this).setListener(this).build()
        billingClient.startConnection(object: BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.i("Disconnected","billing client")
            }

            override fun onBillingSetupFinished(responseCode: Int) {

                billingClient.let { billingClient ->

                    val skulist = ArrayList<String>()
                    skulist.add("subscribe")

                    val params= SkuDetailsParams.newBuilder()
                    params.setSkusList(skulist).setType(BillingClient.SkuType.SUBS)
                    billingClient.querySkuDetailsAsync(params.build(),{
                        responseCode, skuDetailsList ->

                        if(responseCode== BillingClient.BillingResponse.OK && skuDetailsList !=null){
                            for (skuDetails in skuDetailsList) {
                                val sku = skuDetails.sku
                                val price = skuDetails.price
                                Log.i("skudetails",sku)
                                Log.i("skuprice",price)
                                hashMap[sku] = price


                            }
                        }

                    })
                }


            }

        })
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.add("Books")
        list.add("Pens")
        list.add("Keychains")
        list.add("Mobiles")
        queryskudetails()
        subscribe.setOnClickListener(View.OnClickListener {



            val flowParams = BillingFlowParams.newBuilder()
                    .setSku("subscribe")
                    .setType(BillingClient.SkuType.SUBS) // SkuType.SUB for subscription

                    .build()
            val responseCode = billingClient.launchBillingFlow(this, flowParams)
        })

        var layman:RecyclerView.LayoutManager= LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        var adapter=myadapter(this,list)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=layman


    }
}
