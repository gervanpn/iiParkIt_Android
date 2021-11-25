package com.example.iiparkit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.ContentInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.drawToBitmap
import androidx.lifecycle.LifecycleOwner
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import okhttp3.*
import java.io.File
//import com.squareup.okhttp.*
import java.io.IOException
import java.lang.Byte.decode
import java.lang.reflect.Type
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val client = OkHttpClient()

    lateinit var text : TextView
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        text = findViewById(R.id.textView2)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                var bytes = codeScanner to byteArrayOf()
                    //.camera.toDrawable().toBitmap() to byteArrayOf()
//                var bytes = scannerView.onReceiveContent( payload, {
//
//                }
//
//                )



                //drawToBitmap() to byteArrayOf()
                //var bytes = scannerView.drawToBitmap() to byteArrayOf()
                Log.d("bytes", bytes.second.size.toString())
//                val job = GlobalScope.async {saveBitmap("","scan.png",bytes.second) }
//                runBlocking {
//                    job.await()
//                }
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        run("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Example")
    }



    fun run(url: String){
        val request = Request.Builder()
            .url(url)
            .build()
        //val gson = Gson()
//        var parse = DataParsing(
//            _links = Links(
//                next = Next(
//                    href = "",
//                    title = ""
//                )
//            ), hints = listOf(), parsed = listOf(), text = ""
//        )

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {

                //val collectionType: Type = object : TypeToken<DataParsing>() {}.getType()
                val body = response.body
                text.text = body?.toString()
                if (body != null) {
                    //Log.d("response", body.toString())
                    val job1 = GlobalScope.async {saveBitmap("","test.png",body.bytes()) }
                    runBlocking {
                        job1.await()
                    }
//                    val intent = Intent()
//                        .setType("*/*")
//                        .setAction(Intent.ACTION_GET_CONTENT)
//
//                    startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
        }
    }

    suspend fun Context.saveBitmap(path : String, fileName: String, bitmap: ByteArray) = withContext(Dispatchers.IO) {
        val file = File(filesDir, fileName)
        Log.d("file",file.getAbsolutePath())
        //val imageBytes = Base64.decode(bitmap, Base64.DEFAULT);
        val image= BitmapFactory.decodeByteArray(bitmap, 0, bitmap.size);
        //file.printWriter().use {out -> out.print(bitmap)}

            file.outputStream().use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }



    override fun onResume() {
    super.onResume()
    codeScanner.startPreview()
}

override fun onPause() {
    codeScanner.releaseResources()
    super.onPause()
}

}