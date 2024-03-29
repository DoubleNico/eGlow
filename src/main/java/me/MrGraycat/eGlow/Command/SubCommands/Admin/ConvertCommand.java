package me.MrGraycat.eGlow.Command.SubCommands.Admin;

import me.MrGraycat.eGlow.Command.SubCommand;
import me.MrGraycat.eGlow.Config.Playerdata.EGlowPlayerdataManager;
import me.MrGraycat.eGlow.Manager.Interface.IEGlowPlayer;
import me.MrGraycat.eGlow.Util.Text.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class ConvertCommand extends SubCommand {
	private boolean keepActive = false;
	private int counter = 0;
	
	@Override
	public String getName() {
		return "convert";
	}

	@Override
	public String getDescription() {
		return "Convert YML stored playerdata to new DB system";
	}

	@Override
	public String getPermission() {
		return "eglow.command.convert";
	}

	@Override
	public String[] getSyntax() {
		return new String[] {"/eGlow convert <delay>", "/eGlow convert stop"};
	}

	@Override
	public boolean isPlayerCmd() {
		return false;
	}

	@Override
	public void perform(CommandSender sender, IEGlowPlayer ePlayer, String[] args) {
		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("stop")) {
				ChatUtil.sendPlainMsg(sender, "&fConverting has been &cstopped&f. " + ((!keepActive) ? "(&cno conversion started&f)" : ""), true);
				keepActive = false;
			} else {
				if (keepActive) {
					ChatUtil.sendMsg(sender, "&fConversion already in progress!", true);
					return;
				}
				
				try {
					int delay = Integer.parseInt(args[1]);
					
					File playerFolder = new File(getInstance().getDataFolder() + File.separator + "PlayerData");
					YamlConfiguration playerConfig = new YamlConfiguration();
					File[] files;
					
					if (playerFolder.exists()) {
						files = playerFolder.listFiles();
					} else {
						ChatUtil.sendMsg(sender, "There's nothing left to convert!", true);
						return;
					}

					if (files == null)
						return;

					if (delay > 0 && delay <= 10) {
						ChatUtil.sendPlainMsg(sender, "&fStarting to convert &e" + files.length + "&fentries at a rate of &e" + delay + "s&f/entry", true);
						keepActive = true;
						
						new BukkitRunnable() {
							@Override
							public void run() {
								try {
									if (!keepActive) cancel();
									if (counter >= files.length - 1) {
										ChatUtil.sendPlainMsg(sender, "&fFinished conversion of &e" + files.length + "&fentries.", true);
										playerFolder.delete();
										keepActive = false;
										cancel();
									}

									playerConfig.load(new File(getInstance().getDataFolder() + File.separator + "/PlayerData/" + File.separator + files[counter].getName()));

									EGlowPlayerdataManager.savePlayerdata(files[counter].getName().replace(".yml", ""), playerConfig.getString("lastGlowData"), playerConfig.getBoolean("glowOnJoin"), playerConfig.getBoolean("activeOnQuit"), playerConfig.getString("glowVisibility"), playerConfig.getString("glowDisableReason"));
									File file = files[counter];
									file.delete();
									
									counter++;
								} catch (IOException | InvalidConfigurationException e) {
									//Ignored to prevent error spam
								}
							}
						}.runTaskTimerAsynchronously(getInstance(), 0L, delay * 20);
					} else {
						ChatUtil.sendMsg(sender, " &fUse a number from &e1 &fto &e10&f.", true);
					}
				} catch (NumberFormatException e) {
					ChatUtil.sendMsg(sender, "&e" + args[0] + " &fisn't a valid delay&f!", true);
				}	
			}
		} else {
			sendSyntax(sender, getSyntax()[0], true);
			sendSyntax(sender, getSyntax()[1], true);
		}
	}
}