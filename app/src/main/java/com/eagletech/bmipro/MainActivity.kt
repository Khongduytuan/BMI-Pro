package com.eagletech.bmipro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eagletech.bmipro.data.ManagerData
import com.eagletech.bmipro.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myData: ManagerData
    private var height: Int = 160
    private var weight: Float = 60f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myData = ManagerData.getInstance(this)

        // Thiết lập Toolbar
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        getHeightAndWeight()
        clickApp()
    }

    private fun getHeightAndWeight() {
        binding.seekbarHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                height = progress
                binding.tvHeight.text = height.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        binding.seekbarWeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weight = progress.toFloat()
                binding.tvWeight.text = weight.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun clickApp() {
        binding.btnBmi.setOnClickListener {
            if (myData.isPremium == true){
                setBMI()
            } else if(myData.getData() > 0){
                setBMI()
                myData.removeData()
            } else{
                Toast.makeText(this, "Your turn has expired", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BuyyyActivity::class.java)
                startActivity(intent)
            }
            
        }
    }

    private fun setBMI() {
        if (height != 0 && weight != 0f){
            val bmi = calculateBMI(height, weight)
            val bmiStatus = getBMIStatus(bmi)
            val heightStatus = getHeightStatus(height)
            val weightStatus = getWeightStatus(weight)
            binding.tvResult.text = bmiStatus
            binding.tvHeightInfo.text = heightStatus
            binding.tvWeightInfo.text = weightStatus
            binding.tvBmi.text = bmi.toString()
            binding.tvHeightInfoResult.text = height.toString()
            binding.tvWeightInfoResult.text = weight.toString()
            binding.circularProgressBar.apply {
                progressMax = 100f
                setProgressWithAnimation(bmi, 1500)
            }
        }
    }

    private fun calculateBMI(height: Int, weight: Float): Float {
        val heightF = (height/100).toFloat()
        return weight / (heightF * heightF)
    }



    private fun getBMIStatus(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Fat"
        }
    }
    private fun getHeightStatus(height: Int): String {
        return when {
            height < 160 -> "Short"
            height in 160..200 -> "Normal"
            else -> "Great"
        }
    }
    private fun getWeightStatus(weight:  Float): String {
        return when {
            weight < 60f -> "slight"
            weight in 60f..90f -> "Normal"
            else -> "Fat"
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_buy -> {
                val intent = Intent(this, BuyyyActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.menu_info -> {
                showInfoDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_info, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.tvMessage)
        val positiveButton = dialogView.findViewById<Button>(R.id.btnPositive)

        if (myData.isPremium == true) {
            messageTextView.text = "You have successfully registered"
        } else {
            messageTextView.text = "You have ${myData.getData()} use"
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        positiveButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}