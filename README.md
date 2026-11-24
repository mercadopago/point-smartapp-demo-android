# Point SmartApp Android Demo

This Android project shows how MainApp and MiniApp integrations use the Point Smart SDK to work with Mercado Pago Point. Start with the published kit to run the demo and find the examples available for your SDK version.

## Find the published SDK and its release branch

For an integration with a pinned SDK version, open that version’s published release. For a new integration, choose the latest published release that provides an integration kit. Use the associated `release/*` branch as the demo reference and the published tag for the exact source. Use the SDK, documentation, and demo identified by that same release. [GitHub Releases](https://github.com/mercadopago/point-smartapp-demo-android/releases)

Use the published release tag to browse the demo source at that version. Check `targetCommitish` or the release notes for the associated release branch. If GitHub only provides a commit SHA or the branch is unclear, confirm the release reference through your Mercado Pago integration support channel before using the demo. `master`, local tags, and newer release branches can contain work ahead of the published kit.

With the GitHub CLI, this command shows the latest published tag, target branch, and assets. To inspect a specific release, pass its tag as the first argument after `view`.

```bash
gh release view --repo mercadopago/point-smartapp-demo-android \
  --json tagName,targetCommitish,assets,url
```

If you use coding agents in your app repository, pin the release URL, tag, associated release branch, and SDK artifact version in that repository's `AGENTS.md` or equivalent instructions. Keep that reference until you intentionally upgrade the SDK. Follow this repository's [agent instructions](AGENTS.md) when using the demo as a reference.

## Find the kit and its contents

In the selected release’s **Assets**, download and extract `Third-Apps-integration-kit-<version>.zip`. To download it with the GitHub CLI, replace `<release-tag>` with the tag you selected.

```bash
gh release download '<release-tag>' --repo mercadopago/point-smartapp-demo-android \
  --pattern 'Third-Apps-integration-kit-*.zip'
```

Use the release notes and any guide or manifest included in the kit to locate each component. The kit contents and installation steps depend on the selected release. If you cannot identify its SDK, documentation, or demo, verify the reference before proceeding.

| Component | How to use it |
| --- | --- |
| SDK dependency | Add the exact Android dependency identified by the release using its installation instructions. |
| SDK documentation | Use the reference for that version to check setup, supported operations, request types, and results. |
| Demo | Use the demo artifact or build instructions identified by the release. If the kit offers separate demos, choose the one for your integration type. |
| Test tools and instructions | Follow the release’s guide for installing and configuring any tools it provides, such as Smart Sandbox. |

## Run your first demo flow

1. Follow the selected release’s instructions to prepare the test environment and install or build the demo. Use Smart Sandbox when the release provides it for the flow you want to test. Follow the terminal setup in the published guide for flows that require Point hardware.
2. Open the demo as described in the guide and trigger a documented operation through the SDK. If you are using a simulator, return the result as directed by its guide.
3. Verify that the demo receives and handles the result. Repeat for the error and cancellation cases documented for the operation.

## Build your integration with the SDK

1. Follow the onboarding for your integration type and use your assigned application credentials. The [Mercado Pago MainApp playbook](https://www.mercadopago.com.br/developers/es/docs/main-apps/landing) covers MainApp onboarding.
2. Add the SDK dependency using the installation method and exact version documented in the selected release. If the SDK is supplied as a local Android library, use the actual file from the kit and check the demo’s build configuration at the published tag for an example.
3. Use the SDK documentation and the demo source at the selected tag to configure and initialize the SDK with your app’s assigned credentials.
4. Start with one operation your integration needs. Find its example in the demo at the selected tag and confirm its public request types, callbacks, and error handling in the SDK documentation from the kit.
5. Repeat the first flow with your app in the test environment for the selected release. Verify that your app receives and handles the documented results.

**Required for integrators and coding agents.** Every Point operation must go through the released SDK's documented public API. Direct calls to Mercado Pago or Point app deep links, explicit intents targeting their components, private endpoints, and other SDK bypasses are prohibited in integration code and tests. Follow the release’s instructions to open the demo, then trigger operations from the demo or your app through the SDK. If the released SDK lacks a required capability, report the gap and contact your Mercado Pago integration support channel to confirm which options are supported. Skip any demo example that calls a Point service outside the public SDK.
