package com.gowit.sspandroidexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.gowit.sspandroidsdk.SspSdk
import com.gowit.sspandroidsdk.SspSdkConfiguration
import com.gowit.sspandroidsdk.adunit.banner.SspBannerAd
import com.gowit.sspandroidsdk.adunit.interstitial.SspInterstitialAd
import com.gowit.sspandroidsdk.common.*
import com.gowit.sspandroidsdk.utilities.*

class MainActivity : AppCompatActivity(), SspSdkInitializationListener, SspBannerAdListener, SspInterstitialAdListener {

    lateinit var sspSdk: SspSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request a Banner in size of a Banner by invoking 'requestBanner' w320dp h50dp
        // If container is small in size, ad will not be shown and there will be an error message coming
        // from listener
        findViewById<Button>(R.id.buttonLoadFirstBanner).setOnClickListener {
            // You can use identifier as a unique id for each of your banner views
            val sspRequestResponse = sspSdk.requestBanner(bannerSize = SspBannerSizes.BANNER,identifier = 0)
            Log.e(LOG_TAG, "Banner1 Requested: " + SspRequestResponse.valueOf(sspRequestResponse.name).details)
        }

        // Request a Banner in size of a LARGERECT by invoking 'requestBanner' w336dp h280dp
        // If container is small in size, ad will not be shown and there will be an error message coming
        // from listener
        findViewById<Button>(R.id.buttonLoadSecondBanner).setOnClickListener {
            // You can use identifier as a unique id for each of your banner views
            val sspRequestResponse = sspSdk.requestBanner(bannerSize = SspBannerSizes.LARGERECT, identifier = 1)
            Log.e(LOG_TAG, "Banner2 Requested: " + SspRequestResponse.valueOf(sspRequestResponse.name).details)
        }

        // Request a Interstitial Ad in size of a  by invoking 'requestBanner' w320dp h480dp
        // If container is small in size, ad will not be shown and there will be an error message coming
        // from listener
        findViewById<Button>(R.id.buttonInters).setOnClickListener {
            val sspRequestResponse = sspSdk.requestInterstitial(popUpSizes = SspPopUpSizes.SMALL, identifier = 0)
            Log.e(LOG_TAG, "Load Interstitial Ad: : " + SspRequestResponse.valueOf(sspRequestResponse.name).details)
        }

        initializeSspSdk()
    }

    private fun initializeSspSdk() {
        // You have to feed your SspSdkConfiguration object with Inventory Id and Advertiser Id
        // that you get from panel of Ssp.
        val config = SspSdkConfiguration.Builder()
            .setInventoryId("1111111111") // your inventory id which can be found inside panel
            .setAdvertiserId("222222222") // your advertiser id which can be found inside panel
            .setSspLogLevel(SspLoggerLevel.ERROR) // You can set a log level for the Sdk level logs visibility
            .build()

        // You can either fill all of the data inside SspClientMetaData, or you can fill them afterwards
        // Just create your metaData object as singleton and handle it
        val sspClientMetaData = SspClientMetaData.Builder()
            .birthDateAsLong(1988) // User BirthDate
            .gender(SspGender.Male) // User Gender
            .iabCategory(IABCategory.IAB12) // IABCategory
            .latitude(39.0131231.toLong()) // Latitude of client
            .longitude(22.20213.toLong()) // Longitude of client
            .appVersion("1.2.3") // Version of running app
            .build()

        // After you create your necessary config and clientmetada objects, you can get an instance of SspSdk
        // Use getInstance method to get singleton object that is created inside library
        // remember to feed it with an sdkInit Listener as well, to check whether or not sdk is init successfully

        sspSdk = SspSdk.getInstance(
            context = this,
            sspSdkConfiguration = config,
            sspClientMetaData = sspClientMetaData,
            sspSdkInitializationListener = this
        )

        // Attach banner listener if you use a banner inside that activity or fragment
        sspSdk.setBannerAdListener(this)

        // Attach popup / interstitial listener if you want to show a interstitital ad inside that activity or fragment
        sspSdk.setPopUpAdListener(this)
    }

    // After successfully fetching an banner ad from service this callback will be triggered
    // here, banner view can be shown inside a container viewgroup
    override fun adReceived(identifer: Int?, bannerAd: SspBannerAd) {
        // Use sspBannerAd.show method to show this banner inside selected container
        identifer?.let { id ->
            if (id == 0) {
                bannerAd.show(findViewById<FrameLayout>(R.id.frameLayout1))
            } else if (id == 1) {
                bannerAd.show(findViewById<LinearLayout>(R.id.linearLayout1))
            }
        }
    }

    // After receiving an error when fetching a banner ad from service, this callback will be triggered
    override fun adFailedToLoad(identifer: Int?, reason: SspResult) {
        Log.d(LOG_TAG, "Ad failed: $identifer : ${SspResult.valueOf(reason.name).details}")
    }

    // This callback is optional, and can be used as an indicator to say that ad is loaded into container
    override fun adWillAppear(identifer: Int?) {
        Log.e(LOG_TAG, "Ad appear with id : $identifer")
        super.adWillAppear(identifer)
    }

    // This callback is used for understanding if sdk is initialized successfully
    override fun onInitializationSuccess() {
        Log.e(LOG_TAG, "Sdk initialization successfully done")
    }

    // This callback is called when sdk initialization is failed for some reason
    override fun onInitializationFailure(message: String) {
        Log.e(LOG_TAG, "Sdk initialization failure : $message")
    }

    // This callback is called whenever an interstitial ad loaded from server successfully
    override fun interstitialAdReceived(identifier: Int?, sspInterstitialAd: SspInterstitialAd) {
        identifier?.let { id ->
            // Use sspInterstitialAd.show method to show this interstitial ad
            // To show interstitial ad, client should add following tag inside its manifest file
            // <activity android:name="com.gowit.sspandroidsdk.adunit.interstitial.SspInterstitialAdActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
            sspInterstitialAd.show(this)
        }
    }

    // After receiving an error when fetching a interstitial ad from service, this callback will be triggered
    override fun interstitialAdFailedToLoad(identifier: Int?, reason: SspResult) {
        Log.e(LOG_TAG, "Interstitial Ad Failed with id: $identifier and reason is -> :${SspResult.valueOf(reason.name).details}")
    }

    // This callback method is called after interstitial ad is closed from its view
    override fun interstitialAdClosed(identifier: Int?) {
        Log.e(LOG_TAG, "Interstitial Ad Closed!")
        super.interstitialAdClosed(identifier)
    }

    // This callback is called just before interstitial ad is shown
    override fun interstitialAdWillAppear(identifier: Int?) {
        Log.e(LOG_TAG, "Interstitial will appear")
        super.interstitialAdWillAppear(identifier)
    }

    companion object {
        const val LOG_TAG = "SSP-SDK-TEST-APP"
    }
}