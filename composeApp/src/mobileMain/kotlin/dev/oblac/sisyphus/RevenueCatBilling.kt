package dev.oblac.sisyphus

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.configure
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import dev.oblac.sisyphus.game.Place

/** RevenueCat's key for its Test Store, where buying is free. Only debug builds use it. */
private const val TEST_STORE_KEY = "test_yTNXCLnrSyIXtunligUHmteApIR"

/** RevenueCat's key for the App Store, or for Google Play. */
internal expect val STORE_KEY: String

/** The offering that sells the worlds, a package for each, named after its product. */
private const val WORLDS = "worlds"

/**
 * The worlds bought through RevenueCat. The product of a world unlocks the entitlement of the same name.
 * Debug builds buy in the Test Store. The iOS SDK does not stop a release build with the Test Store key, so only debug builds may set [testStore].
 */
class RevenueCatBilling(testStore: Boolean) : Billing {
    private var owned = emptySet<String>()
    private var packages = emptyMap<String, Package>()

    init {
        if (!Purchases.isConfigured) Purchases.configure(if (testStore) TEST_STORE_KEY else STORE_KEY)
        Purchases.sharedInstance.delegate = object : PurchasesDelegate {
            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) = take(customerInfo)

            override fun onPurchasePromoProduct(
                product: StoreProduct,
                startPurchase: (
                    onError: (error: PurchasesError, userCancelled: Boolean) -> Unit,
                    onSuccess: (storeTransaction: StoreTransaction, customerInfo: CustomerInfo) -> Unit,
                ) -> Unit,
            ) = startPurchase({ _, _ -> }, { _, info -> take(info) })
        }
        Purchases.sharedInstance.getCustomerInfo(onError = {}, onSuccess = ::take)
        withPackages {}
    }

    override val userId: String get() = Purchases.sharedInstance.appUserID

    override fun owns(place: Place) = place.product.let { it == null || it in owned }

    override fun price(place: Place) =
        packageOf(place)?.storeProduct?.price?.let { shownPrice(it.formatted, it.amountMicros, it.currencyCode) }

    override fun buy(place: Place, done: (Boolean) -> Unit) = withPackages {
        val world = packageOf(place) ?: return@withPackages done(owns(place))
        Purchases.sharedInstance.purchase(
            world,
            onError = { _, _ -> done(owns(place)) },
            onSuccess = { _, info ->
                take(info)
                done(owns(place))
            },
        )
    }

    override fun restore(done: () -> Unit) = Purchases.sharedInstance.restorePurchases(
        onError = { done() },
        onSuccess = {
            take(it)
            done()
        },
    )

    /** The packages came once, at the start; when the store could not send them then, as with no network, it is asked again. */
    override fun refresh() = withPackages {}

    private fun packageOf(place: Place) = place.product?.let { packages[it] }

    /** Runs [then] once the packages of the worlds are here, or the store could not send them. */
    private fun withPackages(then: () -> Unit) {
        if (packages.isNotEmpty()) return then()
        Purchases.sharedInstance.getOfferings(
            onError = { then() },
            onSuccess = { offerings ->
                packages = offerings[WORLDS]?.availablePackages.orEmpty().associateBy { it.identifier }
                then()
            },
        )
    }

    private fun take(info: CustomerInfo) {
        owned = info.entitlements.active.keys
    }
}
