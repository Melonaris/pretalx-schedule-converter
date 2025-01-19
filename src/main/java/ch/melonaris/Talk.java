package ch.melonaris;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class Talk implements Comparable<Talk> {
    private String code;
    private int id;
    private String title;
    private String description;
    private ArrayList<String> speakers;
    private ZonedDateTime start;
    private LocalTime localStart;
    private ZonedDateTime end;
    private LocalTime localEnd;
    private int room;
    private int duration;

    public Talk(@JsonProperty("code") String code,
                @JsonProperty("id") int id,
                @JsonProperty("title") String title,
                @JsonProperty("abstract") String description,
                @JsonProperty("speakers") ArrayList<String> speakers,
                @JsonProperty("start") String start,
                @JsonProperty("end") String end,
                @JsonProperty("room") int room,
                @JsonProperty("duration") int duration) {
        this.code = code;
        this.id = id;
        this.title = title;
        this.description = description;
        this.speakers = speakers;
        this.start = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
        this.localStart = LocalTime.parse(this.start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        this.end = ZonedDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
        this.localEnd = LocalTime.parse(this.end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        this.room = room;
        this.duration = duration;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(ArrayList<String> speakers) {
        this.speakers = speakers;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);;
    }

    @JsonIgnore
    public LocalTime getLocalStart() {
        return localStart;
    }

    @JsonIgnore
    public void setLocalStart(String localStart) {
        this.localStart = LocalTime.parse(LocalTime.parse(localStart).format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = ZonedDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);;
    }

    @JsonIgnore
    public LocalTime getLocalEnd() {
        return localEnd;
    }

    @JsonIgnore
    public void setLocalEnd(String localEnd) {
        this.localEnd = LocalTime.parse(LocalTime.parse(localEnd).format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(Talk that) {
        if (this.getStart().isBefore(that.getStart())) {
            return -1;
        } else if (this.getStart().isAfter(that.getStart())) {
            return 1;
        }
        if (this.getEnd().isBefore(that.getEnd())) {
            return -1;
        } else if (this.getEnd().isAfter(that.getEnd())){
            return 1;
        }
        return 0;
    }
}
