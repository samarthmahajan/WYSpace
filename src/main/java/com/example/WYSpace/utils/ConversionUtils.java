package com.example.WYSpace.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.example.WYSpace.dto.PassSchedule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConversionUtils {

	private static final String DATE_FORMAT = "00";

	public List<PassSchedule> getAllPassesforFile(File file) {

		List<PassSchedule> passSchedules = new ArrayList<>();
		if (file.exists()) {
			Path passScheduleFilePath = Paths.get(file.getPath());
			try (Stream linesStream = Files.lines(passScheduleFilePath)) {

				linesStream.forEach(line -> {
					String[] strValues = line.toString().split(",");
					PassSchedule ps = PassSchedule.builder()
							.satelliteName(strValues[0])
							.bandwidth(Integer.parseInt(strValues[1]))
							.passStartTime(convertTimetoMin(strValues[2]))
							.passEndTime(convertTimetoMin(strValues[3])).build();
					passSchedules.add(ps);

				});

			} catch (IOException e) {
				log.error("Internal error occurred during file read {} " + e.getLocalizedMessage());
			}
		}

		return passSchedules;
	}

	private int convertTimetoMin(String time) {

		if (StringUtils.isNotBlank(time)) {
			String[] hourMinArray = time.split(":");
			int hour = Integer.parseInt(hourMinArray[0]);
			int mins =  Integer.parseInt(hourMinArray[1]);
			int hoursInMins = hour * 60;

			return hoursInMins + mins;
		}
		return 0;
	}
	
	public String covertMinToHHmm(int num) {
		DecimalFormat decimalFormat = new DecimalFormat(DATE_FORMAT);
		int hours = num / 60;
		int minutes = num % 60;
		return decimalFormat.format(hours) + ":" + decimalFormat.format(minutes);
	}
}
