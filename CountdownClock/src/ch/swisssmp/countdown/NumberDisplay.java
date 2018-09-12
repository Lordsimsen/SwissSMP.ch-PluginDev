package ch.swisssmp.countdown;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import ch.swisssmp.utils.Mathf;

public class NumberDisplay {
	protected static void buildTime(Block position, BlockFace direction, Material numberMaterial, Material gapMaterial, long time){
		Duration duration = Duration.of(time, ChronoUnit.MILLIS);
		long days = duration.toDays();
		long hours = duration.toHours() % 24;
		long minutes = duration.toMinutes() % 60;
		long seconds = duration.getSeconds() % 60;
		Block firstDigitDay = position;
		Block secondDigitDay = position.getRelative(direction, 4);
		Block firstDigitHour = position.getRelative(direction, 10);
		Block secondDigitHour = position.getRelative(direction, 14);
		Block firstDigitMinutes = position.getRelative(direction, 20);
		Block secondDigitMinutes = position.getRelative(direction, 24);
		Block firstDigitSeconds = position.getRelative(direction, 30);
		Block secondDigitSeconds = position.getRelative(direction, 34);
		NumberDisplay.buildNumber(firstDigitDay, direction, numberMaterial, gapMaterial, Mathf.floorToInt(days/10));
		NumberDisplay.buildNumber(secondDigitDay, direction, numberMaterial, gapMaterial, days);
		NumberDisplay.buildNumber(firstDigitHour, direction, numberMaterial, gapMaterial, Mathf.floorToInt(hours/10));
		NumberDisplay.buildNumber(secondDigitHour, direction, numberMaterial, gapMaterial, hours);
		NumberDisplay.buildNumber(firstDigitMinutes, direction, numberMaterial, gapMaterial, Mathf.floorToInt(minutes/10));
		NumberDisplay.buildNumber(secondDigitMinutes, direction, numberMaterial, gapMaterial, minutes);
		NumberDisplay.buildNumber(firstDigitSeconds, direction, numberMaterial, gapMaterial, Mathf.floorToInt(seconds/10));
		NumberDisplay.buildNumber(secondDigitSeconds, direction, numberMaterial, gapMaterial, seconds);
	}
	private static void buildNumber(Block position, BlockFace direction, Material numberMaterial, Material gapMaterial, long number){
		boolean[] numberTemplate = CountdownClockPlugin.getNumberTemplate(number % 10);
		Block current = position;
		for(int y = 6; y >=0; y--){
			current = position.getRelative(BlockFace.UP, y);
			for(int x = 0; x < 3; x++){
				current.setType(numberTemplate[(6-y)*3+x] ? numberMaterial : gapMaterial);
				current = current.getRelative(direction);
			}
		}
	}
}
