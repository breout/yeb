package com.jason.server.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.time.temporal.TemporalAdjusters.*;
import static java.util.stream.Collectors.toList;

public class DateUtils {
//	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
//	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
//	public static final String DATE_FORMAT_YYYYMMDDHHMMSS_SHORT = "yyyyMMddHHmmss";
//	public static final String YYYY_MM_DD = "yyyy-MM-dd";
//	public static final String HH_MM_SS = "HH:mm:ss";

    public static final int defaultTimezoneOffset = TimeZone.getDefault().getRawOffset() / (1000 * 60 * 60);
//	public static final DateTimeFormatter DFY_MD_HMS = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    public static final DateTimeFormatter LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    public static String format(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime localDateTime) {
        return format(localDateTime, true);
    }

    public static String format(LocalDateTime localDateTime, boolean truncateToSeconds) {
        TemporalUnit truncateUnit = truncateToSeconds ? ChronoUnit.SECONDS : ChronoUnit.NANOS;
        return localDateTime.truncatedTo(truncateUnit).format(LOCAL_DATE_TIME);
    }

    public static String format(Date date) {
        return format(toLocalDateTime(date));
    }

    public static String format(Date date, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return df.format(toLocalDateTime(date));
    }

	public static String formatDate(Date date) {
		return DateTimeFormatter.ISO_LOCAL_DATE.format(toLocalDateTime(date));
	}


    public static String format(LocalDate localDate) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(localDate);
    }

	public static String format(LocalTime localTime) {
		return ISO_LOCAL_TIME.format(localTime);
	}

	public static String format(String specifiedTime) {
		return format(LocalTime.parse(specifiedTime));
	}

	public static Date parse(String dateTime) {
		LocalDateTime localDateTime = LocalDateTime.from(LOCAL_DATE_TIME.parse(dateTime));
		return toDate(localDateTime);
	}

    public static Date parse(String date, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        return toDate(localDateTime);
    }

    public static Date parseDate(String date) {
        LocalDate localDate = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(date));
        return toDate(localDate);
    }

    public static Integer toMinutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    public static Integer toMinutes(String time) {
        return toMinutes(LocalTime.parse(time));
    }

    public static Date plus(Date date, long amountToAdd, TemporalUnit unit) {
        LocalDateTime dateTime = toLocalDateTime(date);
        return toDate(dateTime.plus(amountToAdd, unit));
    }

    public static Date plusMonths(Date date, int months) {
        LocalDateTime dateTime = toLocalDateTime(date);
        return toDate(dateTime.plusMonths(months));
    }

    public static Date plusDays(Date date, int days) {
        LocalDateTime dateTime = toLocalDateTime(date);
        return toDate(dateTime.plusDays(days));
    }

    /**
     * 得到服务器的时区偏移小时
     */
    public static int getDefaultTimeZoneOffset() {
        return defaultTimezoneOffset;
    }

    /**
     * 获得某天最大时间 例如 2020-01-01 23:59:59
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime endOfDay = toLocalDateTime(date).with(LocalTime.MAX);
        return toDate(endOfDay);
    }

    /**
     * 获得某天最小时间 例如 2020-01-01 00:00:00
     */
    public static Date getStartOfDay(Date date) {
        return toDate(toLocalDate(date).atStartOfDay());
    }

    public static List<Integer> getWorkDaysByWeekDay(String weekDay, Date start, Date end) {
        List<Integer> result = new ArrayList<>();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(start);
        endCal.setTime(end);
        while (startCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
            if (startCal.get(Calendar.DAY_OF_WEEK) - 1 == Integer.valueOf(weekDay)) {
                result.add(startCal.get(Calendar.DAY_OF_MONTH));
            }
            startCal.add(Calendar.DATE, 1);
        }
        return result;
    }

    public static List<LocalDate> getDatesBetween(Date start, Date end) {
        LocalDate startDate = toLocalDate(start);
        LocalDate endDate = toLocalDate(end);
        Stream.Builder<LocalDate> result = Stream.builder();
        while (!startDate.isAfter(endDate)) {
            result.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return result.build().collect(toList());
    }

    public static long getIntervalDays(Date start, Date end) {
        return ChronoUnit.DAYS.between(toLocalDate(start), toLocalDate(end));
    }

    public static boolean isBetween(Date date, Date start, Date end) {
        return !date.before(start) && !date.after(end);
    }

    public static List<Integer> getDayOfMonth(int weekday, Date start, Date end) {
        Stream.Builder<Integer> result = Stream.builder();
        DayOfWeek dayOfWeek = DayOfWeek.of(weekday);
        LocalDate startDate = toLocalDate(start).with(nextOrSame(dayOfWeek));
        LocalDate endDate = toLocalDate(end);
        while (!startDate.isAfter(endDate)) {
            result.add(startDate.getDayOfMonth());
            startDate = startDate.with(next(dayOfWeek));
        }
        return result.build().collect(toList());
    }

    public static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

	public static LocalDate toLocalDate(String dateTime) {
		return Instant.ofEpochMilli(parseDate(dateTime).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime toLocalDateTime(Date date, String time) {
		return LocalDateTime.of(toLocalDate(date), LocalTime.parse(time));
	}

    public static Date toDate(Date date, LocalTime localTime) {
        LocalDate localDate = DateUtils.toLocalDate(date);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return toDate(localDateTime);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDate localDate) {
        return toDate(localDate.atTime(LocalTime.MIN));
    }

    public static int getDayOfMonth(Date date) {
        return toLocalDateTime(date).getDayOfMonth();
    }

	public static Integer getWorkDayOfWeek(Date date) {
		return toLocalDate(date).getDayOfWeek().getValue();
	}

	public static Integer getWorkDayOfWeek(String dateTime) {
		return toLocalDate(parseDate(dateTime)).getDayOfWeek().getValue();
	}

	public static Date getFirstDayOfMonth(Date date) {
		LocalDate localDate = DateUtils.toLocalDate(date).with(firstDayOfMonth());
		return toDate(localDate);
	}

	public static Date getEndDayOfMonth(Date date) {
		LocalDate localDate = DateUtils.toLocalDate(date).with(lastDayOfMonth());
		return toDate(localDate);
	}

	public static List<Date> getDates(String startDate, String endDate) {
		LocalDate startLocalDate = toLocalDate(startDate);
		LocalDate endLocalDate = toLocalDate(endDate);
		List<Date> dates = Collections.emptyList();
		while (startLocalDate.compareTo(endLocalDate) <= 0) {
			dates.add(toDate(startLocalDate));
			startLocalDate = startLocalDate.plusDays(1);
		}
		return dates;
	}
}
