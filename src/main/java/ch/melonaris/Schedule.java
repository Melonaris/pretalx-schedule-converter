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

    public Schedule(ArrayList<Talk> talks, ArrayList<Room> rooms, ArrayList<Speaker> speakers, ZoneId timezone, LocalDate eventStart, LocalDate eventEnd) {
        this.talks = talks;
        this.rooms = rooms;
        this.speakers = speakers;
        this.timezone = timezone;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.numberOfDays = getDayNumber();
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

    public Schedule getScheduleOfDays(int... dayNumbers) {
        ArrayList<Talk> talksOfDay = new ArrayList<>();

        for (Talk talk : talks) {
            for (int day : dayNumbers) {
                if (talk.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).equals(eventStart.plusDays(day - 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                    talksOfDay.add(talk);
                }
            }
        }
        return new Schedule(talksOfDay, this.rooms, this.speakers, this.timezone, this.eventStart, this.eventEnd);
    }

    public Schedule getScheduleOfRooms(String... rooms) {
        ArrayList<Talk> talksOfRooms = new ArrayList<>();
        ArrayList<Room> roomsOfSchedule = new ArrayList<>();
        Pattern roomNumberFormat = Pattern.compile("^\\d+$");
        Matcher roomNumber;

        for (Talk talk : talks) {
            for (String room : rooms) {
                roomNumber = roomNumberFormat.matcher(room);

                if (roomNumber.find()) {
                    if (talk.getRoom() == Integer.parseInt(room)) {
                        talksOfRooms.add(talk);
                        roomsOfSchedule.add(getRoom(Integer.parseInt(room)));
                    }
                } else {
                    if (talk.getRoom() == Objects.requireNonNull(getRoom(room)).getId()) {
                        talksOfRooms.add(talk);
                        roomsOfSchedule.add(getRoom(room));
                    }
                }
            }
        }

        return new Schedule(talksOfRooms, roomsOfSchedule, this.speakers, this.timezone, this.eventStart, this.eventEnd);
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
