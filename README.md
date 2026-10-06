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

The **Single Sign-On** home action opens `SingleSignOnActivity`. Select **Request identity token** to call `MPManager.singleSignOnTools.requestIdentityToken` with a fresh UUID nonce. The loader and status text follow `onProgress`; `onSuccess` and `onError` hide the loader and enable the button for another request. Errors display their numeric code (`GenericErrorCode` / `SsoErrorCode`), including unrecognized host codes.

This example uses `nativesdk-EXPERIMENTAL-7.3.0-20261006142626.aar`, built from SDK master `25aabbe3`, and configures `withSingleSignOnTools()` before initializing `MPManager`. A compatible Point host and an authenticated Mercado Pago session are required; an unsupported host returns `NOT_SUPPORTED`.

The demo confirms token delivery only: it does not display, log, decode or persist the token or nonce, or create an integrator session. A real integration must establish the expected nonce with its trusted backend and send the token there to validate its signature, claims, and nonce binding and prevent replay. Screen recreation does not resume a pending request; the SDK rejects a second request while one is still running.

## How to download the Third-Apps Integration Kit?

To download the **Third-Apps Integration Kit**, navigate to the [Releases](https://github.com/mercadopago/point-smartapp-demo-android/releases) section. Choose the latest available version and access the assets to locate the *Third-Apps-integration-kit.zip*.
Upon unzipping the file, you'll find the following files:
- **nativesdk-mainapp-X.X.X.aar** : Library for importing into your project.
- **documentation-mainapp-SDK-X.X.X.zip** : Comprehensive documentation detailing available methods and classes of the SDK.
- **demo-mainapp-X.X.X.apk** : A demo application enabling testing of all SDK functionalities.

For additional details, check [the MainApp playbook](https://www.mercadopago.com.br/developers/es/docs/main-apps/landing)
