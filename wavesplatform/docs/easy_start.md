Add waves and in necessary rxjava libs to your app-project in в build.gradle-file

app/build.gradle
dependencies {
    // ...
    // Check last version at https://search.maven.org/artifact/com.wavesplatform
    implementation 'com.wavesplatform:mobile-sdk-android:1.0.0'
    // If you will use Waves Rx service method
    implementation 'io.reactivex.rxjava2:rxjava:2.2.7'
    // ...
}
Add uses-permisson about Internet in manifest.xml-file of project

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.mysite.mywavesapplication">
 
    <uses-permission android:name="android.permission.INTERNET"/>
 
    <application
        <!-- ... -->
    </application>
 
</manifest>
Add library initialization to onCreate() method in your Application-class extension

class App : Application()
class App : Application() {
     
    override fun onCreate() {
        super.onCreate()
        // ...
        // Waves SDK initialization
        WavesPlatform.init(this)
        // ...
    }
}
Now everything is ready to start using Waves. For example, we want to create a new seed-phrase and get address for blockchain. It is available in WavesCrypto.

Seed-phrase generation
// Generate or add your seed-phrase
val newSeed: String = WavesCrypto.randomSeed()
// Get address by seed-phrase
val address: String = WavesCrypto.addressBySeed(newSeed)
You can see about other methods in WavesCrypto.

Now let's see how we can work with the blockchain.
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
            WavesPlatform.service()
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
        WavesPlatform.service().addOnErrorListener(object : OnErrorListener {
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
Signing and sending a transaction:
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
    compositeDisposable.add(WavesPlatform.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                // Do something on success, now we have wavesBalance.balance in satoshi in Long
            }, { error ->
                // Do something on fail
            }))
}
You can find your transaction in https://wavesexplorer.com by id.
Basically that's it. For more information about sending other transactions and work with services, read the table of contents.

