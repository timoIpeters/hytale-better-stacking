# Hytale Better Stacking Mod

With this mod, you can adjust the inventory flow to automatically route picked-up items or entire item stacks to existing
stacks in the offhand or backpack.

## Base Features

- **Item Routing:** Automatically move items to your offhand (utility bar) or backpack based on your personal configuration
- **Two Stacking Modes:**
  - **Partial (Default):** Only adds new pickups to the existing stack in your offhand/backpack
  - **Full:** On pickup, moves the entire stack from your main inventory to the existing stack in your offhand/backpack
- **Per-Slot Configurations:** Configure your offhand and backpack independently. You can have the offhand on "Partial" and the backpack on "Full" simultaneously
  - Configured via commands ([See "Commands" section](#commands))
- **Persistent Settings:** Your configurations are saved to your player profile and will persist across server restarts or re-joins


## Commands
Use the `/stacking` command to manage your settings:

- `/stacking <slot>` : Toggles stacking for the specified slot (`offhand` or `backpack`) using the Partial mode
- `/stacking <slot> --full`: Toggles stacking for the specified slot using the Full mode

## How to Use
The mod can be downloaded from [Curseforge]() or [Nexusmods](https://www.nexusmods.com/hytale/mods/54).
Just add the `.jar` to your server's mods folder (or directly install on your server using the Curseforge UI).

After installing the mod, the first time you join the server, the settings for both slots (offhand and backpack) are initialized
with the feature *enabled* in *partial* mode. You can always use the [commands above](#commands) to adjust the configuration.

## Possible Future Adjustments

- Add settings menu UI to adjust per-slot configurations without commands
- Add priority system to choose whether the offhand or backpack should be checked first when both are enabled

Feel free to reach out if you have any questions or feature/improvement ideas.
