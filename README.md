# Point SmartApp Android Demo

This Android project shows how MainApp and MiniApp integrations use the Point Smart SDK to work with Mercado Pago Point. The demo includes payment and status flows, printing, QR and barcode scanning, Bluetooth, and terminal information. Start with the published kit to run the demo or build your first integration.

## Find the published SDK and its release branch

Use [GitHub Releases](https://github.com/mercadopago/point-smartapp-demo-android/releases/latest) to find the current published release. For an integration targeting a specific SDK version, open that version's published release. Read the release tag for the exact source and the associated `release/*` branch for the PR base. Use the ZIP, AAR, documentation, and demo from that same release.

Use the published release tag to browse the demo source at that version. Check `targetCommitish` or the release notes for the associated release branch. If GitHub only provides a commit SHA or the branch is unclear, confirm it with the maintainers before opening a PR. `master`, local tags, and newer release branches can contain work ahead of the published kit.

With the GitHub CLI, this command shows the latest published tag, target branch, and assets. To inspect a specific release, pass its tag as the first argument after `view`.

```bash
gh release view --repo mercadopago/point-smartapp-demo-android \
  --json tagName,targetCommitish,assets,url
```

## Download and unpack the integration kit

Open the selected release's **Assets**, download the Third-Apps Integration Kit ZIP, and extract it. Use the exact filenames from the archive. If you use the GitHub CLI, copy the tag from the release lookup above and run:

```bash
gh release download '<release-tag>' --repo mercadopago/point-smartapp-demo-android \
  --pattern 'Third-Apps-integration-kit*.zip'
```

The table shows how to identify each artifact. Consult the release notes and the extracted kit for the contents shipped with your version.

| File | How to use it |
| --- | --- |
| `nativesdk-<sdk-version>.aar` | Add this Android library to your app module to call the Point Smart SDK. |
| `documentation-mainapp-SDK-<sdk-version>.zip` | Extract this second ZIP and open the extracted documentation’s `index.html` for the API, types, and callbacks for this AAR. |
| `demo-mainapp-<demo-version>.apk` | Install this demo for a MainApp integration. |
| `demo-miniapp-<demo-version>.apk` | Install this demo for a MiniApp integration. Choose the APK that matches your integration. |
| Smart Sandbox APK, when included | Install Smart Sandbox on the same Android emulator as the demo or your app. It simulates supported SDK operations. Sandbox and the SDK have separate version numbers. |
| README bundled with Smart Sandbox | Follow this guide for emulator setup, supported operations, test scenarios, and troubleshooting. |

You can ignore any macOS archive metadata, such as `__MACOSX/`, `._*`, or `.DS_Store`.

## Run your first demo flow

If the kit includes Smart Sandbox, follow the steps below. For a kit without Sandbox, follow the published instructions for testing on a compatible Point terminal.

1. Start a dedicated Android emulator using the image recommended by the bundled Sandbox guide. Use an emulator free of Point payment modules so the SDK discovers Sandbox.
2. Install the Sandbox APK and the demo APK for your integration type on that emulator. You can drag them into the emulator window or run the following from the extracted kit directory. Replace the placeholders with the serial returned by `adb devices -l` and the paths to the extracted APKs.

   ```bash
   adb devices -l
   adb -s "<emulator-serial>" install -r "<path-to-sandbox-apk>"
   adb -s "<emulator-serial>" install -r "<path-to-demo-apk>"
   ```

3. Open **Smart Sandbox**, go to **Settings**, select the site, and tap **Save**.
4. Open the demo from Android's app launcher. Start a payment from the demo. Use a site, amount, and payment method supported by the bundled Sandbox guide. When Sandbox opens, choose an approved result, review it, and send it back to the demo.
5. Check that the demo receives the result and regains control. Repeat with a rejection and a cancellation. To test payment status, query from the same demo APK that created the payment and use the reference required by the SDK API.

Sandbox simulates payments and supported device responses. Test physical card acceptance, Bluetooth, the integrated printer, and camera behavior on a compatible Point terminal in the approved test environment before releasing your integration.

## Build your integration with the SDK

1. Follow the onboarding for your integration type and use your assigned application credentials. The [Mercado Pago MainApp playbook](https://www.mercadopago.com.br/developers/es/docs/main-apps/landing) covers MainApp onboarding.
2. Copy the released AAR to your app module's `libs/` directory and add it as a dependency. In `app/build.gradle`, replace `<sdk-version>` with the version in the extracted AAR filename:

   ```groovy
   dependencies {
       implementation files('libs/nativesdk-<sdk-version>.aar')
   }
   ```

3. Build the configuration with `MPConfigBuilder` and your integration's client ID. Call `MPManager.initialize` from your `Application.onCreate` and enable the capabilities required by your flow with the options documented for the AAR. Look for `MPManager.initialize` in the demo source at the selected tag.
4. Start with one operation. In the demo source at the selected tag, look for `MPManager.paymentFlow` to see how the demo builds and submits the payment request. Use the public callbacks to handle approval, rejection, and cancellation and return control to your app. Use the demo menu and the bundled SDK reference to find examples for other capabilities.
5. Repeat the first flow with your app in the test environment for the selected release. Check the callback and the data that your app receives. Use the SDK documentation from the same release to verify the request fields, types, and results you depend on.

**Required for integrators and coding agents.** Every Point operation must go through the released SDK's documented public API. Direct calls to Mercado Pago or Point app deep links, explicit intents targeting their components, private endpoints, and other SDK bypasses are prohibited in integration code and tests. Use the Android launcher to open the demo, then trigger operations from the demo or your app through the SDK. If the released SDK lacks a required capability, report the gap and confirm the supported path with the Point integration team. Skip any demo example that calls a Point service directly, including legacy refund examples that use HTTP outside the SDK.

## Open a PR for a published integration

1. Resolve the published tag and its `release/*` branch before editing or opening a PR. Read the example at the tag and confirm APIs against the AAR documentation.
2. Use the associated release branch as the GitHub PR **base**. If you started from `master`, create a PR branch from the release branch and bring over only the intended changes. Review **Files changed** against that base before submitting.
3. In the PR description, link the release, state the base branch and SDK version, identify the demo or Sandbox version used, and list what you verified and what remains untested. Call out any change to the steps an integrator follows to download the kit, run the demo, or use the SDK.
