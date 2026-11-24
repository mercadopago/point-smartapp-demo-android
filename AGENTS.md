# Point SmartApp integration reference

Apply these rules when using this repository to build, modify, test, or review an Android integration with Mercado Pago Point.

1. **Use a published release.** For an existing integration, use its pinned SDK release. For a new integration without a pinned version, use the latest published release that provides an integration kit. Resolve the tag and its associated `release/*` branch from [GitHub Releases](https://github.com/mercadopago/point-smartapp-demo-android/releases). A branch or tag without a published kit is insufficient. If the release branch or kit cannot be verified, stop and report what is missing.
2. **Use the released demo as the source reference.** Find the associated release branch, then read the demo at the published tag to match the exact kit. If your checkout is on `master` or any other development branch, resolve the release before reading or copying demo implementation. Use the SDK dependency, documentation, and demo identified by that same release. Use the release’s notes and kit guide to find and install them.
3. **Call Point only through the SDK.** Use the public API documented for the selected release for all Point operations, including tests. Direct deep links into Mercado Pago or Point apps, explicit intents targeting their components, private endpoints, and other SDK bypasses are prohibited. If the released SDK lacks a capability, report the gap.
4. **Record the reference in the integrator's repository.** Add the published release URL, tag, release branch, and exact SDK artifact version to the integrator's `AGENTS.md` or equivalent agent instructions. Keep using that pinned release until the integration is explicitly upgraded.

See the [README](README.md) for release lookup, kit contents, and the first demo flow.
