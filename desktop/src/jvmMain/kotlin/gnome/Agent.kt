package gnome

import core.Application
import kotlin.coroutines.CoroutineContext

fun agent(
    coroutineContext: CoroutineContext,
    platform: core.Platform = nix.Platform(coroutineContext),
    resources: core.Resources = default.Resources(coroutineContext),
    autostart: core.Autostart = Autostart(platform, resources),
) = Application(
    platform = platform,
    autostart = autostart,
    resources = resources,
)
