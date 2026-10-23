# Third-Apps Integration Kit

## Project status

<p align="center">
<a href='https://android-arsenal.com/api?level=21'><img alt='MIN API' src='https://img.shields.io/badge/min%20api-23-blue?style=for-the-badge'/></a>
<a href='https://developer.android.com/studio/releases#4.2.1'><img alt='ANDROID STUDIO PLUGIN VERSION' src='https://img.shields.io/badge/android%20studio-4.2.1-blue?style=for-the-badge'/></a>
<a href='https://docs.gradle.org/7.4.0/release-notes.html'><img alt='GRADLE VERSION' src='https://img.shields.io/badge/gradle-7.4.0-blue?style=for-the-badge'/></a>
</p>

## What is this?

This repository contains a demo application that utilizes the Third-Party Integration Kit to establish connections between a third-party application and the MercadoPago Payments ecosystem. This integration facilitates the utilization of specific hardware capabilities such as camera for barcode reading, embedded printers, Bluetooth, and more.

## Single Sign-On example

The **Single Sign-On** home action opens `SingleSignOnActivity`. Select **Request identity token** to call `MPManager.singleSignOnTools.requestIdentityToken` with a fresh UUID nonce. The loader and status text follow `onProgress`; `onSuccess` and `onError` hide the loader and enable the button for another request. Errors display their numeric code (`GenericErrorCode` / `SsoErrorCode`) and the SDK public message, including unrecognized host codes.

The demo bundles the stable Integration SDK `7.3.1` AAR. Configure `withSingleSignOnTools()` before initializing `MPManager`. A compatible Point host and an authenticated Mercado Pago session are required; an unsupported host returns `NOT_SUPPORTED`.

The demo displays the full returned identity token (JWT) as selectable text on the SSO screen for inspection and screenshots. It clears the displayed token when a new request starts and does not log, decode or persist it, or create an integrator session. The SSO screen allows screenshots and screen recording. A real integration must establish the expected nonce with its trusted backend and send the token there to validate its signature, claims, and nonce binding and prevent replay. Screen recreation does not resume a pending request; the SDK rejects a second request while one is still running.

## Payment status example

The **Status** home action opens local and online queries side by side. Online queries use
SDK 7.3.1's `OnlinePaymentStatusCallback`: `onSuccess` displays the public payment data,
and `onError` handles `OnlinePaymentStatusErrorCode` values or displays the numeric code
for other errors. Local queries retain the SDK's legacy response callback. Both flows
allow another query after completion and permit screenshots.

## How to download the Third-Apps Integration Kit?

To download the **Third-Apps Integration Kit**, navigate to the [Releases](https://github.com/mercadopago/point-smartapp-demo-android/releases) section. Choose the latest available version and access the assets to locate the *Third-Apps-integration-kit.zip*.
Upon unzipping the file, you'll find the following files:
- **nativesdk-mainapp-X.X.X.aar** : Library for importing into your project.
- **documentation-mainapp-SDK-X.X.X.zip** : Comprehensive documentation detailing available methods and classes of the SDK.
- **demo-mainapp-X.X.X.apk** : A demo application enabling testing of all SDK functionalities.

For additional details, check [the MainApp playbook](https://www.mercadopago.com.br/developers/es/docs/main-apps/landing)

## Screenshot previews

Paparazzi renders the production SSO and payment status screens without a device
or backend. SSO covers initial, processing, waiting for authorization, JWT returned,
and network error states. Payment status covers initial, loading, error, local result,
and online result states. Both use the N950 layout. Paparazzi saves the reference PNGs
at its default size of 500 × 1000 px.

Requires Java 17 and Android SDK 34, using the existing project wrapper and SDK
configuration (`ANDROID_HOME` or `sdk.dir` in the root `local.properties`).

```bash
./gradlew recordSnapshots  # Generate or update reference PNGs after an intentional UI change
./gradlew verifySnapshots  # Compare the current UI with the committed PNGs
```

- Reference PNGs: `app/src/test/snapshots/images/`.
- HTML report: `app/build/reports/paparazzi/index.html`.
- Visual differences: `app/build/paparazzi/failures/`.

`./gradlew test` and `./gradlew check` also verify the screenshots. A difference
fails verification; these tasks never accept changes to the reference PNGs.
Generating a PR does not run these commands automatically.

The tests live in `app/src/test/java/.../snapshot/`. `SnapshotConfiguration` shares
the device dimensions and theme for future screens. Only `mainappDebug` runs the
snapshots because both app flavors share the same UI. All added build dependencies
resolve from public repositories; no internal repositories or credentials are needed.

The JWT fixture is synthetic and has an invalid signature. Never replace it with a
real token. These tests check appearance only; they do not exercise SDK requests or
host authentication.
