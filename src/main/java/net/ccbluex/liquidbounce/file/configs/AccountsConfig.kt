/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.file.configs

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import me.liuli.elixir.account.MinecraftAccount
import me.liuli.elixir.manage.AccountSerializer
import net.ccbluex.liquidbounce.file.FileConfig
import net.ccbluex.liquidbounce.file.FileManager
import java.io.File

class AccountsConfig(file: File) : FileConfig(file) {
    val altManagerMinecraftAccounts: MutableList<MinecraftAccount> = ArrayList()

    override fun loadConfig(config: String) {
        val json = try {
            JsonParser().parse(config).asJsonArray
        } catch (e: JsonSyntaxException) {
            // convert old config
            JsonArray().also {
                config.split("\n").forEach { str ->
                    val information = str.split(":")
                    it.add(AccountSerializer.toJson(AccountSerializer.accountInstance(information[0], information[1])))
                }
            }
        }

        json.forEach { jsonElement ->
            if(!jsonElement.isJsonObject) {
                // not JsonObject it must be string
                val information = jsonElement.asString.split(":")
                AccountSerializer.accountInstance(information[0], information[1])
            } else {
                AccountSerializer.fromJson(jsonElement.asJsonObject)
            }.also {
                altManagerMinecraftAccounts.add(it)
            }
        }
    }

    override fun saveConfig(): String {
        val json = JsonArray()

        altManagerMinecraftAccounts.forEach {
            json.add(AccountSerializer.toJson(it))
        }

        return FileManager.PRETTY_GSON.toJson(json)
    }
}