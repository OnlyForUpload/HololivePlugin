package tech.ice.plugins.HololivePlugin.paper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.api.shop.Shop;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;

public class Renew implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (commandSender instanceof Player) return false;

        ArrayList<Shop> shops = Utils.getShops();

        ArrayList<Map.Entry<String, Double>> sortedItems = new ArrayList<>();

        ArrayList<Map.Entry<Shop, Double>> dropshops = new ArrayList<>();
        ArrayList<Map.Entry<Shop, Double>> riseshops = new ArrayList<>();
        ArrayList<Map.Entry<Shop, Double>> zeroshops = new ArrayList<>();

        File item = new File(Main.HololivePlugin.getDataFolder() + "/items.yml");
        YamlConfiguration items = YamlConfiguration.loadConfiguration(item);
        ConfigurationSection section = items.getConfigurationSection("");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(key);
                if (itemSection != null) {
                    double price = itemSection.getDouble("Prices");
                    sortedItems.add(new AbstractMap.SimpleEntry<>(key, price));
                }
            }
        }

        sortedItems.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        int count = -1;

        for (Map.Entry<String, Double> entry : sortedItems) {
            if (count == -6) {
                break;
            }

            for (Shop shop : shops) {
                if (shop == null) {
                    System.out.println("null");
                    continue;
                }

                if (shop.getItem().getType().toString().equals(entry.getKey())) {
                    switch (count) {
                        case -1 -> {
                            double origin = shop.getPrice();
                            double update = Math.floor(origin * 0.75);
                            shop.setPrice(update);
                            dropshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -2 -> {
                            double origin = shop.getPrice();
                            double update = Math.floor(origin * 0.80);
                            shop.setPrice(update);
                            dropshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -3 -> {
                            double origin = shop.getPrice();
                            double update = Math.floor(origin * 0.85);
                            shop.setPrice(update);
                            dropshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -4 -> {
                            double origin = shop.getPrice();
                            double update = Math.floor(origin * 0.90);
                            shop.setPrice(update);
                            dropshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -5 -> {
                            double origin = shop.getPrice();
                            double update = Math.floor(origin * 0.95);
                            shop.setPrice(update);
                            dropshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                    }
                    break;
                }
            }

            count--;
        }

        for (int i = sortedItems.size() - 1; i >= 0; i--) {
            if (count == 0) {
                break;
            }

            Map.Entry<String, Double> entry = sortedItems.get(i);

            for (Shop shop : shops) {
                if (shop == null) {
                    System.out.println("null");
                    continue;
                }

                if (shop.getItem().getType().toString().equals(entry.getKey())) {
                    switch (count) {
                        case -5 -> {
                            double origin = shop.getPrice();
                            double update = Math.ceil(origin * 1.25);
                            shop.setPrice(update);
                            riseshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -4 -> {
                            double origin = shop.getPrice();
                            double update = Math.ceil(origin * 1.20);
                            shop.setPrice(update);
                            riseshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -3 -> {
                            double origin = shop.getPrice();
                            double update = Math.ceil(origin * 1.15);
                            shop.setPrice(update);
                            riseshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -2 -> {
                            double origin = shop.getPrice();
                            double update = Math.ceil(origin * 1.10);
                            shop.setPrice(update);
                            riseshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                        case -1 -> {
                            double origin = shop.getPrice();
                            double update = Math.ceil(origin * 1.05);
                            shop.setPrice(update);
                            riseshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
                        }
                    }
                    break;
                }
            }

            count++;
        }

        for (Shop shop : shops) {
            if (shop == null) {
                System.out.println("null");
                continue;
            }

            if (!items.contains(shop.getItem().getType().toString())) {
                double origin = shop.getPrice();
                double update = Math.ceil(origin * 1.25);
                shop.setPrice(update);
                zeroshops.add(new AbstractMap.SimpleEntry<>(shop, origin));
            }
        }

        item.delete();

        Main.HololivePlugin.getServer().getScheduler().runTaskLaterAsynchronously(Main.HololivePlugin, () -> {
            JsonObject locale;

            try {
                URL url = new URL("https://raw.githubusercontent.com/YTiceice/LangLib/main/lang/zh_tw.json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                locale = new Gson().fromJson(br, JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TextChannel channel = DiscordUtil.getTextChannelById("1090291234070859826");

            EmbedBuilder drop = new EmbedBuilder();
            EmbedBuilder rise = new EmbedBuilder();
            EmbedBuilder zero = new EmbedBuilder();

            drop.setColor(new Color(0, 255, 0));
            drop.setTitle("本次股市調降商品");

            rise.setColor(new Color(0, 255, 255));
            rise.setTitle("本次股市調漲商品");

            zero.setColor(new Color(255, 255, 0));
            zero.setTitle("本次股市未售出商品");
            zero.setFooter("Hololive Fanclub Server","https://cdn.discordapp.com/attachments/1076743513590480958/1084804566056456272/C2E8FC72-A01D-4B6D-9533-E9AB38A8B53E.jpg");
            zero.setTimestamp(Instant.now());

            for (Map.Entry<Shop, Double> dropshop : dropshops) {
                JsonElement json = locale.get("minecraft:" + dropshop.getKey().getItem().getType().toString().toLowerCase());
                drop.addField(json == null ? dropshop.getKey().getItem().getType().toString().toLowerCase() : json.getAsString(), dropshop.getValue() + " --> " + dropshop.getKey().getPrice(), true);
            }

            for (Map.Entry<Shop, Double> riseshop : riseshops) {
                JsonElement json = locale.get("minecraft:" + riseshop.getKey().getItem().getType().toString().toLowerCase());
                rise.addField(json == null ? riseshop.getKey().getItem().getType().toString().toLowerCase() : json.getAsString(), riseshop.getValue() + " --> " + riseshop.getKey().getPrice(), true);
            }

            for (Map.Entry<Shop, Double> zeroshop : zeroshops) {
                JsonElement json = locale.get("minecraft:" + zeroshop.getKey().getItem().getType().toString().toLowerCase());
                zero.addField(json == null ? zeroshop.getKey().getItem().getType().toString().toLowerCase() : json.getAsString(), zeroshop.getValue() + " --> " + zeroshop.getKey().getPrice(), true);
            }

            if (channel != null) {
                channel.sendMessageEmbeds(drop.build(), rise.build(), zero.build()).allowedMentions(EnumSet.of(Message.MentionType.ROLE)).content("<@&1099328890364776530>").queue();
            }
        }, 100L);

        return true;
    }
}
