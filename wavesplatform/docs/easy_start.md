# WavesSDK for Android

1. Add waves and in necessary rxjava libs to your app-project in "app/build.gradle" - file

```groovy
dependencies {
    // ...
    // Check last version at https://search.maven.org/artifact/com.wavesplatform
    implementation 'com.wavesplatform:mobile-sdk-android:1.0.0'
    // If you will use Waves Rx service method
    implementation 'io.reactivex.rxjava2:rxjava:2.2.7'
    // ...
}
```

2. Add uses-permisson about Internet in "app/src/main/manifest.xml" - file of project

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.mysite.mywavesapplication">
 
    <uses-permission android:name="android.permission.INTERNET"/>
 
    <application>
        <!-- ... -->
    </application>
 
</manifest>
```

3. Add library initialization to onCreate() method in your Application-class extension

```java
class App : Application() {
     
    override fun onCreate() {
        super.onCreate()
        // ...
        // Waves SDK initialization
        WavesSdk.init(this)
        
        // or use Environment.TEST_NET for switch to Test-Net
        // WavesSdk.init(this, Environment.TEST_NET)
        // ...
    }
}
```

4. Now everything is ready to start using Waves. For example, we want to create a new seed-phrase and get address for blockchain. It is available in WavesCrypto.

```java
fun seedPhraseGeneration() {
    // Generate or add your seed-phrase
    val newSeed: String = WavesCrypto.randomSeed()
    // Get address by seed-phrase
    val address: String = WavesCrypto.addressBySeed(newSeed)
}
```

You can see about other methods in WavesCrypto.


5. Now let's see how we can work with the blockchain.

```java
class MainActivity : AppCompatActivity() {
 
    // For Activity or Fragment add Observables in CompositeDisposable from Rx
    private val compositeDisposable = CompositeDisposable()
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }
 
 
    // Now you can get Waves balance from Node
    private fun getWavesBalance(address: String) {
        compositeDisposable.add(
            WavesSdk.service()
                .getNode() // You can choose different Waves services: node, matcher and data service
                .wavesBalance(address) // Here methods of service
                .compose(RxUtil.applyObservableDefaultSchedulers()) // Rx Asynchron settings
                .subscribe({ wavesBalance ->
                    // Do something on success, now we have wavesBalance.balance in satoshi in Long
                    Toast.makeText(
                        this@MainActivity,
                        "Balance is : ${getScaledAmount(wavesBalance.balance, 8)} Waves",
                        Toast.LENGTH_SHORT)
                        .show()
                }, { error ->
                    // Do something on fail
                    val errorMessage = "Can't get wavesBalance! + ${error.message}"
                    Log.e("MainActivity", errorMessage)
                    error.printStackTrace()
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                })
            )
    }
 
 
    // Handle NetworkExceptions if it necessary
    private fun handleNetworkErrors() {
        WavesSdk.service().addOnErrorListener(object : OnErrorListener {
            override fun onError(exception: NetworkException) {
                // Handle NetworkException here
            }
        })
    }
 
 
    // And you must unsubscribe in onDestroy()
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
```

6. Signing and sending a transaction:

```java
private fun transactionsBroadcast() {
     
    // Creation Transfer transaction and fill it with parameters
    val transferTransaction = TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "someAddressOrAlias",
            amount = 100000000,
            fee = WavesConstants.WAVES_MIN_FEE,
            attachment = "Some comment to transaction",
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY)
 
    // Sign transaction with seed
    transferTransaction.sign(seed = "sign transaction with your seed phrase")
 
    // Try to send transaction into Waves blockchain
    compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                // Do something on success, now we have wavesBalance.balance in satoshi in Long
            }, { error ->
                // Do something on fail
            }))
}
```

7. You can find your transaction in [Explorer](https://wavesexplorer.com) by id.

8. Basically that's it.

