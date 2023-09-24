package com.example.cryptocurrency_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cryptocurrency_app.databinding.ActivityMainBinding
import java.util.Locale


//this code sets up the main activity of your cryptocurrency app, fetches data from a
//remote API, and allows users to search and filter the displayed data.

class MainActivity : AppCompatActivity() {

    private lateinit var binding :ActivityMainBinding
    private lateinit var rvAdapter:RvAdapter
    private lateinit var data:ArrayList<Modal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        data=ArrayList<Modal>()

        apiData

        rvAdapter=RvAdapter(this,data);
        binding.Rv.layoutManager=LinearLayoutManager(this)
        binding.Rv.adapter=rvAdapter
        //The adapter is responsible for binding the data to the individual views
        //  in the RecyclerView and handling user interactions with the items.

        binding.search.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val filterdata=ArrayList<Modal>()
                for(item in data)
                {
                    if(item.name.lowercase(Locale.getDefault()).contains(p0.toString().lowercase(
                            Locale.getDefault())))
                    {
                        filterdata.add(item);
                    }

                }
                if(filterdata.isEmpty())
                {
                    Toast.makeText(this@MainActivity,"No data Available!",Toast.LENGTH_SHORT).show()

                }
                else
                {
                    rvAdapter.changeData(filterdata)
                }
            }
        })
    }
//    the above  code sets up a text input listener on an EditText widget. When the user enters text, it filters the
//    data based on the input and updates the displayed data in a RecyclerView using a custom adapter (rvAdapter).
//    The changeData() method in the adapter is expected to handle updating the data displayed in the RecyclerView.


    val  apiData:Unit get() { // Unit in kotlin is similar to void
        val url="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"
        val queue= Volley.newRequestQueue(this)
//Volley is a Android Library particularly well-suited for scenarios where you need to make network requests,
// such as fetching data from a web API or downloading images.Nowadays its replaced by
// newer networking libraries like Retrofit and OkHttp

        val jsonObjectRequest: JsonObjectRequest=

            object:JsonObjectRequest(Method.GET,url,null,
                Response.Listener {
                        response ->
                    binding.progressBar.isVisible=false
                    try {
                        val dataArray=response.getJSONArray("data")
                        for(i in 0 until dataArray.length())
                        {
                            val dataObject=dataArray.getJSONObject(i)
                            val symbol=dataObject.getString("symbol")
                            val name=dataObject.getString("name")
                            val quote=dataObject.getJSONObject("quote")
                            val USD=quote.getJSONObject("USD")
                            val price = String.format("$ "+"%.2f",USD.getDouble("price"))
                            data.add(Modal(name,symbol,price))
                        }
                        rvAdapter.notifyDataSetChanged()
                    }
                    catch (e:Exception)
                    {
                        Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                    }


                },Response.ErrorListener {
                    Toast.makeText(this,"Error1",Toast.LENGTH_LONG).show() }
            ) {
                override fun getHeaders(): Map<String,String> {

                    val headers= HashMap<String,String>();
                    headers["X-CMC_PRO_API_KEY"]="5e8a9dfb-4df0-4c3d-95df-3f25e76809e0"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
}