package ch.melonaris;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// replace abstract with description!!!

public class Schedule {
    private ArrayList<Talk> talks;
    private ArrayList<Room> rooms;
    private ArrayList<Speaker> speakers;
    private ZoneId timezone;
    private LocalDate eventStart;
    private LocalDate eventEnd;
    private int numberOfDays;

    @JsonCreator
    public Schedule(@JsonProperty("talks") ArrayList<Talk> talks,
                    @JsonProperty("rooms") ArrayList<Room> rooms,
                    @JsonProperty("speakers") ArrayList<Speaker> speakers,
                    @JsonProperty("timezone") String timezone,
                    @JsonProperty("event_start") String eventStart,
                    @JsonProperty("event_end") String eventEnd) {
        this.talks = talks;
        this.rooms = rooms;
        this.speakers = speakers;
        this.timezone = ZoneId.of(timezone);
        this.eventStart = LocalDate.parse(eventStart, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.eventEnd = LocalDate.parse(eventEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.numberOfDays = getDayNumber();
    }

    public Schedule(String timezone, String eventStart, String eventEnd) {
        new Schedule(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), timezone, eventStart, eventEnd);
    }

    public static Schedule loadData(String dataToLoad) {
        File file = new File(dataToLoad);

        if (file.exists()) {
            return loadDataFromFile(dataToLoad);
        } else {
            return loadDataFromJsonString(dataToLoad);
        }
    }

    public static Schedule loadDataFromFile(String filePathString) {
        ObjectMapper objectMapper = new ObjectMapper();

        Pattern fileEndingPattern = Pattern.compile(".*\\.json");
        Matcher fileEnding = fileEndingPattern.matcher(filePathString);

        if (!fileEnding.find()) {
            filePathString += ".json";
        }

        File filePath = new File(filePathString);

        try {
            return objectMapper.readValue(filePath, Schedule.class);
        } catch (IOException e) {
            System.out.println("Error: Runtime Exception!");
            return null;
        }
    }

    private static Schedule loadDataFromJsonString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, Schedule.class);
        } catch (IOException e) {
            System.out.println("Error: IO Exception!");
            return null;
        }
    }

    public ArrayList<Talk> getTalksOfDays(int ... dayNumbers) {
        ArrayList<Talk> talksOfDay = new ArrayList<>();

        for (Talk talk : talks) {
            for (int day : dayNumbers) {
                if (talk.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).equals(eventStart.plusDays(day - 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                    talksOfDay.add(talk);
                }
            }
        }
        return talksOfDay;
    }

    public ArrayList<Talk> getTalksInRooms(String ... rooms) {
        ArrayList<Talk> talksOfRooms = new ArrayList<>();
        Pattern roomNumberFormat = Pattern.compile("^\\d+$");
        Matcher roomNumber;

        for (Talk talk : talks) {
            for (String room : rooms) {
                roomNumber = roomNumberFormat.matcher(room);

                if (roomNumber.find()) {
                    if (talk.getRoom() == Integer.parseInt(room)) {
                        talksOfRooms.add(talk);
                    }
                } else {
                    if (talk.getRoom() == Objects.requireNonNull(getRoom(room)).getId()) {
                        talksOfRooms.add(talk);
                    }
                }
            }
        }
        return talksOfRooms;
    }

    private Room getRoom(String roomName) {
        Pattern containsRoomNameFormat = Pattern.compile(".*" + roomName + ".*");
        Matcher containsRoomName;

        // add check if multiple are true

        for (Room room : rooms) {
            containsRoomName = containsRoomNameFormat.matcher(room.getName());
            if (containsRoomName.find()) {
                return room;
            }
        }
        return null;
    }

    private int getDayNumber() {
        ArrayList<String> days = new ArrayList<>();

        for (Talk talk : talks) {
            String date = talk.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (!days.contains(date)) {
                days.add(date);
            }
        }
        return days.size();
    }
}
