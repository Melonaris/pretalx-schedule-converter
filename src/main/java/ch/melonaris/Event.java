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

public class Event {
    private ArrayList<Talk> talks;
    private ArrayList<Room> rooms;
    private ArrayList<Speaker> speakers;
    private ZoneId timezone;
    private LocalDate eventStart;
    private LocalDate eventEnd;
    private int numberOfDays;

    @JsonCreator
    public Event(@JsonProperty("talks") ArrayList<Talk> talks, @JsonProperty("rooms") ArrayList<Room> rooms, @JsonProperty("speakers") ArrayList<Speaker> speakers, @JsonProperty("timezone") String timezone, @JsonProperty("event_start") String eventStart, @JsonProperty("event_end") String eventEnd) {
        this.talks = talks;
        this.talks.sort(null);
        this.rooms = rooms;
        this.speakers = speakers;
        this.timezone = ZoneId.of(timezone);
        this.eventStart = LocalDate.parse(eventStart, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.eventEnd = LocalDate.parse(eventEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.numberOfDays = getDayNumber();
    }

    public Event(String timezone, String eventStart, String eventEnd) {
        new Event(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), timezone, eventStart, eventEnd);
    }

    public Event(ArrayList<Talk> talks, ArrayList<Room> rooms, ArrayList<Speaker> speakers, ZoneId timezone, LocalDate eventStart, LocalDate eventEnd) {
        this.talks = talks;
        this.rooms = rooms;
        this.speakers = speakers;
        this.timezone = timezone;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.numberOfDays = getDayNumber();
    }

    public static Event loadData(String dataToLoad) {
        File file = new File(dataToLoad);

        if (file.exists()) {
            return loadDataFromFile(dataToLoad);
        } else {
            return loadDataFromJsonString(dataToLoad);
        }
    }

    public static Event loadDataFromFile(String filePathString) {
        ObjectMapper objectMapper = new ObjectMapper();

        Pattern fileEndingPattern = Pattern.compile(".*\\.json");
        Matcher fileEnding = fileEndingPattern.matcher(filePathString);

        if (!fileEnding.find()) {
            filePathString += ".json";
        }

        File filePath = new File(filePathString);

        try {
            return objectMapper.readValue(filePath, Event.class);
        } catch (IOException e) {
            System.out.println("Error: Runtime Exception!");
            return null;
        }
    }

    private static Event loadDataFromJsonString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, Event.class);
        } catch (IOException e) {
            System.out.println("Error: IO Exception!");
            return null;
        }
    }

    public Event getScheduleOfDays(int... dayNumbers) {
        ArrayList<Talk> talksOfDay = new ArrayList<>();

        for (Talk talk : talks) {
            for (int day : dayNumbers) {
                if (talk.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).equals(eventStart.plusDays(day - 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                    talksOfDay.add(talk);
                }
            }
        }

        talksOfDay.sort(null);
        return new Event(talksOfDay, this.rooms, this.speakers, this.timezone, this.eventStart, this.eventEnd);
    }

    public Event getScheduleOfRooms(String... rooms) {
        ArrayList<Talk> talksOfRooms = new ArrayList<>();
        ArrayList<Room> roomsOfSchedule = new ArrayList<>();
        Room room;
        Pattern roomNumberFormat = Pattern.compile("^\\d+$");
        Matcher roomNumber;


        for (Talk talk : talks) {
            for (String roomIdentifier : rooms) {
                roomNumber = roomNumberFormat.matcher(roomIdentifier);

                if (roomNumber.find()) {
                    if (talk.getRoom() == Integer.parseInt(roomIdentifier)) {
                        talksOfRooms.add(talk);
                        room = getRoom(Integer.parseInt(roomIdentifier));

                        if (!roomsOfSchedule.contains(room)) {
                            roomsOfSchedule.add(room);
                        }
                    }
                } else {
                    if (talk.getRoom() == Objects.requireNonNull(getRoom(roomIdentifier)).getId()) {
                        talksOfRooms.add(talk);
                        room = getRoom(roomIdentifier);

                        if (!roomsOfSchedule.contains(room)) {
                            roomsOfSchedule.add(room);
                        }
                    }
                }
            }
        }

        talksOfRooms.sort(null);
        return new Event(talksOfRooms, roomsOfSchedule, this.speakers, this.timezone, this.eventStart, this.eventEnd);
    }

    public void printDiscordTimestamps() {
        long startTime, endTime;

        for (Talk talk : talks) {
            startTime = talk.getStart().toInstant().toEpochMilli() / 1000;
            endTime = talk.getEnd().toInstant().toEpochMilli() / 1000;

            System.out.printf("<t:%d:t> to <t:%d:t> %s%n", startTime, endTime, talk.getTitle());
        }
    }

    private Room getRoom(int roomID) {
        for (Room room : rooms) {
            if (room.getId() == roomID) {
                return room;
            }
        }
        return null;
    }

    private Room getRoom(String roomName) {
        java.util.Scanner scanner = Scanner.getScanner();
        ArrayList<Room> matchingRooms = new ArrayList<>();
        Pattern containsRoomNameFormat;
        Matcher containsRoomName;

        do {
            containsRoomNameFormat = Pattern.compile(".*" + roomName + ".*");

            for (Room room : rooms) {
                containsRoomName = containsRoomNameFormat.matcher(room.getName());
                if (containsRoomName.find()) {
                    matchingRooms.add(room);
                }
            }

            if (matchingRooms.size() == 1) {
                break;
            }

            if (matchingRooms.size() > 2) {
                System.out.println("Error: Multiple matches found one expected!");
            }

            if (matchingRooms.isEmpty()) {
                System.out.println("Error: No match found one expected!");
            }

            System.out.println("Re-enter room name:");
            matchingRooms.clear();
            roomName = scanner.nextLine();
        } while (true);

        return matchingRooms.getFirst();
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
