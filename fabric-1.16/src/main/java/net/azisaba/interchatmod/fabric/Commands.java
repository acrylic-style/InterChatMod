package net.azisaba.interchatmod.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.azisaba.interchatmod.common.model.Guild;
import net.azisaba.interchatmod.common.model.GuildMember;
import net.azisaba.interchatmod.common.util.Constants;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class Commands {
    public static final @NotNull Set<String> KNOWN_PLAYERS = new HashSet<>();

    public static LiteralArgumentBuilder<FabricClientCommandSource> builderGTell() {
        return literal("cgtell")
                .then(argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandSource.suggestMatching(KNOWN_PLAYERS.stream(), builder))
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Mod.client.tell(StringArgumentType.getString(ctx, "player"), StringArgumentType.getString(ctx, "message"));
                                    KNOWN_PLAYERS.add(StringArgumentType.getString(ctx, "player"));
                                    return 0;
                                })
                        )
                );
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> builderGS() {
        return ClientCommandManager.literal("cgs")
                .then(ClientCommandManager.argument("guild", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandSource.suggestMatching(Mod.GUILDS.stream().map(Guild::name), builder))
                        .executes(ctx -> executeFocus(ctx.getSource(), StringArgumentType.getString(ctx, "guild")))
                        .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> executeChat(ctx.getSource(), StringArgumentType.getString(ctx, "guild"), StringArgumentType.getString(ctx, "message")))
                        )
                );
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> builderG() {
        return ClientCommandManager.literal("cg")
                .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> executeChat(ctx.getSource(), null, StringArgumentType.getString(ctx, "message")))
                );
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> builderReconnectInterChat() {
        return ClientCommandManager.literal("reconnectinterchat")
                .executes(ctx -> {
                    Mod.reconnect();
                    return 0;
                })
                .then(ClientCommandManager.argument("apikey", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ModConfig.apiKey = StringArgumentType.getString(ctx, "apikey");
                            Mod.reconnect();
                            return 0;
                        })
                );
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> builderGuild() {
        return ClientCommandManager.literal("cguild")
                .then(ClientCommandManager.literal("invite")
                        .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    Mod.client.invite(StringArgumentType.getString(ctx, "player"));
                                    return 0;
                                })
                        )
                )
                .then(ClientCommandManager.literal("accept")
                        .then(ClientCommandManager.argument("guild", StringArgumentType.word())
                                .executes(ctx -> {
                                    Mod.client.respondInvite(StringArgumentType.getString(ctx, "guild"), true);
                                    return 0;
                                })
                        )
                )
                .then(ClientCommandManager.literal("reject")
                        .then(ClientCommandManager.argument("guild", StringArgumentType.word())
                                .executes(ctx -> {
                                    Mod.client.respondInvite(StringArgumentType.getString(ctx, "guild"), false);
                                    return 0;
                                })
                        )
                )
                .then(ClientCommandManager.literal("nick")
                        .executes(ctx -> {
                            Mod.client.nick(null);
                            return 0;
                        })
                        .then(ClientCommandManager.argument("nickname", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Mod.client.nick(StringArgumentType.getString(ctx, "nickname"));
                                    return 0;
                                })
                        )
                )
                .then(literal("jp-on")
                        .executes(ctx -> {
                            Mod.client.toggleTranslate(true);
                            return 0;
                        })
                )
                .then(literal("jp-off")
                        .executes(ctx -> {
                            Mod.client.toggleTranslate(false);
                            return 0;
                        })
                )
                .then(literal("role")
                        .then(argument("member", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    Set<GuildMember> set = Mod.guildMembers.getOrDefault(Mod.client.getSelectedGuild(), Collections.emptySet());
                                    return CommandSource.suggestMatching(set.stream().map(GuildMember::name), builder);
                                })
                                .then(argument("role", StringArgumentType.word())
                                        .suggests((ctx, builder) -> CommandSource.suggestMatching(Stream.of("owner", "moderator", "member"), builder))
                                        .executes(ctx -> {
                                            Mod.client.role(StringArgumentType.getString(ctx, "member"), StringArgumentType.getString(ctx, "role"));
                                            return 0;
                                        })
                                )
                        )
                )
                .then(literal("toggleinvites")
                        .executes(ctx -> {
                            Mod.client.toggleInvites();
                            return 0;
                        })
                )
                .then(literal("hide-guild")
                        .executes(ctx -> {
                            Mod.client.hideGuild();
                            return 0;
                        })
                )
                .then(literal("hideall")
                        .executes(ctx -> {
                            Mod.client.hideAll("");
                            return 0;
                        })
                        .then(argument("duration", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Mod.client.hideAll(StringArgumentType.getString(ctx, "duration"));
                                    return 0;
                                })
                        )
                )
                .then(literal("format")
                        .then(argument("format", StringArgumentType.greedyString())
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(Constants.FORMAT_VARIABLES.stream(), builder))
                                .executes(ctx -> {
                                    Mod.client.format(StringArgumentType.getString(ctx, "format"));
                                    return 0;
                                })
                        )
                )
                .then(literal("info").executes(ctx -> executeInfo(ctx.getSource())))
                .then(literal("block")
                        .then(argument("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    Mod.client.block(StringArgumentType.getString(ctx, "player"));
                                    return 0;
                                })
                        )
                )
                .then(literal("unblock")
                        .then(argument("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    Mod.client.unblock(StringArgumentType.getString(ctx, "player"));
                                    return 0;
                                })
                        )
                )
                .then(literal("tell")
                        .then(argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(KNOWN_PLAYERS.stream(), builder))
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            Mod.client.tell(StringArgumentType.getString(ctx, "player"), StringArgumentType.getString(ctx, "message"));
                                            KNOWN_PLAYERS.add(StringArgumentType.getString(ctx, "player"));
                                            return 0;
                                        })
                                )
                        )
                )
                ;
    }

    private static int executeInfo(FabricClientCommandSource source) {
        Guild guild = Mod.GUILDS.stream().filter(g -> g.id() == Mod.client.getSelectedGuild()).findAny().orElse(null);
        if (guild == null) {
            source.sendError(Text.of("無効なギルドが指定されています。/cgs (ギルド)で選択してください。"));
            return 0;
        }
        Set<GuildMember> members = Mod.guildMembers.getOrDefault(guild.id(), Collections.emptySet());
        source.sendFeedback(
                new LiteralText("--- ギルド").formatted(Formatting.GOLD)
                        .append(new LiteralText(guild.name()).formatted(Formatting.AQUA))
                        .append(new LiteralText("の情報 ---").formatted(Formatting.GOLD))
        );
        source.sendFeedback(
                new LiteralText("メンバー数: ").formatted(Formatting.GOLD)
                        .append(new LiteralText(String.valueOf(members.size())).formatted(Formatting.RED))
                        .append(new LiteralText("/").formatted(Formatting.GOLD))
                        .append(new LiteralText(String.valueOf(guild.capacity())).formatted(Formatting.RED))
        );
        Consumer<String> sendRole = (role) -> {
            String players =
                    members.stream()
                            .filter(m -> m.role().equals(role.toUpperCase(Locale.ROOT)))
                            .map(GuildMember::name)
                            .collect(Collectors.joining(", "));
            source.sendFeedback(
                    new LiteralText(role + ": ").formatted(Formatting.GOLD)
                            .append(new LiteralText(players).formatted(Formatting.WHITE))
            );
        };
        sendRole.accept("Owner");
        sendRole.accept("Moderator");
        sendRole.accept("Member");
        source.sendFeedback(Text.of(""));
        source.sendFeedback(
                new LiteralText("公開: ").formatted(Formatting.GOLD)
                        .append(new LiteralText(String.valueOf(guild.open())).formatted(guild.open() ? Formatting.GREEN : Formatting.RED))
        );
        source.sendFeedback(
                new LiteralText("チャット形式: ").formatted(Formatting.GOLD)
                        .append(new LiteralText(guild.format())
                                .formatted(Formatting.WHITE)
                                .styled(style ->
                                        style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("クリックでコピー")))
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, guild.format()))
                                )
                        )
        );
        return members.size();
    }

    private static int executeFocus(FabricClientCommandSource source, String guildName) {
        Guild guild = Mod.GUILDS.stream().filter(g -> g.name().equalsIgnoreCase(guildName)).findAny().orElse(null);
        if (guild == null) {
            source.sendError(Text.of("そんなぎるどないよ " + guildName));
            return 0;
        }
        Mod.client.selectGuild(guild.id());
        source.sendFeedback(Text.of(guild.name() + " にちゃっとするようにしたよ(/cg <めっせーじ>でできるよ)"));
        return 1;
    }

    private static int executeChat(FabricClientCommandSource source, String guildName, String message) {
        if (guildName != null) {
            Guild guild = Mod.GUILDS.stream().filter(g -> g.name().equalsIgnoreCase(guildName)).findAny().orElse(null);
            if (guild == null) {
                source.sendError(Text.of("そんなぎるどないよ " + guildName));
                return 0;
            }
            Mod.client.sendMessageToGuild(guild.id(), message);
        } else {
            Mod.client.sendMessageToGuild(null, message);
        }
        return 1;
    }
}
