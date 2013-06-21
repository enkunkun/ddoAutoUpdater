package net.enkun.plugins.ddoautoupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{	
	FileConfiguration config;
	
	public void onEnable()
	{
		this.saveDefaultConfig();
		
		config = this.getConfig();
		
		String domain = config.getString("domain");
		String pass = config.getString("password");
		
		if (ddoUpdate(domain, pass))
		{
			this.getServer().getConsoleSender().sendMessage("[ddoAutoUpdater] " + ChatColor.GREEN + "ddo.jp update success!");
		}
		else
		{
			this.getServer().getConsoleSender().sendMessage("[ddoAutoUpdater] " + ChatColor.RED + "[ddoAutoUpdater] ddo.jp update failed.");
		}
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.getLogger().info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

	public void onDisable()
	{
		
	}

	public String getGlobalIP()
	{
		try
		{
			URL url = new URL("http://enkun.net/ip/ipcheck.php");

			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
			String ip = bufferedReader.readLine();
			
			bufferedReader.close();
			http.disconnect();
			
			return ip;
		}
		catch (IOException e)
		{
			return "";
		}
	}

	public boolean ddoUpdate(String domain, String pass)
	{
		if (domain.equals("example.ddo.jp") || pass.equals("example"))
			return false;
		
		try
		{
			String IP = getGlobalIP();
			this.getLogger().info(IP);
			
			URL url = new URL("http://free.ddo.jp/dnsupdate.php?dn=" + domain + "&pw=" + pass + "&ip=" + IP);

			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.connect();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "JISAutoDetect"));
			
			boolean result = false;
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				if (line.matches(".*SUCCESS.*"))
				{
					this.getLogger().info(line);
					result = true;
					break;
				}
				
				if (line.matches(".*FAIL.*"))
				{
					this.getLogger().info(line);
				}
			}
			
			bufferedReader.close();
			http.disconnect();
			
			return result;
		}
		catch (IOException e)
		{
			return false;
		}
	}

}
