package com.hogent.mindfulness.scanner

import android.Manifest.permission.CAMERA
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Toast
import com.google.zxing.Result
import com.hogent.mindfulness.MainActivity
import com.hogent.mindfulness.login.LoginActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler{

    private val REQUEST_CAMERA = 1
    private var scannerView: ZXingScannerView? = null
    private val camId = CAMERA_FACING_BACK
    private lateinit var code:String
    private var returnActivity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        returnActivity = intent.getIntExtra("activity", 0)

        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        val currentApiVersion = Build.VERSION.SDK_INT

        if (currentApiVersion >= Build.VERSION_CODES.M) {
//            if (checkPermission()) {
//                Toast.makeText(applicationContext, "Permission already granted!", Toast.LENGTH_LONG).show()
//            } else {
//                requestPermission()
//            }
            if(!checkPermission()){
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), REQUEST_CAMERA)
    }

    public override fun onResume() {
        super.onResume()

        val currentapiVersion = android.os.Build.VERSION.SDK_INT
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = ZXingScannerView(this)
                    setContentView(scannerView)
                }
                scannerView!!.setResultHandler(this)
                scannerView!!.startCamera()
            } else {
                requestPermission()
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        scannerView!!.stopCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA -> if (grantResults.size > 0) {

                val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted) {
                    Toast.makeText(
                        applicationContext,
                        "Permission Granted, Now you can access camera",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Permission Denied, You cannot access and camera",
                        Toast.LENGTH_LONG
                    ).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                DialogInterface.OnClickListener { dialog, which ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            arrayOf(CAMERA),
                                            REQUEST_CAMERA
                                        )
                                    }
                                })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        android.support.v7.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun handleResult(result: Result) {
//        code = result.text
//        Log.d("QRCodeScanner", result.text)
//        Log.d("QRCodeScanner", result.barcodeFormat.toString())
//
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Scan Result")
//        builder.setPositiveButton(
//            "Unlock"
//        ) { dialog, which ->
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("code", code)
//            startActivity(intent)}
////        builder.setNeutralButton("Visit") { dialog, which ->
////            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(myResult))
////            startActivity(browserIntent)
////        }
//        builder.setMessage(result.text)
//        val alert1 = builder.create()
//        alert1.show()
        code = result.text
        when(returnActivity) {
            0 -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("code", code)
                intent.putExtra("activity", returnActivity)
                startActivity(intent)
            }
            1 -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("code", code)
                intent.putExtra("activity", returnActivity)
                startActivity(intent)
            }
        }

    }

}
