package com.plantquiz.plantquiz.Controller

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.plantquiz.plantquiz.Model.DownloadingObject
import com.plantquiz.plantquiz.Model.ParsePlantUtility
import com.plantquiz.plantquiz.Model.Plant
import com.plantquiz.plantquiz.R

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.util.TypedValue
import android.util.DisplayMetrics
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo


class MainActivity : AppCompatActivity() {

    private var cameraButton: Button? = null
    private var photoGalleryButton: Button? = null
    private var imageTaken: ImageView? = null
    //  val OPEN_CAMERA_BUTTON_REQUEST_ID = 10
    //   val OPEN_PHOTO_GALLERY_BUTTON_REQUEST_ID = 20

    //Instance variable

    var correctAnswerIndex: Int = 0
    var correctPlant: Plant? = null

    var numberOfTimesUserAnsweredCorrectly: Int = 0
    var numberOfTimesUserAnsweredInCorrectly: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        setProgressBar(false)

        displayUIWidgets(false)


        YoYo.with(Techniques.Pulse)
            .duration(700)
            .repeat(1)
            .playOn(btnNextPlant)


//        Toast.makeText(this, "The oncreate method is called", Toast.LENGTH_SHORT).show()
//
//        val myPlant: Plant = Plant("", "", "", "","",
//                 "", 0, 0)
//        //Plant("Koelreuteria", "paniculata", "", "Golden Rain Tree","Koelreuteria_paniculata_branch.JPG",
//         //   "Branch of Koelreuteria paniculata", 3, 24)
//
//        myPlant.plantName = "Wadas Memory Magnolia"
//        var nameOfPlant = myPlant.plantName


     /*   var flower = Plant()
        var tree = Plant()

        var collectionOfPlants: ArrayList<Plant> = ArrayList()
        collectionOfPlants.add(flower)
        collectionOfPlants.add(tree) */

       cameraButton = findViewById<Button>(R.id.btnOpenCamara)
        photoGalleryButton = findViewById<Button>(R.id.OpenPhotoGallery)



        imageTaken = findViewById<ImageView>(R.id.imgTaken)


//Opening the camera Intent
        cameraButton?.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "The Camara button  is clicked",
                Toast.LENGTH_SHORT).show()
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 1)})
//Opening the Gallery Intent
        photoGalleryButton?.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "The photo gallery button is clicked",
                Toast.LENGTH_SHORT).show()
            val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent,2)

        })
            //See the next Plant

        btnNextPlant.setOnClickListener(View.OnClickListener {

            if(checkForInternetConnection()) {

                setProgressBar(true)

               try{
                   val innerClassObject = DownloadingPlantTask()
                   innerClassObject.execute()
               }catch (e: Exception){
                   e.printStackTrace()
               }
//              /*  button1.setBackgroundColor(Color.LTGRAY)
//                button2.setBackgroundColor(Color.LTGRAY)
//                button3.setBackgroundColor(Color.LTGRAY)
//                button4.setBackgroundColor(Color.LTGRAY) */


                var gradientColor: IntArray = IntArray(2)
                gradientColor.set(0, Color.parseColor("#90150517"))
                gradientColor.set(1, Color.parseColor("#333945"))
                var gradientDrawable: GradientDrawable =
                    GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientColor)
                var convertedDipValue = dipToFloat(this@MainActivity, 25f)
                gradientDrawable.setCornerRadius(convertedDipValue)
                gradientDrawable.setStroke(5,Color.parseColor("#2C3335"))

                button1.setBackground(gradientDrawable)
                button2.setBackground(gradientDrawable)
                button3.setBackground(gradientDrawable)
                button4.setBackground(gradientDrawable)



            }
        })



    }

    fun dipToFloat(context: Context, dipValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }


//  Keeping Image taken
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val imageData = data?.getExtras()?.get("data") as Bitmap
                imageTaken?.setImageBitmap(imageData)
            }
        }

        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){

                val contentURI = data?.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                imgTaken.setImageBitmap(bitmap)
            }
        }
    }
    fun button1isClicked(buttonView: View){
      specifyTheRightAndWrongAnswer(0)
  }

    fun button2isclicked(buttonView: View){
        //Toast.makeText(this, "The button 2 is clicked", Toast.LENGTH_SHORT).show()

        specifyTheRightAndWrongAnswer(1)



    }

    fun button3isclicked(buttonView: View){
        //Toast.makeText(this, "The button 3 is clicked", Toast.LENGTH_SHORT).show()

        specifyTheRightAndWrongAnswer(2)

    }

    fun button4isclicked(buttonView: View){
        //Toast.makeText(this, "The button 4 is clicked ", Toast.LENGTH_SHORT).show()
        specifyTheRightAndWrongAnswer(3)

    }

    inner class DownloadingPlantTask: AsyncTask<String, Int, List<Plant>>(){

        override fun doInBackground(vararg params: String?): List<Plant>? {
            //can access background thread. Not user interface thread
            //Downloading the JSON data
//            val downloadingObject: DownloadingObject =
//                DownloadingObject()
//            val jsonData =  downloadingObject.downloadJSONDataFromLink(
//                "http://plantplaces.com/perl/mobile/flashcard.pl")
//            Log.i("JSON", jsonData)


            val parsePlant = ParsePlantUtility()

            return parsePlant.parsePlantObjectsFromJSONData()

        }

        override fun onPostExecute(result: List<Plant>?) {
            super.onPostExecute(result)

            //Can access user Interface thread. Not background thread
          var numberOfPlants = result?.size ?: 0

            if (numberOfPlants > 0){
                var randomPlantIndexForButton1: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton2: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton3: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton4: Int = (Math.random() * result!!.size).toInt()


                var allRandomPlants = ArrayList<Plant>()
                allRandomPlants.add(result.get(randomPlantIndexForButton1))
                allRandomPlants.add(result.get(randomPlantIndexForButton2))
                allRandomPlants.add(result.get(randomPlantIndexForButton3))
                allRandomPlants.add(result.get(randomPlantIndexForButton4))

                button1.text = result?.get(randomPlantIndexForButton1).toString()
                button2.text = result?.get(randomPlantIndexForButton2).toString()
                button3.text = result?.get(randomPlantIndexForButton3).toString()
                button4.text = result?.get(randomPlantIndexForButton4).toString()

                correctAnswerIndex = (Math.random() * allRandomPlants.size).toInt()
                correctPlant = allRandomPlants.get(correctAnswerIndex)

                val downloadingImageTask = DownloadingImageTask()
                downloadingImageTask.execute(allRandomPlants.get(correctAnswerIndex).pictureName)




            }
        }
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "The onStart method is called", Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
        super.onResume()
       // Toast.makeText(this, "The onResume method is called", Toast.LENGTH_SHORT).show()
        checkForInternetConnection()

    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(this, "The onPause method is called", Toast.LENGTH_SHORT).show()

    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(this, "The onStop method is called", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "The onDestroy method is called", Toast.LENGTH_SHORT).show()

    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(this, "The onRestart method is called", Toast.LENGTH_SHORT).show()

    }

/*
    fun imageViewisClicked(view: View){

        val randonNumber: Int =  (Math.random() * 6 ).toInt() + 1
        Log.i("TAG", "The Random Number is: $randonNumber")
//Example for else if Ladder
        if(randonNumber == 1){
            btnOpenCamara.setBackgroundColor(Color.GREEN)

        }else if (randonNumber == 2){
            OpenPhotoGallery.setBackgroundColor(Color.MAGENTA)
        }
        else if (randonNumber == 3){
            //button1.setBackgroundColor(Color.BLUE)
        }
        else if(randonNumber == 4){
            button2.setBackgroundColor(Color.GRAY)
        }
        else if(randonNumber == 5){
            button3.setBackgroundColor(Color.MAGENTA)
        }
        else {
            button4.setBackgroundColor(Color.BLUE)
        }


        val randonNumber: Int =  (Math.random() * 6 ).toInt() + 1
        Log.i("TAG", "The Random Number is: $randonNumber")
//Example for when statement
        when(randonNumber){
            1 -> btnOpenCamara.setBackgroundColor(Color.MAGENTA)
            2 -> OpenPhotoGallery.setBackgroundColor(Color.BLUE)
            3 -> button1.setBackgroundColor(Color.CYAN)
            4 -> button2.setBackgroundColor(Color.DKGRAY)
            5 -> button3.setBackgroundColor(Color.YELLOW)
            6 -> button4.setBackgroundColor(Color.BLACK)
        }
    }
 */
    //check for Internet Connection

    private fun checkForInternetConnection(): Boolean {

        val connectivityManager: ConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo

        val isDeviceIsConnectedToInternet = networkInfo != null
                && networkInfo.isConnectedOrConnecting

        if (isDeviceIsConnectedToInternet) {
            return true
        } else {
            createAlert()
            return false
        }
    }

        private fun createAlert(){
            val alertDialog: AlertDialog =
                AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle("Network Error")
            alertDialog.setMessage("Please check for internet connection")

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", {


                dialog: DialogInterface?, which: Int ->
                startActivity(Intent(Settings.ACTION_SETTINGS)) })

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",{

                dialog: DialogInterface?, which: Int  ->

                Toast.makeText(this@MainActivity, "You must be Connected to the internet",
                    Toast.LENGTH_SHORT).show()

                finish()
            })
            alertDialog.show()

        }


    //Specify the right or wrong answer

    private fun specifyTheRightAndWrongAnswer(userGuess: Int){

        when(correctAnswerIndex){
            0 -> button1.setBackgroundColor(Color.parseColor("#43BE31"))
            1 -> button2.setBackgroundColor(Color.parseColor("#43BE31"))
            2 -> button3.setBackgroundColor(Color.parseColor("#43BE31"))
            3 -> button4.setBackgroundColor(Color.parseColor("#43BE31"))

        }

        if(userGuess == correctAnswerIndex){
            txtState.setText("Right!")
            numberOfTimesUserAnsweredCorrectly++
            txtRightAnswer.setText("$numberOfTimesUserAnsweredCorrectly")
        }else{
            var correctPlantName = correctPlant.toString()
            txtState.setText("Wrong! Choose : $correctPlantName")
            numberOfTimesUserAnsweredInCorrectly++
            txtWrongAnswer.setText("$numberOfTimesUserAnsweredInCorrectly")
        }
    }
//Downloading Image Process

    inner class DownloadingImageTask: AsyncTask<String, Int, Bitmap?>(){

        override fun doInBackground(vararg pictureName: String?): Bitmap? {

            try{
                val downloadingObject = DownloadingObject()
                val plantBitmap: Bitmap? = downloadingObject.downloadPlantPicture(pictureName[0])
                return plantBitmap
            }catch(e: Exception){
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            setProgressBar(false)

            displayUIWidgets(true)

            playAnimationOnView(imgTaken, Techniques.Tada)
            playAnimationOnView(button1, Techniques.RollIn)
            playAnimationOnView(button2, Techniques.RollIn)
            playAnimationOnView(button3, Techniques.RollIn)
            playAnimationOnView(button4, Techniques.RollIn)
            playAnimationOnView(txtState, Techniques.Swing)
            playAnimationOnView(txtWrongAnswer, Techniques.FlipInX)
            playAnimationOnView(txtRightAnswer, Techniques.Landing)

            imgTaken.setImageBitmap(result)



        }
    }


    //Progress Bar Visibility

    private fun setProgressBar(show: Boolean){

        if(show){

            linearLayoutProgress.setVisibility(View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE) //To show ProgressBar
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else if(!show){


            linearLayoutProgress.setVisibility(View.GONE)
            progressBar.setVisibility(View.GONE) //To Hide ProgressBar
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        }
    }


    //Set Visibility of UI

    private fun displayUIWidgets(display: Boolean){

        if(display){

            imgTaken.setVisibility(View.VISIBLE)
            button1.setVisibility(View.VISIBLE)
            button2.setVisibility(View.VISIBLE)
            button3.setVisibility(View.VISIBLE)
            button4.setVisibility(View.VISIBLE)
            txtState.setVisibility(View.VISIBLE)
            txtWrongAnswer.setVisibility(View.VISIBLE)
            txtRightAnswer.setVisibility(View.VISIBLE)

        } else if(!display){
            imgTaken.setVisibility(View.INVISIBLE)
            button1.setVisibility(View.INVISIBLE)
            button2.setVisibility(View.INVISIBLE)
            button3.setVisibility(View.INVISIBLE)
            button4.setVisibility(View.INVISIBLE)
            txtState.setVisibility(View.INVISIBLE)
            txtWrongAnswer.setVisibility(View.INVISIBLE)
            txtRightAnswer.setVisibility(View.INVISIBLE)
        }

    }




    //playing Animations
private fun playAnimationOnView(view: View, techniques: Techniques){
        YoYo.with(Techniques.Tada)
            .duration(700)
            .repeat(1)
            .playOn(view)
    }


}







