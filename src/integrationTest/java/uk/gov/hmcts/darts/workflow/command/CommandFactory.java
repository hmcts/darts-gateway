package uk.gov.hmcts.darts.workflow.command;

import com.service.mojdarts.synapps.com.addaudio.Audio;

import java.io.File;
import java.math.BigInteger;

public final class CommandFactory {

    private  CommandFactory() {

    }

    public static Audio.Start getStart(AddAudioDate date, AddAudioTime time) {
        Audio.Start start = new Audio.Start();
        start.setD(date.getDay());
        start.setM(date.getMonth());
        start.setY(date.getYear());
        start.setH(time.getHour());
        start.setM(time.getMinute());
        start.setS(time.getSecond());
        return start;
    }

    public static Audio.End getEnd(AddAudioIntegerDate date, AddAudioIntegerTime time) {
        Audio.End start = new Audio.End();
        start.setD(date.getDay());
        start.setM(date.getMonth());
        start.setY(date.getYear());
        start.setH(time.getHour());
        start.setM(time.getMinute());
        start.setS(time.getSecond());
        return start;
    }

    public static AddAudioMidTierCommand getAudioCommand(String ipadddress, File mediaPayload, String username, String password) {
        Audio audio = new Audio();
        Audio.Start start = getStart(new AddAudioDate("23", "10", "2023"), new AddAudioTime("09", "00", "00"));

        Audio.End end = getEnd(new AddAudioIntegerDate(BigInteger.valueOf(23), BigInteger.valueOf(10),
                BigInteger.valueOf(2023)),
                new AddAudioIntegerTime(BigInteger.valueOf(10), BigInteger.valueOf(0), BigInteger.valueOf(0)));

        audio.setStart(start);
        audio.setEnd(end);

        audio.setChannel(BigInteger.valueOf(0));
        audio.setMaxChannels(BigInteger.valueOf(2));
        audio.setMediaformat("mp2");
        audio.setCourthouse("CARDIFF");
        audio.setCourtroom("1");

        Audio.CaseNumbers numbers = new Audio.CaseNumbers();
        audio.setCaseNumbers(numbers);

        return new AddAudioMidTierCommand(ipadddress, audio, mediaPayload, username, password);
    }

    public static AddAudioMidTierCommand getAudioCommand(String ipadddress, File xmlPayload, File mediaPayload, String username, String password) {
        return new AddAudioMidTierCommand(ipadddress, xmlPayload, mediaPayload, username, password);
    }
}