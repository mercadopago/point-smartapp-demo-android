# v7.1.0
## Added
- [ISPI-2644] new Activity with new payment flow that use payment methods as strings

# v7.0.0
## Added
- [ISPI-2848] Bump integration SDK to 7.0.0
  - New method to get detailed payment by external reference
## Fixed
- Improved app stability (ANR detection improvements)
- More flexible payment description validation

# v5.2.2
## Fixed
- Update integration SDK 5.2.1 -> 5.2.2
  - Fixed ANR event flooding; ANR detection disabled by default.

# v5.2.1
## Fixed
- [ISPI-1292] Update SDK integration to 5.2.1 and don't show installments screen if no needed.

# v5.1.1
## Fixed
- Correct detection of Main app in smart apps integration
- Improvements on installments flow to init a payment

# v5.1.0
## Added
- Get features and fixes designed for the PDV app to enable it with other smart apps:
  - Adjusted description validation to be more flexible.
  - New field to init a payment: Transaction metadata.

## 5.0.4
- Changed sub-merchant var to val to avoid payment flow & traceability inconsistencies

# Unreleased
## Added
- [ISPI-3067] Migrate point-mainapp-demo to point-smartapp-demo under MercadoPago namespace

# v5.0.3
## Fixed
- Added visualization of the external_reference field in the getStatus method response
- PREPAID_CARD payment method propagation to avoid sending UNDEFINED paymentMethod

# v5.0.0
## Changed
- Updated sdk library version to 5.0.0
- Deleted legacy payment activity result and scanner activity

# v4.5.0
## Added
- Updated sdk library version to 4.6.0
- Add tip amount in feedback of payment response

# v4.4.0
## added
- Support for the Platform ID parameter in the checkout flow and availability of camera scanner enhancements

# v4.3.0
## added
- Availability of the method to obtain the status of an approved payment

# v4.2.0
## added
- adding functionality to retrieve an approved payment by id

# v4.1.1
## Fixed
- Remove cash payment from payment method. This payment method will be available at a later date

# v4.1.0
## Added
- Improve payment flow with taxes in payment.

# v4.0.2
## Added
- Implement new metadata for on-demand app installation.

# v4.0.1
## Fixed
- Fix: Improve Payment Flow Validation and Payment Method Adjustments

# v4.0.0
## Added
- Updated sdk library with unified version to SmartApps
- Moved bitmap print to raw resources, and replaced with new image with addition blank bottom space.

# v3.0.1
## Added
- Improve UX the custom tag activity test
- Implement Activity to test Custom tag print

# v2.9.2
## Added
- Update sdk library with some technical improvements.

# v2.9.1
## Fixed
- Update message print onterminal

# v2.9.0
## Added
- send through messenger custom tag to custom print 2.0
- change string to serializable voucher in payment method model
- Migrate the UserValidation class to an Interface.
- change of strategy in the launch of the deeplink for the camera scanner in reading QR and barcode codes.
- Adjust CameraScannerStatus enumerator class to avoid breaking changes
- implement payment flow with messenger and monitoring of event

# v2.8.2
## Fixed
- Implement sending payment flow response data to PTM

# v2.8.1
## Fixed
- Fixed screen orientation changing when using barcode feature.

# v2.8.0
## Added
- The functionality for reading barcodes was added.
- Added functionality to obtain the client Id from the manifest.
- Added field to enable or disable oauth.

# v2.7.0
## Added
- Added traceability for all SDK functionalities.

# v2.6.3
## Fixed
- Internal MP improvement.

# v2.6.2
## Added
- Update sdk library with some technical improvements.
  - Updated Client Id for Sdk initialization.
  - Added example of use of new payment launch method.
  - Added hide keyboard when on UI get payment methods.

# v2.5.1
## added
- Update sdk library

# v2.5.0
## added
- Update of the SDK library with the implementation of modularization.

# v2.3.0
## Fixed
- adding installments functionality to the MLB seller.
- furyDoc on installments functionality for MLB seller.

## Fixed
- solution to the exception when returning from the payment flow with installments.

# v2.2.1
## Fixed
- Exception mapping when printing image by bitmap.
- Added signature keystore configuration for release build.

# v2.2.0
## Added
- Beat implementation the new way of tracking flow events.
- Remove the Timber library and replace the event log with the new implementation.
- Added smartPos version number in app info.
- Change name parameter SmartDevice.
- Removed coroutines dependency in the printer functionalities.

# v2.1.0
## Added
- MP Generic Response has been implemented in the following features: Payment Flow, Payment Methods, Smart Information, Printer, Camera Scanner.
- Added flavor to support demo app examples for natives MainApps & MiniApps.
- hide unnecessary functionality for mini apps flavors

## Fixed
- Fixed error related to sdk version no available into smart information option.

# v2.0.0
## Modified
- Update kotlin version

## Fixed
- Fixed on click listener in payment button

## Added
- Coroutines have been implemented in the content resolvers and other layers, such as use cases and the implementations of the features available in the SDK.
- Used Smart App content provider to update hardware buttons state
- Implemented the feature that allows to obtain the SDK and device information.
- Backward compatibility in callbacks when building the paymentFlow deeplink in the SDK.
- Implement generic result to handler sdk answer
- Error handling when inserting a non-numeric amount format.

# v1.3.0
## Added
- Launch payment flow with payment method like parameter
- Launch QR Scanner functionality
## Fixed
- Fixed animation when paring or unpair devices
- Error with successfully approved payment.

# v1.2.0
## Added
- Add ticket printing functionality in bitmap format.
- Added maximum and minimum amount validation functionality.

# v1.1.0
## Added
- Add Refunds example using the public MP API
- add observer connect manager bluetooth
- Added bluetooth printer example screen

# v1.0.0
## Added
- Added Integration SDK v1.0.0
- Added support of bluetooth tools & Bluetooth UI settings.
- Added Home activity and color scheme
- Removed all mercado libre plugins an dependecies
- Implemented payment flow
- Initial template
