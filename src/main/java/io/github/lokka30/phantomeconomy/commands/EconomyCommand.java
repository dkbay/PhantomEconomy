package io.github.lokka30.phantomeconomy.commands;

import io.github.lokka30.phantomeconomy.PhantomEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;

public class EconomyCommand implements CommandExecutor {

    private PhantomEconomy instance;

    public EconomyCommand(final PhantomEconomy instance) {
        this.instance = instance;
    }

    /*
    Reference: args.length and args[i]

    args.length:
    /economy(0) add(1) Notch(2) 23(3)

    args[i]:
    /economy(null) add(0) Notch(1) 23(2)
     */

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String label, @NotNull final String[] args) {
        if (sender.hasPermission("phantomeconomy.economy")) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "add":
                        if (sender.hasPermission("phantomeconomy.economy.add")) {
                            if (args.length == 3) {
                                if (instance.provider.hasAccount(args[1])) {
                                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                                    double amount;

                                    try {
                                        amount = Double.parseDouble(args[2]);
                                    } catch (NumberFormatException exception) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-double", "Invalid number - %arg% is not a valid number.")).replaceAll("%arg%", args[2]));
                                        return true;
                                    }

                                    if (amount < 0) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-negative", "Invalid number - %number% is negative.")).replaceAll("%number%", args[2]));
                                        return true;
                                    } else if (amount == 0) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-zero", "Invalid number - 0 is not allowed.")));
                                        return true;
                                    } else {
                                        instance.provider.depositPlayer(offlinePlayer, amount);
                                        sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.add.success", "Deposited %amount% to %player%'s balance.")).replaceAll("%amount%", Matcher.quoteReplacement(instance.provider.format(amount))).replaceAll("%player%", args[1]));
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(instance.colorize(instance.messages.get("common.target-never-played-before", "%player% hasn't joined the server before.")).replaceAll("%player%", args[1]));
                                    return true;
                                }
                            } else {
                                sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.add.usage", "/economy add <player> <amount>")));
                                return true;
                            }
                        } else {
                            sender.sendMessage(instance.colorize(instance.messages.get("common.no-permission", "You don't have access to that.")));
                            return true;
                        }
                    case "remove":
                        if (sender.hasPermission("phantomeconomy.economy.remove")) {
                            if (args.length == 3) {
                                if (instance.provider.hasAccount(args[1])) {
                                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                                    double amount;

                                    try {
                                        amount = Double.parseDouble(args[2]);
                                    } catch (NumberFormatException exception) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-double", "Invalid number - %arg% is not a valid number.")).replaceAll("%arg%", args[2]));
                                        return true;
                                    }

                                    if (amount < 0) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-negative", "Invalid number - %number% is negative.")).replaceAll("%number%", args[2]));
                                        return true;
                                    } else if (amount == 0) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-zero", "Invalid number - 0 is not allowed.")));
                                        return true;
                                    } else {
                                        if (instance.provider.has(offlinePlayer, amount)) {
                                            instance.provider.withdrawPlayer(offlinePlayer, amount);
                                            sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.remove.success", "Withdrew %amount% from %player%'s balance.")).replaceAll("%amount%", Matcher.quoteReplacement(instance.provider.format(amount))).replaceAll("%player%", args[1]));
                                        } else {
                                            sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.remove.not-enough-funds", "%player% doesn't have a balance equal to or greater than %amount%.")).replaceAll("%player%", args[1]).replaceAll("%amount%", Matcher.quoteReplacement(instance.provider.format(amount))));
                                        }
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(instance.colorize(instance.messages.get("common.target-never-played-before", "%player% hasn't joined the server before.")).replaceAll("%player%", args[1]));
                                    return true;
                                }
                            } else {
                                sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.remove.usage", "/economy remove <player> <amount>")));
                                return true;
                            }
                        } else {
                            sender.sendMessage(instance.colorize(instance.messages.get("common.no-permission", "You don't have access to that.")));
                            return true;
                        }
                    case "set":
                        if (sender.hasPermission("phantomeconomy.economy.set")) {
                            if (args.length == 3) {
                                if (instance.provider.hasAccount(args[1])) {
                                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                                    double amount;

                                    try {
                                        amount = Double.parseDouble(args[2]);
                                    } catch (NumberFormatException exception) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-double", "Invalid number - %arg% is not a valid number.")).replaceAll("%arg%", args[2]));
                                        return true;
                                    }

                                    if (amount < 0) {
                                        sender.sendMessage(instance.colorize(instance.messages.get("common.invalid-number-negative", "Invalid number - %number% is negative.")).replaceAll("%number%", args[2]));
                                    } else {
                                        instance.provider.withdrawPlayer(offlinePlayer, instance.provider.getBalance(offlinePlayer));
                                        instance.provider.depositPlayer(offlinePlayer, amount);
                                        sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.set.success", "Set %player%'s balance to %amount%.")).replaceAll("%amount%", Matcher.quoteReplacement(instance.provider.format(amount))).replaceAll("%player%", args[1]));
                                    }
                                } else {
                                    sender.sendMessage(instance.colorize(instance.messages.get("common.target-never-played-before", "%player% hasn't joined the server before.")).replaceAll("%player%", args[1]));
                                }
                            } else {
                                sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.set.usage", "/economy set <player> <amount>")));
                            }
                        } else {
                            sender.sendMessage(instance.colorize(instance.messages.get("common.no-permission", "You don't have access to that.")));
                        }
                        return true;
                    case "reset":
                        if (sender.hasPermission("phantomeconomy.economy.reset")) {
                            if (args.length == 2) {
                                if (instance.provider.hasAccount(args[1])) {
                                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                                    instance.provider.withdrawPlayer(offlinePlayer, instance.provider.getBalance(offlinePlayer));
                                    instance.provider.depositPlayer(offlinePlayer, instance.settings.get("default-balance.amount", 50.0));
                                    sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.reset.success", "Set %player%'s balance to the default balance.")).replaceAll("%player%", args[1]));
                                } else {
                                    sender.sendMessage(instance.colorize(instance.messages.get("common.target-never-played-before", "%player% hasn't joined the server before.")).replaceAll("%player%", args[1]));
                                }
                            } else {
                                sender.sendMessage(instance.colorize(instance.messages.get("commands.economy.reset.usage", "/economy reset <player>")));
                            }
                        } else {
                            sender.sendMessage(instance.colorize(instance.messages.get("common.no-permission", "You don't have access to that.")));
                            return true;
                        }
                        return true;
                    default:
                        for (String msg : instance.messages.get("commands.economy.usage", Arrays.asList("/economy add <player> <amount>", "/economy remove <player> <amount>", "/economy set <player> <amount>", "/economy reset <player>"))) {
                            sender.sendMessage(instance.colorize(msg));
                        }
                        return true;
                }
            } else {
                for (String msg : instance.messages.get("commands.economy.usage", Arrays.asList("/economy add <player> <amount>", "/economy remove <player> <amount>", "/economy set <player> <amount>", "/economy reset <player>"))) {
                    sender.sendMessage(instance.colorize(msg));
                }
                return true;
            }
        } else {
            sender.sendMessage(instance.colorize(instance.messages.get("common.no-permission", "You don't have access to that.")));
            return true;
        }
    }
}
