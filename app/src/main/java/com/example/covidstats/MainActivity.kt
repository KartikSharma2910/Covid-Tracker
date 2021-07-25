package com.example.covidstats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    lateinit var worldCasesTV:TextView
    lateinit var worldRecoveredTV:TextView
    lateinit var worldDeathsTV:TextView
    lateinit var countryCasesTV:TextView
    lateinit var countryRecoveredTV:TextView
    lateinit var countryDeathsTV:TextView
    lateinit var stateRV:RecyclerView
    lateinit var stateRVAdapter:StateRVAdapter
    lateinit var stateList:List<StateModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        worldCasesTV=findViewById(R.id.idTVWorldCases)
        worldRecoveredTV=findViewById(R.id.idTVWorldRecovered)
        worldDeathsTV=findViewById(R.id.idTVWorldDeath)
        countryCasesTV=findViewById(R.id.idTVIndiaCases)
        countryDeathsTV=findViewById(R.id.idTVIndiaDeath)
        countryRecoveredTV=findViewById(R.id.idTVIndiaRecovered)

        stateRV=findViewById(R.id.idRVStates)
        stateList=ArrayList<StateModel>()

        getStateInfo()
        getWorldInfo()

    }
    private fun getStateInfo(){
        val url="https://api.rootnet.in/covid19-in/stats/latest"
        val queue=Volley.newRequestQueue(this@MainActivity)

        val request=
            JsonObjectRequest(Request.Method.GET,url,null,{response->
                    try {

                        val dataObj=response.getJSONObject("data")
                        val summaryObj=dataObj.getJSONObject("summary")
                        val cases:Int=summaryObj.getInt("total")
                        val recovered:Int=summaryObj.getInt("discharged")
                        val deaths:Int=summaryObj.getInt("deaths")

                        countryCasesTV.text=cases.toString()
                        countryRecoveredTV.text=cases.toString()
                        countryDeathsTV.text=cases.toString()

                        val regionalArray=dataObj.getJSONArray("regional")
                        for(i in 0 until regionalArray.length()){
                            val regionalObj=regionalArray.getJSONObject(i)
                            val stateName:String=regionalObj.getString("loc")
                            val cases:Int=regionalObj.getInt("totalConfirmed")
                            val recovered:Int=regionalObj.getInt("discharged")
                            val deaths:Int=regionalObj.getInt("deaths")

                            val stateModel=StateModel(stateName,recovered,deaths,cases)
                            stateList=stateList+stateModel
                        }
                        stateRVAdapter= StateRVAdapter(stateList)
                        stateRV.layoutManager=LinearLayoutManager(this)
                        stateRV.adapter=stateRVAdapter

                    }catch (e:JSONException){
                        e.printStackTrace()
                    }


            },
                { error->
                    {
                        Toast.makeText(this,"Fail to get Data",Toast.LENGTH_LONG).show()
                    }

                })
        queue.add(request)
    }

    private fun getWorldInfo(){
        val url="https://corona.lmao.ninja/v3/covid-19/all"
        val queue=Volley.newRequestQueue(this@MainActivity)
        val request=
            JsonObjectRequest(Request.Method.GET,url,null,{ response->
                try {
                    val worldCases:Int=response.getInt("cases")
                    val worldRecovered:Int=response.getInt("recovered")
                    val worldDeaths:Int=response.getInt("deaths")
                    worldRecoveredTV.text=worldRecovered.toString()
                    worldCasesTV.text=worldCases.toString()
                    worldDeathsTV.text=worldDeaths.toString()
                }catch (e:JSONException){
                    e.printStackTrace()
                }

            },{
                error->
                {
                    Toast.makeText(this,"Fail to get Data",Toast.LENGTH_LONG).show()
                }
            })

        queue.add(request)
    }
}