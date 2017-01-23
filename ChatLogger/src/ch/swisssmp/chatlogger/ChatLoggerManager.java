package ch.swisssmp.chatlogger;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Random;



public class ChatLoggerManager {

	private int nLines=0;
	private LineNumberReader lnr;
	private ChatLogger plugin;
	private Random rng = new Random();


	public ChatLoggerManager(ChatLogger chatlogger){

		
		setPlugin(chatlogger);
		try (FileReader fr = new FileReader(plugin.getDataFolder()+ File.separator + "ChatLog.txt")){	

			lnr = new LineNumberReader(fr);
			String line = lnr.readLine();

			while (line != null){

				nLines++;
				line = lnr.readLine();

			}

		} catch (IOException e) {
			System.out.println("No Chatlog File found.\n Make sure your file is named \"ChatLog.txt\"");
		}



	}

	public int getNumberOfLines(){		
		return nLines;		
	}



	public String getLineNumberN(int n) throws IOException{

		FileReader fr = new FileReader(plugin.getDataFolder()+ File.separator + "ChatLog.txt");
		lnr = new LineNumberReader(fr);

		for (int i = 0;i<n;i++){
			lnr.readLine();
		}

		return lnr.readLine();
	}

	public void increaseNumberOfLinesByOne(){
		nLines++;
	}

	public ChatLogger getPlugin() {
		return plugin;
	}

	private void setPlugin(ChatLogger plugin) {
		this.plugin = plugin;
	}

	public int getRandomNumber(int nLines){		
		return rng.nextInt(nLines);		
	}

}