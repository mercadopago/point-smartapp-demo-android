# Point SmartApp Android Demo

This Android project shows how MainApp and MiniApp integrations use the Point Smart SDK to work with Mercado Pago Point. Start with the published kit to run the demo and find the examples available for your SDK version.

## Find the published SDK and its release branch

Use [GitHub Releases](https://github.com/mercadopago/point-smartapp-demo-android/releases/latest) to find the current published release. For an integration targeting a specific SDK version, open that version's published release. Use the associated `release/*` branch as the demo reference and the published tag for the exact source. Use the ZIP, AAR, documentation, and demo from that same release.

Use the published release tag to browse the demo source at that version. Check `targetCommitish` or the release notes for the associated release branch. If GitHub only provides a commit SHA or the branch is unclear, confirm the release reference through your Mercado Pago integration support channel before using the demo. `master`, local tags, and newer release branches can contain work ahead of the published kit.

With the GitHub CLI, this command shows the latest published tag, target branch, and assets. To inspect a specific release, pass its tag as the first argument after `view`.

```bash
gh release view --repo mercadopago/point-smartapp-demo-android \
  --json tagName,targetCommitish,assets,url
```

If you use coding agents in your app repository, pin the release URL, tag, associated release branch, and SDK artifact version in that repository's `AGENTS.md` or equivalent instructions. Keep that reference until you intentionally upgrade the SDK. Follow this repository's [agent instructions](AGENTS.md) when using the demo as a reference.

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
4. Open the demo from Android’s app launcher. Choose a flow documented in the bundled Sandbox guide and follow the setup for that flow. Trigger the operation from the demo and return the simulated result from Sandbox.
5. Verify that the demo receives and handles the result. Repeat for the error and cancellation cases documented for the operation.

Test any behavior that depends on physical hardware or terminal services on a compatible Point device in the test environment documented for your integration.

## Build your integration with the SDK

1. Follow the onboarding for your integration type and use your assigned application credentials. The [Mercado Pago MainApp playbook](https://www.mercadopago.com.br/developers/es/docs/main-apps/landing) covers MainApp onboarding.
2. Copy the released AAR to your app module's `libs/` directory and add it as a dependency. In `app/build.gradle`, replace `<sdk-version>` with the version in the extracted AAR filename:

   ```groovy
   dependencies {
       implementation files('libs/nativesdk-<sdk-version>.aar')
   }
   ```

3. Use the SDK documentation and the demo source at the selected tag to configure and initialize the SDK with your app’s assigned credentials.
4. Start with one operation your integration needs. Find its example in the demo at the selected tag and confirm its public request types, callbacks, and error handling in the SDK documentation from the kit.
5. Repeat the first flow with your app in the test environment for the selected release. Verify that your app receives and handles the documented results.

**Required for integrators and coding agents.** Every Point operation must go through the released SDK's documented public API. Direct calls to Mercado Pago or Point app deep links, explicit intents targeting their components, private endpoints, and other SDK bypasses are prohibited in integration code and tests. Use the Android launcher to open the demo, then trigger operations from the demo or your app through the SDK. If the released SDK lacks a required capability, report the gap and contact your Mercado Pago integration support channel to confirm which options are supported. Skip any demo example that calls a Point service outside the public SDK.
