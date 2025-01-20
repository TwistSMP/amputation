# Amputation Plugin

A Minecraft plugin that adds a unique twist to survival gameplay by requiring players to sacrifice a body part, each with its own unique challenges and effects.

## Features

### Limb Sacrifice System
Players must choose one of the following body parts to sacrifice:

- **Main Hand**
  - Applies Weakness III
  - Reduces block and entity interaction range to 0
  - Makes combat and building more difficult

- **Off Hand**
  - Applies Weakness III
  - Applies Mining Fatigue I
  - Disables off-hand usage
  - Prevents bow usage completely
  - 5% chance to drop held items when using them
  - Makes combat and tool usage more challenging

- **Eyes**
  - Applies permanent Blindness
  - Applies permanent Darkness
  - Makes navigation and combat extremely challenging
  - Reduces view distance to minimum (1 chunk)

- **Leg**
  - Applies Slowness III
  - Removes fall damage protection
  - Makes movement more challenging

- **Lung**
  - Drains hunger rapidly while sprinting (1 point every 0.5 seconds)
  - Drains oxygen faster underwater (1.5 bubbles every 0.25 seconds)
  - Additional hunger drain while swimming

- **Heart**
  - Reduces max health to 2 hearts
  - Disables natural regeneration
  - Prevents absorption effects

## Commands

- `/activateamputation` - Activates the plugin and forces all players to choose a limb
- `/chooselimb <player>` - Opens the limb selection GUI for a specific player
- `/untwist` - Removes the limb sacrifice effect from all players
- `/retwisttimer [hours]` - Starts a timer after which all players must re-choose their limbs
- `/retwisttimer end` - Ends the current re-twist timer
- `/amputationstatus` - Check if the plugin is active and see player statistics

## Permissions

- `amputation.activate` - Allows use of `/activateamputation`
- `amputation.untwist` - Allows use of `/untwist`
- `amputation.chooselimb` - Allows use of `/chooselimb`
- `amputation.retwisttimer` - Allows use of `/retwisttimer`
- `amputation.status` - Allows use of `/amputationstatus` (default: true)

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Use `/activateamputation` to start the game mode

## Configuration

The plugin saves its state in `config.yml`, maintaining the active status between server restarts.

## Notes

- Players cannot close the limb selection GUI without making a choice when the plugin is active
- Effects are persistent and will be reapplied if a player dies or logs out
- Players cannot drink milk to remove the effects
- The plugin can be deactivated using the `/untwist` command with appropriate permissions
- Re-twist timer allows for periodic resetting of all players' limb choices

## License

This plugin is licensed under the GNU General Public License v3.0 or later (GPL-3.0-or-later). This means:

- You are free to use, modify, and distribute this software
- If you distribute modified versions, you must also distribute them under GPL-3.0-or-later
- You must make the source code available when you distribute the software
- There is no warranty for this program

For the full license text, see the [LICENSE](LICENSE) file or visit [GNU GPL v3](https://www.gnu.org/licenses/gpl-3.0.en.html).
