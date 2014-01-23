package com.mcdr.corruption.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import com.mcdr.corruption.Corruption;
import com.mcdr.corruption.util.Utility;
import com.mcdr.corruption.player.CorPlayer;

public class CorPlayerFileAccessor implements Runnable {
	private static final String filePath = Corruption.in.getDataFolder().getPath() + File.separator + "players.dat";
		private static final String seperator = ":";
		private static final Pattern pattern = Pattern.compile(seperator);
		private Thread runner;
		private volatile boolean running;
		private Lock saveLock = new ReentrantLock();
		private Lock loadLock = new ReentrantLock();
		private Condition saveCondition = this.saveLock.newCondition();
		private List<CorPlayer> playersToSave = new ArrayList<CorPlayer>();
		private Map<Short, List<CorPlayer>> playersToLoad = new HashMap<Short, List<CorPlayer>>();
		private Map<Short, List<CorPlayer>> loadedPlayers = new HashMap<Short, List<CorPlayer>>();

		public void run()
		{
			while (this.running) {
				this.saveLock.lock();
				try
				{
					if (!this.playersToSave.isEmpty()) {
						savePlayers();
						this.playersToSave.clear();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					this.saveCondition.signal();
					this.saveLock.unlock();
				}

				this.loadLock.lock();
				try
				{
					if (!this.playersToLoad.isEmpty()) {
						loadPlayers();
						this.playersToLoad.clear();
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					this.loadLock.unlock();
				}
				try
				{
					Thread.sleep(100L);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			synchronized (this) {
				this.runner = null;
			}
		}

		private void savePlayers() throws Exception {
			String endLine = System.getProperty("line.separator");
			File file = getFile();
			File tempFile = new File(file.getPath() + ".temp");

			Utility.fileToFile(file, tempFile);

			Scanner scanner = new Scanner(new FileInputStream(tempFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
			String nameInFile;
			String playerName;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line != endLine)
				{
					nameInFile = line.substring(0, line.indexOf(seperator));

					for (CorPlayer CorPlayer : this.playersToSave) {
						playerName = CorPlayer.getName();

						if (nameInFile.equals(playerName)) {
							writer.write(playerName + seperator + CorPlayer.getCorPlayerData().getViewer() + seperator + CorPlayer.getCorPlayerData().getIgnore() + seperator);

							Map<String, Integer> bossesKilled = CorPlayer.getBossesKilled();

							for (Entry<String, Integer> bossKilled : bossesKilled.entrySet()) {
								writer.write(bossKilled.getKey() + seperator + bossKilled.getValue() + seperator);
							}

							writer.newLine();

							this.playersToSave.remove(CorPlayer);
							break;
						}

					}
				}
			}
			scanner.close();

			for (CorPlayer CorPlayer : this.playersToSave) {
				writer.write(CorPlayer.getName() + seperator + CorPlayer.getCorPlayerData().getViewer() + seperator + CorPlayer.getCorPlayerData().getIgnore() + seperator);

				Map<String, Integer> bossesKilled = CorPlayer.getBossesKilled();

				for (Entry<String, Integer> bossKilled : bossesKilled.entrySet()) {
					writer.write(bossKilled.getKey() + seperator + bossKilled.getValue() + seperator);
				}

				writer.newLine();
			}

			writer.close();
		}

		private void loadPlayers() throws IOException {
			File file = getFile();
			String line;
			String name;
			CorPlayer CorPlayer;

			for (Entry<Short, List<CorPlayer>> entry : this.playersToLoad.entrySet()) {
				Scanner scanner = new Scanner(new FileInputStream(file));
				List<CorPlayer> toLoad = entry.getValue();
				List<CorPlayer> loaded = new ArrayList<CorPlayer>();

				if (toLoad == null) {
					while (scanner.hasNextLine()) {
						line = scanner.nextLine();
						name = line.substring(0, line.indexOf(seperator));
						CorPlayer = new CorPlayer(Corruption.in.getServer().getOfflinePlayer(name));

						setData(line, CorPlayer);
						loaded.add(CorPlayer);
					}
				}
				else {
					while (scanner.hasNextLine())
					{
						line = scanner.nextLine();
						name = line.substring(0, line.indexOf(seperator));

						Iterator<CorPlayer> localIterator2 = toLoad.iterator(); if (localIterator2.hasNext()) { CorPlayer = localIterator2.next();
							if (name.equalsIgnoreCase(CorPlayer.getName()))
							{
								setData(line, CorPlayer);
								toLoad.remove(CorPlayer);
								loaded.add(CorPlayer);
							}
						}
					}
				}

				this.loadedPlayers.put(entry.getKey(), loaded);
				scanner.close();
			}
		}

		private File getFile() throws IOException {
			File file = new File(filePath);

			if (!file.exists()) {
				File parentFile = file.getParentFile();

				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}

				file.createNewFile();
			}

			return file;
		}

		private void setData(String line, CorPlayer CorPlayer) {
			String[] data = pattern.split(line);

			if (data.length >= 3) {
				CorPlayer.getCorPlayerData().setViewer(Boolean.valueOf(data[1]));
				CorPlayer.getCorPlayerData().setIgnore(Boolean.valueOf(data[2]));
			}

			for (int i = 3; i < data.length; i += 2)
				CorPlayer.addBossKilled(data[i], Integer.valueOf(data[(i + 1)]));
		}

		public boolean initiatePlayerDataSaving(List<CorPlayer> CorPlayersToSave)
		{
			if (this.saveLock.tryLock()) {
				try {
					this.playersToSave = new ArrayList<CorPlayer>(CorPlayersToSave);

					return true;
				}
				finally {
					this.saveLock.unlock();
				}
			}

			return false;
		}

		public void forcePlayerDataSaving(List<CorPlayer> CorPlayersToSave) {
			this.saveLock.lock();
			try
			{
				this.playersToSave = new ArrayList<CorPlayer>(CorPlayersToSave);

				this.saveCondition.await();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				this.saveLock.unlock();
			}
		}

		public short initiatePlayerDataLoading(List<CorPlayer> CorPlayersToLoad) {
			List<CorPlayer> toLoad = null;

			if (CorPlayersToLoad != null) {
				toLoad = new ArrayList<CorPlayer>(CorPlayersToLoad);
			}

			if (this.loadLock.tryLock()) {
				try {
					for (short id = 1; id < 32767; id = (short)(id + 1))
						if (!this.loadedPlayers.containsKey(id))
						{
							this.playersToLoad.put(id, toLoad);
							return id;
						}
				}
				finally {
					this.loadLock.unlock();
				}
			}

			return 0;
		}

		public void start()	{
			synchronized (this) {
				if (this.runner != null) {
					return;
				}

				this.running = true;
				this.runner = new Thread(this);

				this.runner.setName("Corruption File Accessor");
				this.runner.start();
			}
		}

		public void stop() {
			this.running = false;
		}

		public void join() {
			try {
				this.runner.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public List<CorPlayer> getResult(short id) {
			if (this.loadLock.tryLock()) {
				try {
					return this.loadedPlayers.get(id);
				}
				finally {
					this.loadLock.unlock();
				}
			}

			return null;
		}
}
