/*
 * $Id$ [18-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Defines a recurrence.
 *
 * @author Ben Fortuna
 */
public class Recur implements Serializable {

    private static final long serialVersionUID = -7333226591784095142L;

    private static final String FREQ = "FREQ";

    private static final String UNTIL = "UNTIL";

    private static final String COUNT = "COUNT";

    private static final String INTERVAL = "INTERVAL";

    private static final String BYSECOND = "BYSECOND";

    private static final String BYMINUTE = "BYMINUTE";

    private static final String BYHOUR = "BYHOUR";

    private static final String BYDAY = "BYDAY";

    private static final String BYMONTHDAY = "BYMONTHDAY";

    private static final String BYYEARDAY = "BYYEARDAY";

    private static final String BYWEEKNO = "BYWEEKNO";

    private static final String BYMONTH = "BYMONTH";

    private static final String BYSETPOS = "BYSETPOS";

    private static final String WKST = "WKST";

    // frequencies..
    public static final String SECONDLY = "SECONDLY";

    public static final String MINUTELY = "MINUTELY";

    public static final String HOURLY = "HOURLY";

    public static final String DAILY = "DAILY";

    public static final String WEEKLY = "WEEKLY";

    public static final String MONTHLY = "MONTHLY";

    public static final String YEARLY = "YEARLY";

    private static Log log = LogFactory.getLog(Recur.class);

    private String frequency;

    private Date until;

    private int count = -1;

    private int interval = -1;

    private NumberList secondList;

    private NumberList minuteList;

    private NumberList hourList;

    private WeekDayList dayList;

    private NumberList monthDayList;

    private NumberList yearDayList;

    private NumberList weekNoList;

    private NumberList monthList;

    private NumberList setPosList;

    private String weekStartDay;

    private Map experimentalValues = new HashMap();

    // The order, or layout, of the date as it appears in a string
    // e.g. 20050415T093000 (April 15th, 9:30:00am)
    private static final int[] DATE_ORDER =
            {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH,
             Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};

    /**
     * Constructor.
     *
     * @param aValue
     *            a string representation of a recurrence.
     * @throws ParseException
     *             thrown when the specified string contains an invalid
     *             representation of an UNTIL date value
     */
    public Recur(final String aValue) throws ParseException {
        for (StringTokenizer t = new StringTokenizer(aValue, ";="); t
                .hasMoreTokens();) {
            String token = t.nextToken();
            if (FREQ.equals(token)) {
                frequency = t.nextToken();
            }
            else if (UNTIL.equals(token)) {
                String untilString = t.nextToken();
                try {
                    until = new Date(untilString);
                }
                catch (ParseException pe) {
                    until = new DateTime(untilString);
                }
            }
            else if (COUNT.equals(token)) {
                count = Integer.parseInt(t.nextToken());
            }
            else if (INTERVAL.equals(token)) {
                interval = Integer.parseInt(t.nextToken());
            }
            else if (BYSECOND.equals(token)) {
                secondList = new NumberList(t.nextToken());
            }
            else if (BYMINUTE.equals(token)) {
                minuteList = new NumberList(t.nextToken());
            }
            else if (BYHOUR.equals(token)) {
                hourList = new NumberList(t.nextToken());
            }
            else if (BYDAY.equals(token)) {
                dayList = new WeekDayList(t.nextToken());
            }
            else if (BYMONTHDAY.equals(token)) {
                monthDayList = new NumberList(t.nextToken());
            }
            else if (BYYEARDAY.equals(token)) {
                yearDayList = new NumberList(t.nextToken());
            }
            else if (BYWEEKNO.equals(token)) {
                weekNoList = new NumberList(t.nextToken());
            }
            else if (BYMONTH.equals(token)) {
                monthList = new NumberList(t.nextToken());
            }
            else if (BYSETPOS.equals(token)) {
                setPosList = new NumberList(t.nextToken());
            }
            else if (WKST.equals(token)) {
                weekStartDay = t.nextToken();
            }
            // assume experimental value..
            else {
                experimentalValues.put(token, t.nextToken());
            }
        }
    }

    /**
     * @param frequency
     * @param until
     */
    public Recur(final String frequency, final Date until) {
        this.frequency = frequency;
        this.until = until;
    }

    /**
     * @param frequency
     * @param count
     */
    public Recur(final String frequency, final int count) {
        this.frequency = frequency;
        this.count = count;
    }

    /**
     * @return Returns the dayList.
     */
    public final WeekDayList getDayList() {
        if (dayList == null) {
            dayList = new WeekDayList();
        }
        return dayList;
    }

    /**
     * @return Returns the hourList.
     */
    public final NumberList getHourList() {
        if (hourList == null) {
            hourList = new NumberList();
        }
        return hourList;
    }

    /**
     * @return Returns the minuteList.
     */
    public final NumberList getMinuteList() {
        if (minuteList == null) {
            minuteList = new NumberList();
        }
        return minuteList;
    }

    /**
     * @return Returns the monthDayList.
     */
    public final NumberList getMonthDayList() {
        if (monthDayList == null) {
            monthDayList = new NumberList();
        }
        return monthDayList;
    }

    /**
     * @return Returns the monthList.
     */
    public final NumberList getMonthList() {
        if (monthList == null) {
            monthList = new NumberList();
        }
        return monthList;
    }

    /**
     * @return Returns the secondList.
     */
    public final NumberList getSecondList() {
        if (secondList == null) {
            secondList = new NumberList();
        }
        return secondList;
    }

    /**
     * @return Returns the setPosList.
     */
    public final NumberList getSetPosList() {
        if (setPosList == null) {
            setPosList = new NumberList();
        }
        return setPosList;
    }

    /**
     * @return Returns the weekNoList.
     */
    public final NumberList getWeekNoList() {
        if (weekNoList == null) {
            weekNoList = new NumberList();
        }
        return weekNoList;
    }

    /**
     * @return Returns the yearDayList.
     */
    public final NumberList getYearDayList() {
        if (yearDayList == null) {
            yearDayList = new NumberList();
        }
        return yearDayList;
    }

    /**
     * @return Returns the count.
     */
    public final int getCount() {
        return count;
    }

    /**
     * @return Returns the experimentalValues.
     */
    public final Map getExperimentalValues() {
        return experimentalValues;
    }

    /**
     * @return Returns the frequency.
     */
    public final String getFrequency() {
        return frequency;
    }

    /**
     * @return Returns the interval.
     */
    public final int getInterval() {
        return interval;
    }

    /**
     * @return Returns the until.
     */
    public final Date getUntil() {
        return until;
    }

    /**
     * @return Returns the weekStartDay.
     */
    public final String getWeekStartDay() {
        return weekStartDay;
    }

    /**
     * @param weekStartDay The weekStartDay to set.
     */
    public final void setWeekStartDay(final String weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
        b.append(FREQ);
        b.append('=');
        b.append(frequency);
        if (weekStartDay != null) {
            b.append(';');
            b.append(WKST);
            b.append('=');
            b.append(weekStartDay);
        }
        if (interval >= 1) {
            b.append(';');
            b.append(INTERVAL);
            b.append('=');
            b.append(interval);
        }
        if (until != null) {
            b.append(';');
            b.append(UNTIL);
            b.append('=');
            // Note: date-time representations should always be in UTC time.
            b.append(until);
        }
        if (count >= 1) {
            b.append(';');
            b.append(COUNT);
            b.append('=');
            b.append(count);
        }
        if (!getMonthList().isEmpty()) {
            b.append(';');
            b.append(BYMONTH);
            b.append('=');
            b.append(monthList);
        }
        if (!getWeekNoList().isEmpty()) {
            b.append(';');
            b.append(BYWEEKNO);
            b.append('=');
            b.append(weekNoList);
        }
        if (!getYearDayList().isEmpty()) {
            b.append(';');
            b.append(BYYEARDAY);
            b.append('=');
            b.append(yearDayList);
        }
        if (!getMonthDayList().isEmpty()) {
            b.append(';');
            b.append(BYMONTHDAY);
            b.append('=');
            b.append(monthDayList);
        }
        if (!getDayList().isEmpty()) {
            b.append(';');
            b.append(BYDAY);
            b.append('=');
            b.append(dayList);
        }
        if (!getHourList().isEmpty()) {
            b.append(';');
            b.append(BYHOUR);
            b.append('=');
            b.append(hourList);
        }
        if (!getMinuteList().isEmpty()) {
            b.append(';');
            b.append(BYMINUTE);
            b.append('=');
            b.append(minuteList);
        }
        if (!getSecondList().isEmpty()) {
            b.append(';');
            b.append(BYSECOND);
            b.append('=');
            b.append(secondList);
        }
        if (!getSetPosList().isEmpty()) {
            b.append(';');
            b.append(BYSETPOS);
            b.append('=');
            b.append(setPosList);
        }
        return b.toString();
    }
    
    /**
     * Returns a list of start dates in the specified period represented by this recur.
     * Any date fields not specified by this recur are retained from the period start,
     * and as such you should ensure the period start is initialised correctly.
     * @param periodStart the start of the period
     * @param periodEnd the end of the period
     * @param value the type of dates to generate (i.e. date/date-time)
     * @return a list of dates
     */
    public final DateList getDates(final Date periodStart, final Date periodEnd, final Value value) {
        return getDates(periodStart, periodStart, periodEnd, value);
    }

    /**
     * Returns a list of start dates in the specified period represented
     * by this recur.  This method includes a base date argument, which
     * indicates the start of the fist occurrence of this recurrence.
     *
     * The base date is used to inject default values to return a set of dates in
     * the correct format.  For example, if the search start date (start) is
     * Wed, Mar 23, 12:19PM, but the recurrence is Mon - Fri, 9:00AM - 5:00PM,
     * the start dates returned should all be at 9:00AM, and not 12:19PM.
     *
     * @return a list of dates represented by this recur instance
     * @param base the start date of this Recurrence's first instance
     * @param periodStart the start of the period
     * @param periodEnd the end of the period
     * @param value the type of dates to generate (i.e. date/date-time)
     */
    public final DateList getDates(final Date base, final Date periodStart,
                                   final Date periodEnd, final Value value) {

        DateList dates = new DateList(value);

        // Should never happen!  base is always required!
        if (base == null) {
            return dates;
        }
        
        Date start = (Date) periodStart.clone();

        // We don't want to see or search any dates that occur before the
        // first instance of this recurrence.
        if (start.before(base)) {
            start = base;
        }

        Calendar baseCalendar = Calendar.getInstance();
        baseCalendar.setTime(base);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.MILLISECOND, 0);

        int lvf = getLowestVariableField();
        
        // Set everything to the right of the LVF to match the DTSTART.
        // Everything else comes from the query start date.
        int[] matchFields = getMatchFields(lvf);
        for (int i = 0; i < matchFields.length; i++) {
            cal.set(matchFields[i], baseCalendar.get(matchFields[i]));
        }

        // Deal with rolling over to the next time period if necessary.
        if (cal.getTime().before(periodStart)) {
            cal.add(lvf, 1);
        }
        
        // Weekly frequencies need to match up the week day. (i.e. if it's
        // Friday, and that's not part of our criteria, keep adding a day
        // until they match).
        /*if (getFrequency().equals(WEEKLY)) {

            while (cal.get(Calendar.DAY_OF_WEEK) !=

                   dtStartCalendar.get(Calendar.DAY_OF_WEEK)) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        }*/

        cal.setTime(base);
        
        // apply frequency/interval rules..
        if (getUntil() != null) {
            while (!cal.getTime().after(getUntil())
                    && !(periodEnd != null && cal.getTime().after(periodEnd))) {
                dates.add(new Date(cal.getTime().getTime()));
                increment(cal);
            }
        }
        /*
        else if (getCount() >= 1) {
            for (int i = 0;
                i < getCount() && !(periodEnd != null && cal.getTime().after(periodEnd));
                i++) {
                dates.add(cal.getTime());
                increment(cal);
            }
        }
        */
        else if (periodEnd != null) {
            while (!cal.getTime().after(periodEnd)) {
                dates.add(new Date(cal.getTime().getTime()));
                increment(cal);
            }
        }
        else {
            // if no end-point specified we can't calculate a finite
            // set of dates..
            return dates;
        }

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after FREQUENCY/INTERVAL processing: " + dates);
        }

        dates = getMonthVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYMONTH processing: " + dates);
        }

        dates = getWeekNoVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYWEEKNO processing: " + dates);
        }

        dates = getYearDayVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYYEARDAY processing: " + dates);
        }

        dates = getMonthDayVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYMONTHDAY processing: " + dates);
        }

        dates = getDayVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYDAY processing: " + dates);
        }

        dates = getHourVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYHOUR processing: " + dates);
        }

        dates = getMinuteVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYMINUTE processing: " + dates);
        }

        dates = getSecondVariants(dates);

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after BYSECOND processing: " + dates);
        }
        
        // check count..
        if (getCount() >= 1 && dates.size() >= getCount()) {
            for (int i = getCount(); i < dates.size();) {
                dates.remove(i);
            }
        }

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Dates after COUNT processing: " + dates);
        }

        // final post-processing..
        for (int i = 0; i < dates.size(); i++) {
            Date date = (Date) dates.get(i);
            if (periodStart != null && date.before(periodStart)) {
                dates.remove(date);
                i--;
            }
            else if (periodEnd != null && date.after(periodEnd)) {
                dates.remove(date);
                i--;
            }
            else if (getUntil() != null && date.after(getUntil())) {
                dates.remove(date);
                i--;
            }
            /*
            else if (getCount() >= 1 && i >= getCount()) {
                dates.remove(date);
                i--;
            }
            */
        }

        return dates;
    }
    
    /**
     * Returns the finest precision at which this recurrence will modify
     * a date value.
     * @return an int representing a <code>java.util.Calendar</code> field.
     */
    private int getLowestVariableField() {
        int lowestVariableField = 9999;

        // check frequency precision..
        if (YEARLY.equals(frequency)) {
            lowestVariableField = Calendar.YEAR;
        }
        else if (MONTHLY.equals(frequency)) {
            lowestVariableField = Calendar.MONTH;
        }
        else if (WEEKLY.equals(frequency)) {
            lowestVariableField = Calendar.DAY_OF_MONTH;
        }
        else if (DAILY.equals(frequency)) {
            lowestVariableField = Calendar.DAY_OF_MONTH;
        }
        else if (HOURLY.equals(frequency)) {
            lowestVariableField = Calendar.HOUR_OF_DAY;
        }
        else if (MINUTELY.equals(frequency)) {
            lowestVariableField = Calendar.MINUTE;
        }
        else if (SECONDLY.equals(frequency)) {
            lowestVariableField = Calendar.SECOND;
        }

        // check BY* rules precision..
        if (!getMonthList().isEmpty() && Calendar.MONTH < lowestVariableField) {
            lowestVariableField = Calendar.MONTH;
        }
        if (!getWeekNoList().isEmpty() && Calendar.DAY_OF_MONTH < lowestVariableField) {
            lowestVariableField = Calendar.DAY_OF_MONTH;
        }
        if (!getYearDayList().isEmpty() && Calendar.DAY_OF_MONTH < lowestVariableField) {
            lowestVariableField = Calendar.DAY_OF_MONTH;
        }
        if (!getMonthDayList().isEmpty() && Calendar.DAY_OF_MONTH < lowestVariableField) {
            lowestVariableField = Calendar.DAY_OF_MONTH;
        }
        if (!getDayList().isEmpty() && Calendar.DAY_OF_MONTH < lowestVariableField) {
            lowestVariableField = Calendar.DAY_OF_MONTH;
        }
        if (!getHourList().isEmpty() && Calendar.HOUR_OF_DAY < lowestVariableField) {
            lowestVariableField = Calendar.HOUR_OF_DAY;
        }
        if (!getMinuteList().isEmpty() && Calendar.MINUTE < lowestVariableField) {
            lowestVariableField = Calendar.MINUTE;
        }
        if (!getSecondList().isEmpty() && Calendar.SECOND < lowestVariableField) {
            lowestVariableField = Calendar.SECOND;
        }
        return lowestVariableField;
    }

    /**
     * Return all the items to the right of LVF in a date string.
     *
     * @param lowestVariableField
     * @return
     */
    private int[] getMatchFields(final int lowestVariableField) {

        int[] matchFields = new int[0];
        int lvfIndex = getDateOrderIndex(lowestVariableField);

        for (int i = 0; i < DATE_ORDER.length; i++) {

            int nextItem = DATE_ORDER[i];

            if (i > lvfIndex) {

                int[] tmpArray = matchFields;
                matchFields = new int[tmpArray.length + 1];

                System.arraycopy(tmpArray, 0, matchFields, 0, tmpArray.length);

                matchFields[matchFields.length - 1] = nextItem;
            }
        }

        return matchFields;
    }

    /**
     * Return the index of this item in the DATE_ORDER array.
     *
     * @param val
     * @return
     */
    private int getDateOrderIndex(final int val) {

        for (int i = 0; i < DATE_ORDER.length; i++) {

            int nextVal = DATE_ORDER[i];
            if (nextVal == val) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Increments the specified calendar according to the
     * frequency and interval specified in this recurrence
     * rule.
     * @param cal a java.util.Calendar to increment
     */
    private void increment(final Calendar cal) {
        // initialise interval..
        int calInterval = (getInterval() >= 1) ? getInterval() : 1;
        if (SECONDLY.equals(getFrequency())) {
            cal.add(Calendar.SECOND, calInterval);
        }
        else if (MINUTELY.equals(getFrequency())) {
            cal.add(Calendar.MINUTE, calInterval);
        }
        else if (HOURLY.equals(getFrequency())) {
            cal.add(Calendar.HOUR_OF_DAY, calInterval);
        }
        else if (DAILY.equals(getFrequency())) {
            cal.add(Calendar.DAY_OF_YEAR, calInterval);
        }
        else if (WEEKLY.equals(getFrequency())) {
            cal.add(Calendar.WEEK_OF_YEAR, calInterval);
        }
        else if (MONTHLY.equals(getFrequency())) {
            cal.add(Calendar.MONTH, calInterval);
        }
        else if (YEARLY.equals(getFrequency())) {
            cal.add(Calendar.YEAR, calInterval);
        }
    }

    /**
     * Applies BYSETPOS rules to <code>dates</code>. Valid positions are from
     * 1 to the size of the date list. Invalid positions are ignored.
     * 
     * @param dates
     */
    protected final DateList applySetPosRules(final DateList dates) {
        DateList setPosDates = new DateList(dates.getType());
        int size = dates.size();
        for (Iterator i = getSetPosList().iterator(); i.hasNext();) {
            Integer setPos = (Integer) i.next();
            int pos = setPos.intValue();
            if (pos > 0 && pos <= size) {
                setPosDates.add(dates.get(pos - 1));
            }
            else if (pos < 0 && pos >= -size) {
                setPosDates.add(dates.get(size + pos));
            }
        }
        return setPosDates;
    }
    
    /**
     * Applies BYMONTH rules specified in this Recur instance to the
     * specified date list. If no BYMONTH rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMonthVariants(final DateList dates) {
        if (getMonthList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList monthlyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getMonthList().iterator(); j.hasNext();) {
                Integer month = (Integer) j.next();
                // Java months are zero-based..
                cal.set(Calendar.MONTH, month.intValue() - 1);
                monthlyDates.add(new Date(cal.getTime().getTime()));
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty()
                && getWeekNoList().isEmpty()
                && getYearDayList().isEmpty()
                && getMonthDayList().isEmpty()
                && getDayList().isEmpty()
                && getHourList().isEmpty()
                && getMinuteList().isEmpty()
                && getSecondList().isEmpty()) {
            return applySetPosRules(monthlyDates);
        }
        return monthlyDates;
    }

    /**
     * Applies BYWEEKNO rules specified in this Recur instance to the
     * specified date list. If no BYWEEKNO rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getWeekNoVariants(final DateList dates) {
        if (getWeekNoList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList weekNoDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getWeekNoList().iterator(); j.hasNext();) {
                Integer weekNo = (Integer) j.next();
                cal.set(Calendar.WEEK_OF_YEAR, Dates.getAbsWeekNo(cal.getTime(), weekNo.intValue()));
                weekNoDates.add(cal.getTime());
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty()
                && getYearDayList().isEmpty()
                && getMonthDayList().isEmpty()
                && getDayList().isEmpty()
                && getHourList().isEmpty()
                && getMinuteList().isEmpty()
                && getSecondList().isEmpty()) {
            return applySetPosRules(weekNoDates);
        }
        return weekNoDates;
    }

    /**
     * Applies BYYEARDAY rules specified in this Recur instance to the
     * specified date list. If no BYYEARDAY rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getYearDayVariants(final DateList dates) {
        if (getYearDayList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList yearDayDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getYearDayList().iterator(); j.hasNext();) {
                Integer yearDay = (Integer) j.next();
                cal.set(Calendar.DAY_OF_YEAR, Dates.getAbsYearDay(cal.getTime(), yearDay.intValue()));
                yearDayDates.add(cal.getTime());
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty()
                && getMonthDayList().isEmpty()
                && getDayList().isEmpty()
                && getHourList().isEmpty()
                && getMinuteList().isEmpty()
                && getSecondList().isEmpty()) {
            return applySetPosRules(yearDayDates);
        }
        return yearDayDates;
    }

    /**
     * Applies BYMONTHDAY rules specified in this Recur instance to the
     * specified date list. If no BYMONTHDAY rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMonthDayVariants(final DateList dates) {
        if (getMonthDayList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList monthDayDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getMonthDayList().iterator(); j.hasNext();) {
                Integer monthDay = (Integer) j.next();
                cal.set(Calendar.DAY_OF_YEAR, Dates.getAbsMonthDay(cal.getTime(), monthDay.intValue()));
                monthDayDates.add(new Date(cal.getTime().getTime()));
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty()
                && getDayList().isEmpty()
                && getHourList().isEmpty()
                && getMinuteList().isEmpty()
                && getSecondList().isEmpty()) {
            return applySetPosRules(monthDayDates);
        }
        return monthDayDates;
    }

    /**
     * Applies BYDAY rules specified in this Recur instance to the
     * specified date list. If no BYDAY rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getDayVariants(final DateList dates) {
        if (getDayList().isEmpty()) {
            return dates;
        }
        DateList weekDayDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            for (Iterator j = getDayList().iterator(); j.hasNext();) {
                WeekDay weekDay = (WeekDay) j.next();
                weekDayDates.addAll(getAbsWeekDays(date, weekDay));
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty()
                && getHourList().isEmpty()
                && getMinuteList().isEmpty()
                && getSecondList().isEmpty()) {
            return applySetPosRules(weekDayDates);
        }
        return weekDayDates;
    }

    /**
     * Returns a list of applicable dates corresponding to the specified
     * week day in accordance with the frequency specified by this recurrence
     * rule.
     * @param date
     * @param weekDay
     * @return
     */
    private List getAbsWeekDays(final Date date, final WeekDay weekDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int calDay = -1;
        DateList days = new DateList(Value.DATE);
        if (WeekDay.SU.getDay().equals(weekDay.getDay())) {
            calDay = Calendar.SUNDAY;
        }
        else if (WeekDay.MO.getDay().equals(weekDay.getDay())) {
            calDay = Calendar.MONDAY;
        }
        else if (WeekDay.TU.getDay().equals(weekDay.getDay())) {
            calDay = Calendar.TUESDAY;
        }
        else if (WeekDay.WE.getDay().equals(weekDay.getDay())) {
            calDay = Calendar.WEDNESDAY;
        }
        else if (WeekDay.TH.getDay().equals(weekDay.getDay())) {
            calDay = Calendar.THURSDAY;
        }
        else if (WeekDay.FR.getDay().equals(weekDay.getDay())) {
            calDay = Calendar.FRIDAY;
        }
        else if (WeekDay.SA.getDay().equals(weekDay.getDay())) {
            calDay = Calendar.SATURDAY;
        }
        else {
            // a matching weekday cannot be identified..
            return days;
        }
        if (DAILY.equals(getFrequency())) {
            if (cal.get(Calendar.DAY_OF_WEEK) == calDay) {
                // only one date is applicable..
                days.add(new Date(cal.getTime().getTime()));
            }
        }
        else if (WEEKLY.equals(getFrequency())  || !getWeekNoList().isEmpty()) {
            //int weekNo = cal.get(Calendar.WEEK_OF_YEAR);
            // construct a list of possible week days..
//            cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }

            int weekNo = cal.get(Calendar.WEEK_OF_YEAR);

            while (cal.get(Calendar.WEEK_OF_YEAR) == weekNo) {
                days.add(new Date(cal.getTime().getTime()));
                cal.add(Calendar.DAY_OF_WEEK, 7);
            }
        }
        else if (MONTHLY.equals(getFrequency())  || !getMonthList().isEmpty()) {
            int month = cal.get(Calendar.MONTH);
            // construct a list of possible month days..
            cal.set(Calendar.DAY_OF_MONTH, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            while (cal.get(Calendar.MONTH) == month) {
                days.add(new Date(cal.getTime().getTime()));
                cal.add(Calendar.DAY_OF_MONTH, 7);
            }
        }
        else if (YEARLY.equals(getFrequency())) {
            int year = cal.get(Calendar.YEAR);
            // construct a list of possible year days..
            cal.set(Calendar.DAY_OF_YEAR, 1);
            while (cal.get(Calendar.DAY_OF_WEEK) != calDay) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            while (cal.get(Calendar.YEAR) == year) {
                days.add(new Date(cal.getTime().getTime()));
                cal.add(Calendar.DAY_OF_YEAR, 7);
            }
        }
        DateList weekDays = new DateList(days.getType());
        sublist(days, weekDay.getOffset(), weekDays);
        return weekDays;
    }

    /**
     * Returns a single-element sublist containing the element of
     * <code>list</code> at <code>offset</code>. Valid offsets are from 1
     * to the size of the list. If an invalid offset is supplied, all elements
     * from <code>list</code> are added to <code>sublist</code>.
     * 
     * @param list
     * @param offset
     * @param sublist
     */
    protected static void sublist(final List list, final int offset, final List sublist) {
        int size = list.size();
        if (offset < 0 && offset >= -size) {
            sublist.add(list.get(size + offset));
        } else if (offset > 0 && offset <= size) {
            sublist.add(list.get(offset - 1));
        } else {
            sublist.addAll(list);
        }
    }
    
    /**
     * Applies BYHOUR rules specified in this Recur instance to the specified
     * date list. If no BYHOUR rules are specified the date list is returned
     * unmodified.
     * 
     * @param dates
     * @return
     */
    private DateList getHourVariants(final DateList dates) {
        if (getHourList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList hourlyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getHourList().iterator(); j.hasNext();) {
                Integer hour = (Integer) j.next();
                cal.set(Calendar.HOUR_OF_DAY, hour.intValue());
                hourlyDates.add(new Date(cal.getTime().getTime()));
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty()
                && getMinuteList().isEmpty()
                && getSecondList().isEmpty()) {
            return applySetPosRules(hourlyDates);
        }
        return hourlyDates;
    }
    
    /**
     * Applies BYMINUTE rules specified in this Recur instance to the
     * specified date list. If no BYMINUTE rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getMinuteVariants(final DateList dates) {
        if (getMinuteList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList minutelyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getMinuteList().iterator(); j.hasNext();) {
                Integer minute = (Integer) j.next();
                cal.set(Calendar.MINUTE, minute.intValue());
                minutelyDates.add(new Date(cal.getTime().getTime()));
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty() && getSecondList().isEmpty()) {
            return applySetPosRules(minutelyDates);
        }
        return minutelyDates;
    }
    
    /**
     * Applies BYSECOND rules specified in this Recur instance to the
     * specified date list. If no BYSECOND rules are specified the
     * date list is returned unmodified.
     * @param dates
     * @return
     */
    private DateList getSecondVariants(final DateList dates) {
        if (getSecondList().isEmpty()) {
            return dates;
        }
        Calendar cal = Calendar.getInstance();
        DateList secondlyDates = new DateList(dates.getType());
        for (Iterator i = dates.iterator(); i.hasNext();) {
            Date date = (Date) i.next();
            cal.setTime(date);
            for (Iterator j = getSecondList().iterator(); j.hasNext();) {
                Integer second = (Integer) j.next();
                cal.set(Calendar.SECOND, second.intValue());
                secondlyDates.add(new Date(cal.getTime().getTime()));
            }
        }
        // apply BYSETPOS rules..
        if (!getSetPosList().isEmpty()) {
            return applySetPosRules(secondlyDates);
        }
        return secondlyDates;
    }
    
    /**
     * @param count The count to set.
     */
    public final void setCount(final int count) {
        this.count = count;
        this.until = null;
    }
    
    /**
     * @param frequency The frequency to set.
     */
    public final void setFrequency(final String frequency) {
        this.frequency = frequency;
    }
    
    /**
     * @param interval The interval to set.
     */
    public final void setInterval(final int interval) {
        this.interval = interval;
    }
    
    /**
     * @param until The until to set.
     */
    public final void setUntil(final Date until) {
        this.until = until;
        this.count = -1;
    }
}
