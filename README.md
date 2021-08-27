# AdPlus Android SDK Geliştirici Kılavızı

Bu kılavuz **AdPlus** Android SDK kullanımına ait geliştiricilerin ihtiyaç duyacağı bilgileri içermektedir.


[Örnek Uygulama](https://github.com/gowittechnology/ssp-android-sdk-public/tree/master/app)


- [Android SDK  Entegrasyonu](#setup)
  - [Gradle entegrasyonu](#include-the-sdk)
  - [SDK'nın yüklenmesi](#setup-the-sdk)
- [SDK konfigürasyonu](#sdk-config)
- [Reklam Oluşturma](#create-add)
  - [1 - Banner Reklam Oluşturma](#init-banner)
    - [Genel Bilgiler](#info-banner)
    - [Reklam İsteği Oluşturma](#req-banner)
    - [Reklamların Gösterilmesi ve Geri bildirim methodları](#callback-banner)
  - [2 - Interstitial Reklam Oluşturma](#init-interstitial)
    - [Genel Bilgiler](#info-interstitial)
    - [Reklam İsteği Oluşturma](#req-interstitial)
    - [Reklamların Gösterilmesi ve Geri bildirim methodları](#callback-interstitial)
- [Gereklilikler](#req)

<a name="setup"/>

# Android SDK  Entegrasyonu

Bu bölümde android uygulamanıza **AdPlus** reklamlarını ekleyebilmeniz için gereken adımlar yer almaktadır.


<a name="include-the-sdk"/>

## Gradle entegrasyonu

SDK'yı uygulamanıza ekleyebilmeniz için **build.gradle** dosyalarınızda çeşitli eklemeler yapmanız gerekmektedir. İlk adım olarak uygulamanın SDK'sının barınmakta olduğu yolu proje seviyesinde bulunan **build.gradle** dosyasına aşağıdaki şekilde eklemeniz gereklidir.

```groovy
repositories {
        google()
        mavenCentral()
        maven {
            url "https://gowitssp.jfrog.io/artifactory/default-maven-virtual"
        }
    }
```

Ardından app seviyesinde bulunan **build.gradle** dosyanıza aşağıdaki şekilde kütüphanenin importunu yapmalısınız.

```groovy
// Add repository for SSP SDK
    implementation 'com.gowit:ssp-android-sdk:0.0.10'
```

Sonrasında yapacağınız **gradle sync** işleminin arkasından kütüphane projenize dahil edilmiş olacaktır.

<a name="setup-the-sdk"/>

## SDK'nın yüklenmesi

**SDK** nın modüllerine erişmek için ilk önce **InventoryID**'niz ve **AdUnitID**'niz ile konfigürasyon dosyanızı oluşturmalısınız. 

```kotlin
val config = SspSdkConfiguration.Builder()
            .setInventoryId("1111111111") // inventory id
            .setAdvertiserId("222222222") // advertiser id
            .setSspLogLevel(SspLoggerLevel.ERROR) // SDK sistemi loglama seviyesi.
            .build()
```

Ardından **SSPSdk** nesnemizi oluşturmak için aşağıda bulunan örnek kodu kullanabilirsiniz.

```kotlin
lateinit var sspSdk: SspSdk

sspSdk = SspSdk.getInstance(
            context = this,
            sspSdkConfiguration = config,
            sspClientMetaData = sspClientMetaData, // SspClientMetaData objemizi ilerleyen bir zamanda da yaratabiliriz. Optional bir alandır.
            sspSdkInitializationListener = this // SspSdkInitializationListener objemizi eğer kütüphane durumunu izlemek istersek yaratıp burada ekleyebiliriz.
        )
```

<a name="sdk-config"/>

# SDK konfigürasyonu

Bu bölümde android uygulamanızda **AdPlus** reklamlarını özelleştirmeniz için gereken adımlar yer almaktadır.

Reklam isteklerini özelleştirmek için yarattığımız **SspSdk** nesnesi üzerindeki **SspClientMetaData** objesini kullanabilirsiniz.

Özelleştirmek için kullanılan parametreler aşşağıdaki gibidir.

```kotlin
    val yob: Long?, //Cihaz sahibinin doğum yılı.
    val sspGender: SspGender?, //Cihaz sahibinin cinsiyeti.
    val cat: IABCategory?, //Sayfa iceriğinin IAB kategorileri.
    val lt: Long?, //Eğer cihaz konum alıyorsa enlem verisi.
    val ln: Long?, //Eğer cihaz konum alıyorsa boylam verisi.
    val app_v: String? //Uygulamanın versiyonu.
```
Bu parametreleri reklam isteği oluşturmadan önce belirlemelisiniz.

<a name="create-add"/>

# Reklam Oluşturma

Bu bölümde android uygulamanızda **AdPlus** reklamlarını oluşturmanız için gereken adımlar yer almaktadır.

<a name="init-banner"/>

## 1 - Banner Reklam Oluşturma

<a name="info-banner"/>

### Genel Bilgiler

**Banner** reklamlar sayfanızın içeriğine dahil olacak şekilde görünecek ebatları ve pozisyonu belirlenebilen **statik görünümlü** reklamlardır. Bir sayfada **birden çok** banner reklam yer alabilir. 

<a name="req-banner"/>

### Reklam İsteği Oluşturma

**Banner** reklam isteği oluşturmadan önce yarattığımız **SspSdk** nesnesi üzerindeki **SspBannerAdListener** arayüzünü uyguladığınız aktivite veya fragmentınızı belirtmelisiniz.

Bu arayüz aşşağıdaki gibi tanımlanmıştır.

```kotlin
interface SspBannerAdListener {
    fun adReceived(identifer: Int?, bannerAd: SspBannerAd)
    fun adFailedToLoad(identifer: Int?, reason: SspResult)
    fun adWillAppear(identifer: Int?) {}
}
```

Bu parametreyi belirledikten sonra reklam isteğinizi aşşağıdaki methodu çağırarak oluşturabilirsiniz.

```kotlin
    fun requestBanner(bannerSize: SspBannerSizes, identifier: Int? = null): SspRequestResponse {}
```

<a name="callback-banner"/>

### Reklamların Gösterilmesi ve Geri bildirim methodları

Reklam isteğiniz başarılı bir şekilde oluşturulduktan sonra, bu isteğin sonucu **SspBannerAdListener** arayüzünü uyguladığınız sınıfınıza iletilecektir.

Reklam isteğiniz sonucunda, gösterilecek reklam olması halinde

```kotlin
    fun adReceived(identifer: Int?, bannerAd: SspBannerAd)
```
methodu çağırılacaktır.

Bu aşamada reklamı göstermek istedğiniz **ViewGroup** objenizi SDK ya göndermeniz gerekmektedir. Bu işlem için bu kod örneği aşşağıdaki gibidir.

```kotlin
    override fun adReceived(identifer: Int?, bannerAd: SspBannerAd) {
        identifer?.let { id ->
            if (id == 0) {
                bannerAd.show(findViewById<FrameLayout>(R.id.frameLayout1))
            } else if (id == 1) {
                bannerAd.show(findViewById<LinearLayout>(R.id.linearLayout1))
            }
        }
    }
```

Reklam isteğiniz sonucunda ya da reklam gösterilmesi sırasında bir hata oluşması halinde

```kotlin
    fun adFailedToLoad(identifer: Int?, reason: SspResult)
```
methodu çağırılacaktır.

Reklam isteği ile ilgili tüm işlemler tamamlandıktan sonra, reklam nesnesi arayüze eklenmeden hemen önce eğer uygulandı ise

```kotlin
    // optional
    fun adWillAppear(identifer: Int?) {}
```
methodu çağırılacaktır. 

<a name="init-interstitial"/>

## 2 - Interstitial Reklam Oluşturma

<a name="info-interstitial"/>

### Genel Bilgiler

**Interstitial** reklamlar çalıştığı uygulamanın tümünü kaplayacak şekilde görünecek **dinamik görünümlü** reklamlardır. Bir sayfada **sadece bir** interstitial reklam yer alabilir. 

<a name="req-interstitial"/>

### Reklam İsteği Oluşturma

**Interstitial** reklam isteği oluşturmadan önce yarattığımız **SspSdk** nesnesi üzerindeki **SspInterstitialAdListener** arayüzünüzü uyguladığınız sınıfınızı belirtmelisiniz.

Bu arayüz aşağıdaki gibi tanımlanmıştır.

```kotlin
interface SspInterstitialAdListener {
    fun interstitialAdReceived(identifier: Int?, sspInterstitialAd: SspInterstitialAd)
    fun interstitialAdFailedToLoad(identifier: Int?, reason: SspResult)
    fun interstitialAdClicked(identifier: Int?) {}
    fun interstitialAdClosed(identifier: Int?) {}
    fun interstitialAdWillAppear(identifier: Int?) {}
}
```

Bu parametreyi belirledikten sonra reklam isteğinizi aşağıdaki methodu çağırarak oluşturabilirsiniz.

```kotlin
    fun requestInterstitial(popUpSizes: SspPopUpSizes, identifier: Int? = null): SspRequestResponse
```

<a name="callback-interstitial"/>

### Reklamların Gösterilmesi ve Geri bildirim methodları

Reklam isteğiniz başarılı bir şekilde oluşturulduktan sonra, bu isteğin sonucu **SspInterstitialAdListener** arayüzünü uyguladığınız sınıfınıza iletilecektir.

Reklam isteğiniz sonucunda, gösterilecek reklam olması halinde

```kotlin
    fun interstitialAdReceived(identifier: Int?, sspInterstitialAd: SspInterstitialAd)
```
methodu çağırılacaktır.

Bu aşamada reklamı göstermek istediğiniz **ViewGroup** objenizi SDK ya göndermeniz gerekmektedir. Bu işlem için bu kod örneği aşağıdaki gibidir.

```kotlin
    override fun interstitialAdReceived(identifier: Int?, sspInterstitialAd: SspInterstitialAd) {
        sspInterstitialAd.show(this)
    }
```
Reklamın gösterileceği aktivitenin uygulamanızın **AndroidManifest.xml** dosyasında belirtilmiş application tagi altında olması gereklidir.

```xml
    <activity android:name="com.gowit.sspandroidsdk.adunit.interstitial.SspInterstitialAdActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
```

Açılan reklamın kapatılması durumunda arayüzünüzün aşağıdaki methodu çağırılacaktır.

```kotlin
    fun interstitialAdClosed(identifier: Int?) {}
```

Reklam gösterimi sırasında karşılaşılan bir sorun durumunda ise aşağıdaki method çağıralacaktır.

```kotlin
    fun interstitialAdFailedToLoad(identifier: Int?, reason: SspResult)
```

Reklam isteği ile ilgili tüm işlemler tamamlandıktan sonra, reklam nesnesi arayüze eklenmeden hemen önce eğer uygulandı ise.

```kotlin
    fun interstitialAdWillAppear(identifier: Int?) {}
```
methodu çağırılacaktır. 

<a name="#req"/>

# Gereklilikler
- Android Studio 3.2 ve ustu
- Android API level 21 ve ustu

 
