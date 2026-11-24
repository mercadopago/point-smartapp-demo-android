# Point SmartApp Android Demo

This Android project shows how MainApp and MiniApp integrations use the Point Smart SDK to work with Mercado Pago Point. The demo includes payment and status flows, printing, QR and barcode scanning, Bluetooth, and terminal information. Start with the published kit to run the demo or build your first integration.

## Find the published SDK and its release branch

Use [GitHub Releases](https://github.com/mercadopago/point-smartapp-demo-android/releases/latest) to find the current published release. For an integration targeting a specific SDK version, open that version's published release. Read the release tag for the exact source and the associated `release/*` branch for the PR base. Use the ZIP, AAR, documentation, and demo from that same release.

The published release verified on November 24, 2026 is [`7.0.0`](https://github.com/mercadopago/point-smartapp-demo-android/releases/tag/7.0.0). GitHub records [`release/7.0`](https://github.com/mercadopago/point-smartapp-demo-android/tree/release/7.0) as its target branch, and the tag identifies the exact source for the kit. `master`, local tags, and newer release branches can contain work ahead of the published kit.

With the GitHub CLI, this command shows the latest published tag, target branch, and assets. To inspect a specific release, pass its tag as the first argument after `view`.

```bash
gh release view --repo mercadopago/point-smartapp-demo-android \
  --json tagName,targetCommitish,assets,url
```

## Download and unpack the integration kit

Open the release's **Assets**, download [`Third-Apps-integration-kit-7.0.0.zip`](https://github.com/mercadopago/point-smartapp-demo-android/releases/download/7.0.0/Third-Apps-integration-kit-7.0.0.zip), and extract it. The filenames below are from the published 7.0.0 kit.

| File | How to use it |
| --- | --- |
| `nativesdk-7.0.0.aar` | Add this Android library to your app module to call the Point Smart SDK. |
| `documentation-mainapp-SDK-7.0.0.zip` | Extract this second ZIP and open `documentation-mainapp-SDK-7.0.0/index.html` for the API, types, and callbacks for this AAR. |
| `demo-mainapp-7.0.0.apk` | Install this demo for a MainApp integration. |
| `demo-miniapp-7.0.0.apk` | Install this demo for a MiniApp integration. Choose the APK that matches your integration. |
| `emulator sandbox  for testing/[ispi-sandbox-android]-v1.1.0-release-universal.apk` | Install Smart Sandbox 1.1.0 on the same Android emulator as the demo or your app. It simulates supported SDK operations. Sandbox and the SDK have separate version numbers. |
| `emulator sandbox  for testing/README.md` | Follow this guide for emulator setup, supported operations, test scenarios, and troubleshooting. |

The archive also contains `__MACOSX/`, `._*`, and `.DS_Store` metadata from macOS. You can ignore those files.

## Run your first demo flow

1. Start a dedicated Android emulator. The Sandbox guide recommends Android 12 (API 31) and an image matching your host architecture. Use an emulator free of Point payment modules so the SDK discovers Sandbox.
2. Install the Sandbox APK and the demo APK for your integration type on that emulator. You can drag them into the emulator window or run the following from the extracted kit directory. Replace `emulator-5554` with the serial shown by `adb devices -l`.

   ```bash
   adb devices -l
   adb -s emulator-5554 install -r "emulator sandbox  for testing/[ispi-sandbox-android]-v1.1.0-release-universal.apk"
   adb -s emulator-5554 install -r demo-mainapp-7.0.0.apk
   ```

3. Open **Smart Sandbox**, go to **Settings**, select the site, and tap **Save**.
4. Open the demo from Android's app launcher. Start a payment from the demo. For example, select site **MLA** and request a card payment for **1500 ARS** with taxes and automatic printing disabled. When Sandbox opens, choose an approved result, review it, and send it back to the demo.
5. Check that the demo receives the result and regains control. Repeat with a rejection and a cancellation. To test payment status, query from the same demo APK that created the payment and use the reference required by the SDK API.

Sandbox simulates payments and supported device responses. Test physical card acceptance, Bluetooth, the integrated printer, and camera behavior on a compatible Point terminal in the approved test environment before releasing your integration.

## Build your integration with the SDK

1. Follow the onboarding for your integration type and use your assigned application credentials. The [Mercado Pago MainApp playbook](https://www.mercadopago.com.br/developers/es/docs/main-apps/landing) covers MainApp onboarding.
2. Copy the released AAR to your app module's `libs/` directory and add it as a dependency. For example, in `app/build.gradle` with Groovy:

   ```groovy
   dependencies {
       implementation files('libs/nativesdk-7.0.0.aar')
   }
   ```

3. Build the configuration with `MPConfigBuilder` and your integration's client ID. Call `MPManager.initialize` from your `Application.onCreate` and enable the capabilities required by your flow with the options documented for the AAR. See the [released `MainApplication.kt`](https://github.com/mercadopago/point-smartapp-demo-android/blob/7.0.0/app/src/main/java/com/mercadopago/android/point_smartapp_demo/app/MainApplication.kt).
4. Start with one operation. The [released `PaymentLauncherActivity.kt`](https://github.com/mercadopago/point-smartapp-demo-android/blob/7.0.0/app/src/main/java/com/mercadopago/android/point_smartapp_demo/app/view/payment/launcher/PaymentLauncherActivity.kt) builds the payment request and passes it to `MPManager.paymentFlow`. Use the public callbacks to handle approval, rejection, and cancellation and return control to your app. [`ActionsProviderImpl.kt`](https://github.com/mercadopago/point-smartapp-demo-android/blob/7.0.0/app/src/main/java/com/mercadopago/android/point_smartapp_demo/app/ActionsProviderImpl.kt) maps demo menu items to the examples for other capabilities.
5. Install your app alongside Sandbox and repeat the first flow. Check the callback and the data that your app receives. Use the SDK documentation from the same release to verify the request fields, types, and results you depend on.

**Required for integrators and coding agents.** Every Point operation must go through the released SDK's documented public API. Direct calls to Mercado Pago or Point app deep links, explicit intents targeting their components, private endpoints, and other SDK bypasses are prohibited in integration code and tests. Use the Android launcher to open the demo, then trigger operations from the demo or your app through the SDK. If the released SDK lacks a required capability, report the gap and confirm the supported path with the Point integration team. The 7.0.0 demo's direct HTTP refund example is outside this SDK-only contract and must be skipped.

## Open a PR for a published integration

1. Resolve the published tag and its `release/*` branch before editing or opening a PR. Read the example at the tag and confirm APIs against the AAR documentation.
2. Use the associated release branch as the GitHub PR **base**. For the 7.0.0 kit, select `release/7.0`. If you started from `master`, create a PR branch from the release branch and bring over only the intended changes. Review **Files changed** against that base before submitting.
3. In the PR description, link the release, state the base branch and SDK version, identify the demo or Sandbox version used, and list what you verified and what remains untested. Call out any change to the steps an integrator follows to download the kit, run the demo, or use the SDK.
